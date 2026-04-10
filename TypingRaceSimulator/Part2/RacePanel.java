import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * RacePanel displays the live typing race.
 * It shows each typist's cursor moving through the passage text,
 * handles the game loop using a Swing Timer, and sends results
 * sends data to ResultsPanel when the race ends.
 *
 * @author Thanh Son Nguyen
 * @version 1
 */
public class RacePanel extends JPanel
{
    // Reference to main GUI for switching screens
    private TypingRaceGUI gui;

    // --- Race data ---
    private Typist[] typists;
    private String passage;
    private int passageLength;

    // --- Difficulty modifiers ---
    private boolean autocorrect;
    private boolean caffeineMode;
    private boolean[] wristSupport;
    private boolean[] energyDrink;
    private boolean[] headphones;

    // --- Race mechanics constants ---
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int SLIDE_BACK_AMOUNT = 2;
    private static final int BURNOUT_DURATION = 3;

    // --- Race state ---
    private int turnCount;
    private boolean raceFinished;
    private int[] burnoutCounts;   // how many times each typist burnt out
    private int[] mistypeCounts;   // how many times each typist mistyped
    private boolean[] justMistyped; // tracks if typist just mistyped this turn

    // --- Swing Timer for the game loop ---
    // Timer fires every 300ms and calls gameTick() to advance the race by one turn
    private Timer gameTimer;

    // --- UI panels for each typist lane ---
    private JPanel[] lanePanels;
    private JLabel[] passageLabels;   // shows the passage text with cursor
    private JLabel[] statusLabels;    // shows name, accuracy, burnout info
    private JLabel turnLabel;         // shows current turn number

    /**
     * Constructor - builds the race screen layout.
     *
     * @param gui the main TypingRaceGUI window
     */
    public RacePanel(TypingRaceGUI gui)
    {
        this.gui = gui;
        setBackground(new Color(20, 20, 20));
        setLayout(new BorderLayout());

        // Title bar at top
        JLabel titleLabel = new JLabel("RACE IN PROGRESS", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 255, 100));
        add(titleLabel, BorderLayout.NORTH);

        // Turn counter
        turnLabel = new JLabel("Turn: 0", JLabel.CENTER);
        turnLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        turnLabel.setForeground(Color.LIGHT_GRAY);

        // Centre panel holds all the typist lanes
        JPanel centrePanel = new JPanel(new GridLayout(0, 1, 5, 5));
        centrePanel.setBackground(new Color(20, 20, 20));

        // We support up to 3 typists — lanes created dynamically in setupRace()
        lanePanels = new JPanel[3];
        passageLabels = new JLabel[3];
        statusLabels = new JLabel[3];

        for (int i = 0; i < 3; i++)
        {
            // Each lane is a panel with BorderLayout
            lanePanels[i] = new JPanel(new BorderLayout());
            lanePanels[i].setBackground(new Color(35, 35, 35));
            lanePanels[i].setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

            // Passage label shows the text with the typist's cursor position highlighted
            passageLabels[i] = new JLabel();
            passageLabels[i].setFont(new Font("Courier New", Font.PLAIN, 14));
            passageLabels[i].setForeground(Color.WHITE);
            // setOpaque(true) makes the label's background colour visible
            passageLabels[i].setOpaque(true);
            passageLabels[i].setBackground(new Color(35, 35, 35));

            // Status label shows name, accuracy, burnout info on the right
            statusLabels[i] = new JLabel();
            statusLabels[i].setFont(new Font("Arial", Font.PLAIN, 13));
            statusLabels[i].setForeground(Color.LIGHT_GRAY);
            // setPreferredSize fixes the width so the status label does not resize during the race
            statusLabels[i].setPreferredSize(new Dimension(280, 40));

            lanePanels[i].add(passageLabels[i], BorderLayout.CENTER);
            lanePanels[i].add(statusLabels[i], BorderLayout.EAST);

            centrePanel.add(lanePanels[i]);
        }

        // Legend at the bottom
        JLabel legendLabel = new JLabel("  [~] = burnt out     [<] = just mistyped", JLabel.LEFT);
        legendLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        legendLabel.setForeground(Color.GRAY);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(20, 20, 20));
        bottomPanel.add(turnLabel, BorderLayout.NORTH);
        bottomPanel.add(legendLabel, BorderLayout.SOUTH);

        add(centrePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Sets up the race with the given typists and settings, then starts it.
     * Called by SetupPanel when the user clicks Start Race.
     *
     * @param typists      array of Typist objects to race
     * @param passage      the passage text to type
     * @param autocorrect  if true, slideBack amount is halved
     * @param caffeineMode if true, speed boost for first 10 turns then higher burnout risk
     * @param wristSupport per-typist wrist support accessory flag
     * @param energyDrink  per-typist energy drink accessory flag
     * @param headphones   per-typist headphones accessory flag
     */
    public void setupRace(Typist[] typists, String passage,
                          boolean autocorrect, boolean caffeineMode,
                          boolean[] wristSupport, boolean[] energyDrink,
                          boolean[] headphones)
    {
        this.typists = typists;
        this.passage = passage;
        this.passageLength = passage.length();
        this.autocorrect = autocorrect;
        this.caffeineMode = caffeineMode;
        this.wristSupport = wristSupport;
        this.energyDrink = energyDrink;
        this.headphones = headphones;

        // Reset race state
        turnCount = 0;
        raceFinished = false;
        burnoutCounts = new int[typists.length];
        mistypeCounts = new int[typists.length];
        justMistyped = new boolean[typists.length];

        // Reset each typist
        for (int i = 0; i < typists.length; i++)
        {
            typists[i].resetToStart();
        }

        // Hide unused lanes, show used ones
        for (int i = 0; i < 3; i++)
        {
            lanePanels[i].setVisible(i < typists.length);
        }

        // Start the game loop timer — fires every 300ms
        if (gameTimer != null && gameTimer.isRunning())
        {
            gameTimer.stop();
        }

        // Timer repeatedly calls gameTick() every 300ms to drive the race animation
        gameTimer = new Timer(300, e -> gameTick());
        gameTimer.start();
    }

    /**
     * One tick of the game loop — advances all typists by one turn,
     * updates the display, and checks if the race is finished.
     */
    private void gameTick()
    {
        if (raceFinished)
        {
            return;
        }

        turnCount++;

        // Advance each typist
        for (int i = 0; i < typists.length; i++)
        {
            justMistyped[i] = false;
            advanceTypist(i);
        }

        // Update the display
        updateDisplay();

        // Check if anyone has finished
        for (int i = 0; i < typists.length; i++)
        {
            if (typists[i].getProgress() >= passageLength)
            {
                raceFinished = true;
                gameTimer.stop();
                endRace(i);
                return;
            }
        }
    }

    /**
     * Advances a single typist by one turn.
     * Applies all modifiers — caffeine mode, energy drink, headphones, autocorrect.
     *
     * @param i the index of the typist in the typists array
     */
    private void advanceTypist(int i)
    {
        Typist t = typists[i];

        if (t.isBurntOut())
        {
            t.recoverFromBurnout();
            return;
        }

        // Caffeine mode — first 10 turns give a speed boost
        double accuracyBoost = 0.0;
        if (caffeineMode && turnCount <= 10)
        {
            accuracyBoost = 0.15;
        }

        // Energy drink — boost in first half of race, penalty in second half
        if (energyDrink[i])
        {
            if (t.getProgress() < passageLength / 2)
            {
                accuracyBoost += 0.05;
            }
            else
            {
                accuracyBoost -= 0.05;
            }
        }

        double effectiveAccuracy = t.getAccuracy() + accuracyBoost;
        if (effectiveAccuracy > 1.0)
        {
            effectiveAccuracy = 1.0;
        }
        if (effectiveAccuracy < 0.0)
        {
            effectiveAccuracy = 0.0;
        }

        // Attempt to type a character
        if (Math.random() < effectiveAccuracy)
        {
            t.typeCharacter();
        }

        // Mistype check — headphones reduce mistype chance
        double mistypeChance = effectiveAccuracy * MISTYPE_BASE_CHANCE;
        if (headphones[i])
        {
            mistypeChance -= 0.05;
        }
        if (mistypeChance < 0)
        {
            mistypeChance = 0;
        }

        if (Math.random() < mistypeChance)
        {
            // Autocorrect halves the slide back amount
            int slideAmount = SLIDE_BACK_AMOUNT;
            if (autocorrect)
            {
                slideAmount = SLIDE_BACK_AMOUNT / 2;
            }

            if (slideAmount < 1)
            {
                slideAmount = 1;
            }

            t.slideBack(slideAmount);
            justMistyped[i] = true;
            mistypeCounts[i]++;
        }

        // Burnout check — wrist support reduces burnout duration
        double burnoutChance = 0.05 * effectiveAccuracy * effectiveAccuracy;
        if (caffeineMode && turnCount > 10)
        {
            burnoutChance += 0.05; // caffeine crash
        }

        if (Math.random() < burnoutChance)
        {
            // Wrist support from accessory OR purchased upgrade reduces burnout duration
            int burnoutDuration = BURNOUT_DURATION;
            if (wristSupport[i] || gui.getHasWristSupport()[i])
            {
                burnoutDuration = BURNOUT_DURATION - 1;
            }
            if (burnoutDuration < 1)
            {
                burnoutDuration = 1;
            }

            t.burnOut(burnoutDuration);
            t.setAccuracy(t.getAccuracy() - 0.01);
            burnoutCounts[i]++;
        }
    }

    /**
     * Updates all the lane displays to show current progress.
     * Uses HTML in JLabel to colour the passage text — completed chars in green,
     * current cursor position highlighted, remaining chars in grey.
     */
    private void updateDisplay()
    {
        turnLabel.setText("Turn: " + turnCount);

        for (int i = 0; i < typists.length; i++)
        {
            Typist t = typists[i];
            int progress = t.getProgress();
            if (progress > passageLength)
            {
                progress = passageLength;
            }

            // StringBuilder builds the HTML string piece by piece efficiently
            // JLabel supports HTML text — wrapping in <html> tags for coloured characters
            StringBuilder sb = new StringBuilder("<html><body style='white-space:pre'><font face='Courier New'>");

            // Characters already typed — shown in green
            sb.append("<font color='#00CC44'>");
            sb.append(passage.substring(0, progress));
            sb.append("</font>");

            // Current cursor character — highlighted in yellow
            if (progress < passageLength)
            {
                sb.append("<font color='#000000' style='background-color:#FFFF00'>");
                sb.append(passage.charAt(progress) == ' ' ? " " : passage.charAt(progress));
                sb.append("</font>");

                // Remaining characters — shown in grey
                sb.append("<font color='#888888'>");
                sb.append(passage.substring(progress + 1));
                sb.append("</font>");
            }

            sb.append("</font></body></html>");
            passageLabels[i].setText(sb.toString());

            // Status label on the right
            String status = t.getName() + "  (Acc: " + t.getAccuracy() + ")";
            if (t.isBurntOut())
            {
                status += "  BURNT OUT (" + t.getBurnoutTurnsRemaining() + ")";
                statusLabels[i].setForeground(new Color(255, 100, 100));
            }
            else if (justMistyped[i])
            {
                status += "  [<] mistyped";
                statusLabels[i].setForeground(new Color(255, 200, 0));
            }
            else
            {
                statusLabels[i].setForeground(Color.LIGHT_GRAY);
            }
            statusLabels[i].setText(status);
        }
    }

    /**
     * Called when a typist finishes the race.
     * Improves winner accuracy, calculates WPM, then sends results to ResultsPanel.
     *
     * @param winnerIndex the index of the winning typist
     */
    private void endRace(int winnerIndex)
    {
        // Improve winner accuracy
        Typist winner = typists[winnerIndex];
        double oldAccuracy = winner.getAccuracy();
        winner.setAccuracy(oldAccuracy + 0.02);

        // Calculate WPM for each typist
        // WPM = (characters typed / 5) / (turns * 0.3 seconds / 60)
        // Standard WPM treats 5 characters as one word
        double timeInMinutes = (turnCount * 0.3) / 60.0;
        int[] wpmValues = new int[typists.length];
        for (int i = 0; i < typists.length; i++)
        {
            int charsTyped = typists[i].getProgress();
            if (timeInMinutes > 0)
            {
                wpmValues[i] = (int) ((charsTyped / 5.0) / timeInMinutes);
            }
        }

        // Calculate accuracy percentage for each typist
        // accuracy % = correct chars / (correct chars + mistypes) * 100
        int[] accuracyPercents = new int[typists.length];
        for (int i = 0; i < typists.length; i++)
        {
            int correct = typists[i].getProgress();
            int total   = correct + mistypeCounts[i];
            if (total > 0)
            {
                accuracyPercents[i] = (int) ((correct / (double) total) * 100);
            }
            else
            {
                accuracyPercents[i] = 100;
            }
        }

        // Send results to ResultsPanel
        gui.getResultsPanel().showResults(
            typists, winnerIndex, wpmValues,
            accuracyPercents, burnoutCounts,
            oldAccuracy, turnCount
        );

        // Switch to results screen after a short delay
        // Timer with setRepeats(false) fires once after 1 second then stops automatically
        Timer delay = new Timer(1000, e -> gui.showScreen(TypingRaceGUI.RESULTS_SCREEN));
        delay.setRepeats(false);
        delay.start();
    }
}
