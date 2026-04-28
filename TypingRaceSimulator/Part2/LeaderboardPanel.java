import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * LeaderboardPanel displays the global leaderboard and financial standings.
 * Shows cumulative points ranking (Option A) and total earnings ranking (Option B)
 * across all races, along with titles and badges earned.
 *
 * @author Thanh Son Nguyen 
 * @version 1
 */
public class LeaderboardPanel extends JPanel
{
    // Reference to main GUI for switching screens
    private TypingRaceGUI gui;

    // --- Current data passed from ResultsPanel ---
    private Typist[] typists;
    private int[] cumulativePoints;
    private int[] totalEarnings;
    private int[] raceCount;

    // --- UI panels rebuilt each refresh ---
    private JPanel pointsPanel;
    private JPanel earningsPanel;

    /**
     * Constructor - builds the leaderboard screen layout.
     *
     * @param gui the main TypingRaceGUI window
     */
    public LeaderboardPanel(TypingRaceGUI gui)
    {
        this.gui = gui;
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("LEADERBOARD", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.BLACK);
        add(titleLabel, BorderLayout.NORTH);

        // Two side by side panels — points and earnings
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(Color.WHITE);

        // Points leaderboard (Option A)
        pointsPanel = new JPanel();
        // BoxLayout stacks components vertically
        pointsPanel.setLayout(new BoxLayout(pointsPanel, BoxLayout.Y_AXIS));
        pointsPanel.setBackground(Color.WHITE);
        pointsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Earnings leaderboard (Option B)
        earningsPanel = new JPanel();
        // BoxLayout stacks components vertically
        earningsPanel.setLayout(new BoxLayout(earningsPanel, BoxLayout.Y_AXIS));
        earningsPanel.setBackground(Color.WHITE);
        earningsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        contentPanel.add(pointsPanel);
        contentPanel.add(earningsPanel);
        add(contentPanel, BorderLayout.CENTER);

        // Back button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton backButton = new JButton("BACK TO RESULTS");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(0, 150, 255));
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> gui.showScreen(TypingRaceGUI.RESULTS_SCREEN));

        JButton setupButton = new JButton("NEW RACE");
        setupButton.setFont(new Font("Arial", Font.BOLD, 14));
        setupButton.setBackground(new Color(0, 200, 100));
        setupButton.setForeground(Color.BLACK);
        setupButton.setFocusPainted(false);
        setupButton.addActionListener(e -> gui.showScreen(TypingRaceGUI.SETUP_SCREEN));

        buttonPanel.add(backButton);
        buttonPanel.add(setupButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Updates the leaderboard with the latest cumulative data.
     * Called by ResultsPanel before switching to this screen.
     *
     * @param typists          the typists who raced
     * @param cumulativePoints total points per typist across all races
     * @param totalEarnings    total coins earned per typist across all races
     * @param raceCount        number of races each typist has completed
     */
    public void refresh(Typist[] typists, int[] cumulativePoints, int[] totalEarnings, int[] raceCount)
    {
        this.typists = typists;
        this.cumulativePoints = cumulativePoints;
        this.totalEarnings = totalEarnings;
        this.raceCount = raceCount;

        buildPointsPanel();
        buildEarningsPanel();

        // revalidate() redoes the layout after components are added
        revalidate();
        // repaint() redraws the panel visually
        repaint();
    }

    /**
     * Builds the points leaderboard panel (Option A).
     * Ranks typists by cumulative points and shows their titles and badges.
     */
    private void buildPointsPanel()
    {
        // removeAll() clears existing components before rebuilding
        pointsPanel.removeAll();

        JLabel title = new JLabel("Points Leaderboard (Option A)");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.BLACK);
        pointsPanel.add(title);
        pointsPanel.add(Box.createVerticalStrut(15));

        if (typists == null) {
            return;
        }
        // Sort typists by cumulative points using simple bubble sort
        int[] sortedIndexes = getSortedIndexes(cumulativePoints);

        // Display each typist in ranked order
        for (int rank = 0; rank < typists.length; rank++)
        {
            int i = sortedIndexes[rank];

            // Rank medal colours — gold, silver, bronze
            Color rankColour = Color.BLACK;

            JLabel rankLabel = new JLabel((rank + 1) + ".  " + typists[i].getName());
            rankLabel.setFont(new Font("Arial", Font.BOLD, 14));
            rankLabel.setForeground(rankColour);
            pointsPanel.add(rankLabel);

            pointsPanel.add(makeStatLabel("Points: " + cumulativePoints[i]));
            pointsPanel.add(makeStatLabel("Races: " + raceCount[i]));
            pointsPanel.add(makeStatLabel("Avg pts/race: " + getAvgPoints(i)));

            // Show title based on points
            String title2 = getTitle(i);
            if (!title2.isEmpty())
            {
                JLabel titleLabel2 = new JLabel("Title: " + title2);
                titleLabel2.setFont(new Font("Arial", Font.ITALIC, 12));
                titleLabel2.setForeground(Color.BLACK);
                pointsPanel.add(titleLabel2);
            }

            // Show badge if earned
            String badge = getBadge(i);
            if (!badge.isEmpty())
            {
                JLabel badgeLabel = new JLabel("Badge: " + badge);
                badgeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                badgeLabel.setForeground(Color.BLACK);
                pointsPanel.add(badgeLabel);
            }

            pointsPanel.add(Box.createVerticalStrut(12));
        }
    }

    /**
     * Builds the earnings leaderboard panel (Option B).
     * Ranks typists by total earnings and shows upgrade suggestions.
     */
    private void buildEarningsPanel()
    {
        // removeAll() clears existing components before rebuilding
        earningsPanel.removeAll();

        JLabel title = new JLabel("Earnings Leaderboard (Option B)");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.BLACK);
        earningsPanel.add(title);
        earningsPanel.add(Box.createVerticalStrut(15));

        if (typists == null) {
            return;
        }
        // Sort typists by total earnings
        int[] sortedIndexes = getSortedIndexes(totalEarnings);

        for (int rank = 0; rank < typists.length; rank++)
        {
            int i = sortedIndexes[rank];

            Color rankColour = Color.BLACK;

            JLabel rankLabel = new JLabel((rank + 1) + ".  " + typists[i].getName());
            rankLabel.setFont(new Font("Arial", Font.BOLD, 14));
            rankLabel.setForeground(rankColour);
            earningsPanel.add(rankLabel);

            earningsPanel.add(makeStatLabel("Total Earnings: " + totalEarnings[i] + " coins"));
            earningsPanel.add(makeStatLabel("Races: " + raceCount[i]));
            earningsPanel.add(makeStatLabel("Avg earnings/race: " + getAvgEarnings(i)));

            // Upgrades available based on earnings (Option B Point 27)
            earningsPanel.add(Box.createVerticalStrut(5));
            earningsPanel.add(makeStatLabel("Available Upgrades:"));

            if (totalEarnings[i] >= 100)
            {
                earningsPanel.add(makeUpgradeLabel("Better Keyboard (+0.05 acc) - 100 coins"));
            }
            if (totalEarnings[i] >= 150)
            {
                earningsLabel("Wrist Support (-1 burnout turn) - 150 coins", earningsPanel);
            }
            if (totalEarnings[i] < 100)
            {
                earningsPanel.add(makeStatLabel("Keep racing to unlock upgrades!"));
            }

            earningsPanel.add(Box.createVerticalStrut(12));
        }
    }

    /**
     * Returns indexes of typists sorted by descending score using bubble sort.
     * Index 0 in the result = the typist with the highest score.
     *
     * @param scores the scores to sort by
     * @return sorted array of typist indexes
     */
    private int[] getSortedIndexes(int[] scores)
    {
        int n = typists.length;
        int[] indexes = new int[n];
        for (int i = 0; i < n; i++) indexes[i] = i;

        // Bubble sort indexes by descending score
        for (int i = 0; i < n - 1; i++)
        {
            for (int j = 0; j < n - 1 - i; j++)
            {
                if (scores[indexes[j]] < scores[indexes[j + 1]])
                {
                    int temp = indexes[j];
                    indexes[j] = indexes[j + 1];
                    indexes[j + 1] = temp;
                }
            }
        }
        return indexes;
    }

    /**
     * Returns average points per race for a typist.
     *
     * @param i typist index
     * @return average points as an int
     */
    private int getAvgPoints(int i)
    {
        if (raceCount[i] == 0) {
            return 0;
        }
        return cumulativePoints[i] / raceCount[i];
    }

    /**
     * Returns average earnings per race for a typist.
     *
     * @param i typist index
     * @return average earnings as an int
     */
    private int getAvgEarnings(int i)
    {
        if (raceCount[i] == 0) {
            return 0;
        }
        return totalEarnings[i] / raceCount[i];
    }

    /**
     * Returns a title for a typist based on their cumulative points.
     * Titles reflect progression through the leaderboard.
     *
     * @param i typist index
     * @return title string or empty string if no title yet
     */
    private String getTitle(int i)
    {
        if (cumulativePoints[i] >= 30){ 
            return "Grand Champion";
        }

        else if (cumulativePoints[i] >= 20) {
            return "Elite Typist";
        }

        else if (cumulativePoints[i] >= 10) {
            return "Rising Star";
        }

        else if (cumulativePoints[i] >= 5) {
            return "Competitor";
        }

        else {
            return "";
        }
    }

    /**
     * Returns a badge for a typist based on their race history.
     * Speed Demon: accumulated 9+ points (roughly 3 wins)
     * Iron Fingers: 5+ races completed
     * First Victory: won at least 1 race
     *
     * @param i typist index
     * @return badge string or empty string if no badge earned
     */
    private String getBadge(int i)
    {
        String badge = "";
        
        if (cumulativePoints[i] >= 1)
        {
            badge += "First Victory ";
        }

        if (cumulativePoints[i] >= 9) {
            badge += "Speed Demon ";
        }

        if (raceCount[i] >= 5) {
            badge += "Iron Fingers";
        }

        return badge.trim();
    }

    /**
     * Helper method to create a small grey stat label.
     */
    private JLabel makeStatLabel(String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(Color.BLACK);
        return label;
    }

    /**
     * creates a green upgrade label.
     */
    private JLabel makeUpgradeLabel(String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(Color.BLACK);
        return label;
    }

    /**
     * Helper method to add an upgrade label directly to a panel.
     */
    private void earningsLabel(String text, JPanel panel)
    {
        panel.add(makeUpgradeLabel(text));
    }
}
