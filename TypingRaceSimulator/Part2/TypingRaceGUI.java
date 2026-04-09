import javax.swing.*;
import java.awt.*;

/**
 * TypingRaceGUI is the main entry point for the graphical typing race.
 * It creates the main JFrame window 
 * It manages switching between screens:
 * from (Setup) to (Race) to (Results) to (Stats/Leaderboard)
 *
 * @author Thanh Son Nguyen / 250267606
 * @version 25
 */
public class TypingRaceGUI
{
    // The main application window
    private JFrame frame;

    // The main panel that uses CardLayout to switch between screens
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Panel names used by CardLayout to identify each screen
    public static final String SETUP_SCREEN = "SETUP";
    public static final String RACE_SCREEN = "RACE";
    public static final String RESULTS_SCREEN = "RESULTS";
    public static final String STATS_SCREEN = "STATS";
    public static final String LEADERBOARD_SCREEN = "LEADERBOARD";

    // The individual screen panels (java files)
    private SetupPanel setupPanel;
    private RacePanel racePanel;
    private ResultsPanel resultsPanel;
    private StatsPanel statsPanel;
    private LeaderboardPanel leaderboardPanel;


    // the Constructor: which builds the main window and all screen panels.

    public TypingRaceGUI()
    {
        // Create the main JFrame window
        frame = new JFrame("Typing Race Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null); // centre on screen

        // CardLayout lets us swap between panels without opening new windows
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.BLACK);

        // Create all the screen panels, 
        // passing 'this' keyword so they can switch screens
        setupPanel = new SetupPanel(this);
        racePanel = new RacePanel(this);
        resultsPanel = new ResultsPanel(this);
        statsPanel = new StatsPanel(this);
        leaderboardPanel = new LeaderboardPanel(this);

        // Add all panels to the CardLayout container
        mainPanel.add(setupPanel, SETUP_SCREEN);
        mainPanel.add(racePanel, RACE_SCREEN);
        mainPanel.add(resultsPanel, RESULTS_SCREEN);
        mainPanel.add(statsPanel, STATS_SCREEN);
        mainPanel.add(leaderboardPanel, LEADERBOARD_SCREEN);

        // Add the main panel to the frame and show it
        frame.add(mainPanel);
        frame.setVisible(true);

        // Start on the setup screen
        showScreen(SETUP_SCREEN);
    }

    /**
     * this procedure Switches the visible screen to the given screen name.
     * Uses CardLayout to swap panels without opening new windows.
     *
     * @param screenName the name of the screen to show (use the constants above)
     */
    public void showScreen(String screenName)
    {
        cardLayout.show(mainPanel, screenName);
    }

    /**
     * Returns the RacePanel so other panels can pass race data to it.
     *
     * @return the RacePanel instance
     */
    public RacePanel getRacePanel()
    {
        return racePanel;
    }

    /**
     * Returns the ResultsPanel so race results can be sent to it.
     *
     * @return the ResultsPanel instance
     */
    public ResultsPanel getResultsPanel()
    {
        return resultsPanel;
    }

    /**
     * Returns the StatsPanel so stats can be updated after each race.
     *
     * @return the StatsPanel instance
     */
    public StatsPanel getStatsPanel()
    {
        return statsPanel;
    }

    /**
     * Returns the LeaderboardPanel so points can be updated after each race.
     *
     * @return the LeaderboardPanel instance
     */
    public LeaderboardPanel getLeaderboardPanel()
    {
        return leaderboardPanel;
    }

    /**
     * The entry point for the GUI version of the typing race.
     * Called from outside to launch the graphical application.
     */
    public void startRaceGUI()
    {
        // The constructor already sets everything up and shows the window.
        // This method exists as the required entry point from the spec.
        showScreen(SETUP_SCREEN);
    }


    // Main method for running the GUI directly.
    public static void main(String[] args)
    {
        TypingRaceGUI gui = new TypingRaceGUI();
        gui.startRaceGUI();
    }
}
