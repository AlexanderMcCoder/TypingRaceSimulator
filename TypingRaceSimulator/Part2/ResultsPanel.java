import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ResultsPanel displays the post-race summary.
 * Shows the winner, WPM, accuracy percentage, burnout count,
 * accuracy changes, leaderboard points (obeyingOption A), and
 * sponsor bonuses (Option B) for each typist.
 * each sponsor has a condition and bonus amount
 * KeyCorp: rewards no burnouts
 * TypeTech: rewards high WPM
 * SpeedKeys: first place bonus
 *
 * @author Thanh Son Nguyen
 * @version 1
 */
public class ResultsPanel extends JPanel
{
    // Reference to main GUI for switching screens
    private TypingRaceGUI gui;

    // --- Results data ---
    private Typist[] typists;
    private int winnerIndex;
    private int[] wpmValues;
    private int[] accuracyPercents;
    private int[] burnoutCounts;
    private double oldWinnerAccuracy;
    private int turnCount;

    // --- Leaderboard points (Option A) ---
    // Cumulative points stored across races
    private int[] cumulativePoints;
    private int[] raceCount;
    private int[] winsWithoutBurnout; // tracks consecutive burnout-free wins for badges

    // --- Sponsor system (Option B) ---
    // Each typist has a sponsor with a condition and bonus amount
    private static final String[] SPONSOR_NAMES = {
        "KeyCorp", "TypeTech", "SpeedKeys"
    };
    private static final String[] SPONSOR_CONDITIONS = {
        "Finish without a single burnout: +50 coins",
        "Finish with WPM above 40: +30 coins",
        "Finish in first place: +40 coins"
    };
    private int[] totalEarnings; // cumulative earnings across races

    // --- UI labels updated each race ---
    private JLabel winnerLabel;
    private JPanel statsPanel;
    private JPanel leaderboardPanel;
    private JPanel sponsorPanel;

    /**
     * Constructor - builds the results screen layout.
     *
     * @param gui the main TypingRaceGUI window
     */
    public ResultsPanel(TypingRaceGUI gui)
    {
        this.gui = gui;
        setBackground(new Color(20, 20, 20));
        setLayout(new BorderLayout());

        // Initialise persistent data for up to 3 typists
        cumulativePoints = new int[3];
        raceCount = new int[3];
        winsWithoutBurnout = new int[3];
        totalEarnings = new int[3];

        // Winner banner at the top
        winnerLabel = new JLabel("", JLabel.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        winnerLabel.setForeground(new Color(255, 215, 0));
        add(winnerLabel, BorderLayout.NORTH);

        // Main content area split into three sections
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        contentPanel.setBackground(new Color(20, 20, 20));

        // Stats section (left)
        statsPanel = new JPanel();
        // BoxLayout stacks components vertically inside the panel
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(new Color(35, 35, 35));
        statsPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 200, 255)));

        // Leaderboard section (middle)
        leaderboardPanel = new JPanel();
        // BoxLayout stacks components vertically inside the panel
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
        leaderboardPanel.setBackground(new Color(35, 35, 35));
        leaderboardPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0)));

        // Sponsor section (right)
        sponsorPanel = new JPanel();
        // BoxLayout stacks components vertically inside the panel
        sponsorPanel.setLayout(new BoxLayout(sponsorPanel, BoxLayout.Y_AXIS));
        sponsorPanel.setBackground(new Color(35, 35, 35));
        sponsorPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 100)));

        contentPanel.add(statsPanel);
        contentPanel.add(leaderboardPanel);
        contentPanel.add(sponsorPanel);
        add(contentPanel, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(20, 20, 20));

        JButton raceAgainButton = new JButton("RACE AGAIN");
        raceAgainButton.setFont(new Font("Arial", Font.BOLD, 14));
        raceAgainButton.setBackground(new Color(0, 200, 100));
        raceAgainButton.setForeground(Color.WHITE);
        raceAgainButton.setFocusPainted(false);
        raceAgainButton.addActionListener(e -> gui.showScreen(TypingRaceGUI.SETUP_SCREEN));

        JButton statsButton = new JButton("VIEW STATS");
        statsButton.setFont(new Font("Arial", Font.BOLD, 14));
        statsButton.setBackground(new Color(0, 150, 255));
        statsButton.setForeground(Color.WHITE);
        statsButton.setFocusPainted(false);
        statsButton.addActionListener(e -> {
            gui.getStatsPanel().refresh(typists, wpmValues, accuracyPercents, burnoutCounts);
            gui.showScreen(TypingRaceGUI.STATS_SCREEN);
        });

        JButton leaderboardButton = new JButton("LEADERBOARD");
        leaderboardButton.setFont(new Font("Arial", Font.BOLD, 14));
        leaderboardButton.setBackground(new Color(255, 165, 0));
        leaderboardButton.setForeground(Color.WHITE);
        leaderboardButton.setFocusPainted(false);
        leaderboardButton.addActionListener(e -> {
            gui.getLeaderboardPanel().refresh(typists, cumulativePoints, totalEarnings, raceCount);
            gui.showScreen(TypingRaceGUI.LEADERBOARD_SCREEN);
        });

        buttonPanel.add(raceAgainButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(leaderboardButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Populates the results screen with data from the finished race.
     * Called by RacePanel when a typist crosses the finish line.
     *
     * @param typists          the typists who raced
     * @param winnerIndex      index of the winning typist
     * @param wpmValues        WPM achieved by each typist
     * @param accuracyPercents accuracy percentage for each typist
     * @param burnoutCounts    how many times each typist burnt out
     * @param oldWinnerAccuracy the winner's accuracy before the improvement
     * @param turnCount        total turns the race took
     */
    public void showResults(Typist[] typists, int winnerIndex, int[] wpmValues,
                            int[] accuracyPercents, int[] burnoutCounts,
                            double oldWinnerAccuracy, int turnCount)
    {
        this.typists = typists;
        this.winnerIndex = winnerIndex;
        this.wpmValues = wpmValues;
        this.accuracyPercents = accuracyPercents;
        this.burnoutCounts = burnoutCounts;
        this.oldWinnerAccuracy = oldWinnerAccuracy;
        this.turnCount = turnCount;

        // Update winner banner
        winnerLabel.setText("And the winner is... " + typists[winnerIndex].getName() + "!");

        // Calculate leaderboard points for this race
        int[] pointsEarned = calculatePoints();

        // Calculate sponsor bonuses for this race
        int[] sponsorBonuses = calculateSponsorBonuses();

        // Update cumulative data for each typist after the race:
        for (int i = 0; i < typists.length; i++)
        {
            cumulativePoints[i] += pointsEarned[i];
            totalEarnings[i] += sponsorBonuses[i];
            raceCount[i]++;

            // Track consecutive wins without burnout for badges
            if (i == winnerIndex && burnoutCounts[i] == 0)
            {
                winsWithoutBurnout[i]++;
            }
            else
            {
                winsWithoutBurnout[i] = 0;
            }
        }

        // Auto purchase upgrades if typist can afford them
        for (int i = 0; i < typists.length; i++)
        {
            if (totalEarnings[i] >= 100)
            {
                gui.setBetterKeyboard(i, true);
            }
            if (totalEarnings[i] >= 150)
            {
                gui.setWristSupport(i, true);
            }
        }

        // Save cumulative points to GUI so SetupPanel can read them for next race
        gui.setCumulativePoints(cumulativePoints.clone());

        // removeAll() clears all existing components so the panel can be rebuilt with fresh data
        buildStatsPanel();
        buildLeaderboardPanel(pointsEarned);
        buildSponsorPanel(sponsorBonuses);

        // revalidate() tells the layout manager to redo the layout after components changed
        revalidate();
        // repaint() redraws the panel visually so the new content appears on screen
        repaint();
    }

    /**
     * Builds the stats section showing WPM, accuracy, burnout and accuracy changes.
     */
    private void buildStatsPanel()
    {
        // Removes all previously added components before rebuilding with new race data
        statsPanel.removeAll();

        JLabel title = new JLabel("Race Statistics");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(0, 200, 255));
        statsPanel.add(title);
        statsPanel.add(Box.createVerticalStrut(10));

        for (int i = 0; i < typists.length; i++)
        {
            // Typist name header
            JLabel nameLabel = new JLabel(typists[i].getName());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
            nameLabel.setForeground(i == winnerIndex ? new Color(255, 215, 0) : Color.WHITE);
            statsPanel.add(nameLabel);

            // WPM
            statsPanel.add(makeStatLabel("WPM: " + wpmValues[i]));

            // Accuracy percentage
            statsPanel.add(makeStatLabel("Accuracy: " + accuracyPercents[i] + "%"));

            // Burnout count
            statsPanel.add(makeStatLabel("Burnouts: " + burnoutCounts[i]));

            // Accuracy change
            if (i == winnerIndex)
            {
                statsPanel.add(makeStatLabel("Accuracy: " + oldWinnerAccuracy
                    + " -> " + typists[i].getAccuracy() + " (improved)"));
            }
            else if (burnoutCounts[i] > 0)
            {
                statsPanel.add(makeStatLabel("Accuracy reduced by burnout"));
            }

            statsPanel.add(Box.createVerticalStrut(8));
        }
    }

    /**
     * Builds the leaderboard section showing points earned this race
     * and cumulative points totals. Also shows titles and badges.
     *
     * @param pointsEarned points each typist earned this race
     */
    private void buildLeaderboardPanel(int[] pointsEarned)
    {
        // Removes all previously added components before rebuilding with new race data
        leaderboardPanel.removeAll();

        JLabel title = new JLabel("Leaderboard");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(255, 215, 0));
        leaderboardPanel.add(title);
        leaderboardPanel.add(Box.createVerticalStrut(10));

        // Points earned this race
        JLabel thisRaceLabel = new JLabel("This Race:");
        thisRaceLabel.setFont(new Font("Arial", Font.BOLD, 13));
        thisRaceLabel.setForeground(Color.LIGHT_GRAY);
        leaderboardPanel.add(thisRaceLabel);

        for (int i = 0; i < typists.length; i++)
        {
            leaderboardPanel.add(makeStatLabel(typists[i].getName()
                + ": +" + pointsEarned[i] + " pts"));
        }

        leaderboardPanel.add(Box.createVerticalStrut(10));

        // Cumulative points
        JLabel totalLabel = new JLabel("Total Points:");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 13));
        totalLabel.setForeground(Color.LIGHT_GRAY);
        leaderboardPanel.add(totalLabel);

        for (int i = 0; i < typists.length; i++)
        {
            leaderboardPanel.add(makeStatLabel(typists[i].getName()
                + ": " + cumulativePoints[i] + " pts"));
        }

        leaderboardPanel.add(Box.createVerticalStrut(10));

        // Badges
        JLabel badgeLabel = new JLabel("Badges:");
        badgeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        badgeLabel.setForeground(Color.LIGHT_GRAY);
        leaderboardPanel.add(badgeLabel);

        for (int i = 0; i < typists.length; i++)
        {
            String badge = getBadge(i);
            if (!badge.isEmpty())
            {
                leaderboardPanel.add(makeStatLabel(typists[i].getName() + ": " + badge));
            }
        }
    }

    /**
     * Builds the sponsor section showing sponsor deals and bonuses earned.
     *
     * @param sponsorBonuses coins each typist earned from sponsors this race
     */
    private void buildSponsorPanel(int[] sponsorBonuses)
    {
        // Removes all previously added components before rebuilding with new race data
        sponsorPanel.removeAll();

        JLabel title = new JLabel("Sponsors");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(0, 255, 100));
        sponsorPanel.add(title);
        sponsorPanel.add(Box.createVerticalStrut(10));

        // Build one sponsor card per typist and append it to sponsorPanel.
        for (int i = 0; i < typists.length; i++)
        {
            // Sponsor name
            String sponsorName = i < SPONSOR_NAMES.length ? SPONSOR_NAMES[i] : "No Sponsor";
            JLabel sponsorLabel = new JLabel(typists[i].getName() + " - " + sponsorName);
            sponsorLabel.setFont(new Font("Arial", Font.BOLD, 12));
            sponsorLabel.setForeground(Color.WHITE);
            sponsorPanel.add(sponsorLabel);

            // Sponsor condition
            String condition = i < SPONSOR_CONDITIONS.length ? SPONSOR_CONDITIONS[i] : "";
            sponsorPanel.add(makeStatLabel(condition));

            // Bonus earned
            sponsorPanel.add(makeStatLabel("Earned: +" + sponsorBonuses[i] + " coins"));
            sponsorPanel.add(makeStatLabel("Total: " + totalEarnings[i] + " coins"));
            sponsorPanel.add(Box.createVerticalStrut(8));

            // Show upgrade unlocked message
            if (totalEarnings[i] >= 150)
            {
                JLabel upgradeLabel = new JLabel("Upgrade unlocked: Better Keyboard + Wrist Support!");
                upgradeLabel.setFont(new Font("Arial", Font.BOLD, 11));
                upgradeLabel.setForeground(new Color(0, 255, 100));
                sponsorPanel.add(upgradeLabel);
            }
            else if (totalEarnings[i] >= 100)
            {
                JLabel upgradeLabel = new JLabel("Upgrade unlocked: Better Keyboard!");
                upgradeLabel.setFont(new Font("Arial", Font.BOLD, 11));
                upgradeLabel.setForeground(new Color(0, 255, 100));
                sponsorPanel.add(upgradeLabel);
            }
        }

    }

    /**
     * Calculates leaderboard points for each typist based on:
     * - Finishing position (3 pts for 1st, 2 pts for 2nd, 1 pt for 3rd)
     * - WPM bonus (1 extra pt per 10 WPM above 30)
     * - Burnout penalty (-1 pt per burnout)
     *
     * @return array of points earned by each typist this race
     */
    private int[] calculatePoints()
    {
        int[] points = new int[typists.length];

        // Award position points based on finishing order
        // Winner gets 3 pts, others get 2 and 1 based on progress
        points[winnerIndex] = 3;

        // Sort remaining typists by progress for 2nd and 3rd place
        int secondPlace = -1;
        int bestProgress = -1;

        // Scan all typists, skipping the winner, to find the one with the highest progress.
        // Using > so only the first typist encountered wins a tie for second place.
        for (int i = 0; i < typists.length; i++)
        {
            if (i != winnerIndex && typists[i].getProgress() > bestProgress)
            {
                bestProgress = typists[i].getProgress();
                secondPlace = i;
            }
        }

        // Calculate the final points for each typist:
        //1. Base points: 3 for 1st place, 2 for 2nd place, 1 for everyone else.
        //2. WPM bonus:   +1 point for every 10 WPM above 30 (integer division, so 31–39 WPM = +1, etc.).
        //3. Burnout penalty: -1 point for every 3 burnouts accumulated.
        //4. Floor: winner cannot drop below 1; all others cannot drop below 0.
        for (int i = 0; i < typists.length; i++)
        {
            if (i == winnerIndex) points[i] = 3;
            else if (i == secondPlace) points[i] = 2;
            else points[i] = 1;

            // WPM bonus
            if (wpmValues[i] > 30)
            {
                points[i] += (wpmValues[i] - 30) / 10;
            }

            // Burnout penalty
            points[i] -= burnoutCounts[i] / 3;
            if (i == winnerIndex && points[i] < 1)
            {
                points[i] = 1;
            }
            else if (points[i] < 0)
            {
                points[i] = 0;
            }
        }

        return points;
    }

    /**
     * Calculates sponsor bonuses for each typist.
     * KeyCorp: +50 coins for finishing without burnout
     * TypeTech: +30 coins for WPM above 40
     * SpeedKeys: +40 coins for finishing first
     *
     * @return array of coins earned by each typist this race
     */
    private int[] calculateSponsorBonuses()
    {
        int[] bonuses = new int[typists.length];

        // Base prize money by position
        bonuses[winnerIndex] += 100;

        for (int i = 0; i < typists.length; i++)
        {
            // Base earnings for all typists
            bonuses[i] += 20;

            // KeyCorp: no burnout bonus
            if (i == 0 && burnoutCounts[i] == 0)
            {
                bonuses[i] += 50;
            }

            // TypeTech: WPM bonus
            if (i == 1 && wpmValues[i] > 40)
            {
                bonuses[i] += 30;
            }

            // SpeedKeys: first place bonus
            if (i == 2 && i == winnerIndex)
            {
                bonuses[i] += 40;
            }
        }

        return bonuses;
    }

    /**
     * Returns a badge string for a typist based on their achievements.
     * Speed Demon: 3 consecutive wins
     * Iron Fingers: 5 races without burnout
     *
     * @param typistIndex the index of the typist
     * @return badge string or empty string if no badge earned
     */
    private String getBadge(int typistIndex)
    {
        String badge = "";

        // First Victory — awarded for winning at least 1 race
        // cumulativePoints[typistIndex] >= 1 means the typist has won at least 1 race
        if (typistIndex == winnerIndex && cumulativePoints[typistIndex] >= 1)
        {
            badge += "First Victory ";
        }

        // Speed Demon — 3 consecutive wins
        if (typistIndex == winnerIndex && cumulativePoints[typistIndex] >= 9)
        {
            badge += "Speed Demon ";
        }

        // Iron Fingers — 5 races without burnout
        if (winsWithoutBurnout[typistIndex] >= 5)
        {
            badge += "Iron Fingers";
        }

        return badge.trim();
    }

    /**
     * Helper to create a small grey stat label.
     *
     * @param text the label text
     * @return a styled JLabel
     */
    private JLabel makeStatLabel(String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }
}
