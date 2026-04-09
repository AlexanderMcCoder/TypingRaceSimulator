import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * StatsPanel displays detailed performance statistics for each typist.
 * Shows personal bests, race history, and a side-by-side comparison view.
 *
 * @author Thanh Son Nguyen
 * @version 1
 */
public class StatsPanel extends JPanel
{
    // Reference to main GUI for switching screens
    private TypingRaceGUI gui;

    // --- Personal bests (stored across races) ---
    private int[] bestWPM;
    private int[] bestAccuracyPercent;
    private int[] totalRaces;
    private int[] totalWins;
    private int[] totalBurnouts;

    // --- Race history (last 5 races per typist) ---
    // Stores WPM values for each of the last 5 races
    private int[][] wpmHistory;      // wpmHistory[typistIndex][raceIndex]
    private int[] historyCount;      // how many races recorded so far

    // --- Current race data (passed from ResultsPanel) ---
    private Typist[] typists;
    private int[] currentWPM;
    private int[] currentAccuracy;
    private int[] currentBurnouts;

    // --- UI panels rebuilt each time ---
    private JPanel contentPanel;

    /**
     * Constructor - builds the stats screen layout.
     *
     * @param gui the main TypingRaceGUI window
     */
    public StatsPanel(TypingRaceGUI gui)
    {
        this.gui = gui;
        setBackground(new Color(20, 20, 20));
        setLayout(new BorderLayout());

        // Initialise storage for up to 3 typists, 5 races of history
        bestWPM = new int[3];
        bestAccuracyPercent = new int[3];
        totalRaces = new int[3];
        totalWins = new int[3];
        totalBurnouts = new int[3];
        wpmHistory = new int[3][5];
        historyCount = new int[3];

        // Title
        JLabel titleLabel = new JLabel("STATISTICS", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 200, 255));
        add(titleLabel, BorderLayout.NORTH);

        // Content panel rebuilt on refresh
        contentPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        contentPanel.setBackground(new Color(20, 20, 20));
        add(contentPanel, BorderLayout.CENTER);

        // Back button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(20, 20, 20));

        JButton backButton = new JButton("BACK TO RESULTS");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(0, 150, 255));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> gui.showScreen(TypingRaceGUI.RESULTS_SCREEN));

        JButton compareButton = new JButton("COMPARE TYPISTS");
        compareButton.setFont(new Font("Arial", Font.BOLD, 14));
        compareButton.setBackground(new Color(255, 165, 0));
        compareButton.setForeground(Color.WHITE);
        compareButton.setFocusPainted(false);
        compareButton.addActionListener(e -> showComparisonView());

        buttonPanel.add(backButton);
        buttonPanel.add(compareButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Updates the stats panel with the latest race data.
     * Called by ResultsPanel before switching to this screen.
     *
     * @param typists          the typists who raced
     * @param wpmValues        WPM achieved by each typist
     * @param accuracyPercents accuracy percentage for each typist
     * @param burnoutCounts    burnout count for each typist
     */
    public void refresh(Typist[] typists, int[] wpmValues,
                        int[] accuracyPercents, int[] burnoutCounts)
    {
        this.typists = typists;
        this.currentWPM = wpmValues;
        this.currentAccuracy = accuracyPercents;
        this.currentBurnouts = burnoutCounts;

        // Update personal bests and history for each typist
        for (int i = 0; i < typists.length; i++)
        {
            totalRaces[i]++;
            totalBurnouts[i] += burnoutCounts[i];

            // Update best WPM
            if (wpmValues[i] > bestWPM[i])
            {
                bestWPM[i] = wpmValues[i];
            }

            // Update best accuracy
            if (accuracyPercents[i] > bestAccuracyPercent[i])
            {
                bestAccuracyPercent[i] = accuracyPercents[i];
            }

            // Store WPM in history — shift old values left to keep last 5
            if (historyCount[i] < 5)
            {
                wpmHistory[i][historyCount[i]] = wpmValues[i];
                historyCount[i]++;
            }
            else
            {
                // Shift history left by one to make room for new entry
                for (int j = 0; j < 4; j++)
                {
                    wpmHistory[i][j] = wpmHistory[i][j + 1];
                }
                wpmHistory[i][4] = wpmValues[i];
            }
        }

        buildContent();
    }

    /**
     * Builds the main stats content — one column per typist.
     */
    private void buildContent()
    {
        // removeAll() clears existing components so the panel can be rebuilt
        contentPanel.removeAll();

        if (typists == null) return;

        for (int i = 0; i < typists.length; i++)
        {
            JPanel typistPanel = new JPanel();
            // BoxLayout stacks components vertically
            typistPanel.setLayout(new BoxLayout(typistPanel, BoxLayout.Y_AXIS));
            typistPanel.setBackground(new Color(35, 35, 35));
            typistPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 200, 255)));

            // Typist name header
            JLabel nameLabel = new JLabel(typists[i].getName());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
            nameLabel.setForeground(new Color(0, 200, 255));
            typistPanel.add(nameLabel);
            typistPanel.add(Box.createVerticalStrut(10));

            // Current race stats
            typistPanel.add(makeSectionLabel("This Race:"));
            typistPanel.add(makeStatLabel("WPM: " + currentWPM[i]));
            typistPanel.add(makeStatLabel("Accuracy: " + currentAccuracy[i] + "%"));
            typistPanel.add(makeStatLabel("Burnouts: " + currentBurnouts[i]));
            typistPanel.add(Box.createVerticalStrut(8));

            // Personal bests
            typistPanel.add(makeSectionLabel("Personal Bests:"));
            typistPanel.add(makeStatLabel("Best WPM: " + bestWPM[i]));
            typistPanel.add(makeStatLabel("Best Accuracy: " + bestAccuracyPercent[i] + "%"));
            typistPanel.add(Box.createVerticalStrut(8));

            // Overall stats
            typistPanel.add(makeSectionLabel("Overall:"));
            typistPanel.add(makeStatLabel("Races: " + totalRaces[i]));
            typistPanel.add(makeStatLabel("Total Burnouts: " + totalBurnouts[i]));
            typistPanel.add(Box.createVerticalStrut(8));

            // WPM history
            typistPanel.add(makeSectionLabel("WPM History:"));
            for (int j = 0; j < historyCount[i]; j++)
            {
                typistPanel.add(makeStatLabel("Race " + (j + 1) + ": " + wpmHistory[i][j] + " WPM"));
            }

            contentPanel.add(typistPanel);
        }

        // revalidate() redoes the layout after components are added
        revalidate();
        // repaint() redraws the panel visually
        repaint();
    }

    /**
     * Shows a comparison view of all typists side by side in a dialog.
     * Displays WPM, accuracy and burnouts for quick comparison.
     */
    private void showComparisonView()
    {
        if (typists == null) return;

        // StringBuilder builds the comparison text piece by piece efficiently
        StringBuilder sb = new StringBuilder();
        sb.append("=== TYPIST COMPARISON ===\n\n");

        // String.format formats text into fixed-width columns for alignment
        sb.append(String.format("%-20s %8s %10s %10s\n","Name", "WPM", "Accuracy", "Burnouts"));
        sb.append("--------------------------------------------------\n");

        for (int i = 0; i < typists.length; i++)
        {
            sb.append(String.format("%-20s %8d %9d%% %10d\n",
                typists[i].getName(),
                currentWPM[i],
                currentAccuracy[i],
                currentBurnouts[i]));
        }

        sb.append("\n=== PERSONAL BESTS ===\n\n");
        sb.append(String.format("%-20s %8s %10s\n", "Name", "Best WPM", "Best Acc"));
        sb.append("------------------------------------------\n");

        for (int i = 0; i < typists.length; i++)
        {
            sb.append(String.format("%-20s %8d %9d%%\n",
                typists[i].getName(),
                bestWPM[i],
                bestAccuracyPercent[i]));
        }

        // JTextArea inside JScrollPane for a scrollable comparison dialog
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Courier New", Font.PLAIN, 13));

        // setEditable(false) prevents the user from typing in the text area
        textArea.setEditable(false);
        textArea.setBackground(new Color(30, 30, 30));
        textArea.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(textArea);
        // setPreferredSize fixes the size of the dialog popup
        scrollPane.setPreferredSize(new Dimension(500, 300));

        // JOptionPane shows a simple popup dialoge
        JOptionPane.showMessageDialog(this, scrollPane,"Typist Comparison", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Helper method to create a bold section title label.
     */
    private JLabel makeSectionLabel(String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(Color.WHITE);
        return label;
    }

    /**
     * Helper method to create a small grey stat label.
     */
    private JLabel makeStatLabel(String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }
}
