import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * SetupPanel is the configuration screen shown before the race begins.
 * Users can:
 *   1. Choose a passage (short, medium, long, or custom)
 *   2. Choose how many typists compete (2 to 6... but we support up to 3 for simplicity)
 *   3. Apply difficulty modifiers (Autocorrect, Caffeine Mode, Night Shift)
 *   4. Customise each typist (name, symbol, typing style, keyboard, accessories)
 *
 * @author Thanh Son Nguyen / 250267606
 * @version 25
 */
public class SetupPanel extends JPanel
{
    // Reference to the main GUI so we can switch screens
    private TypingRaceGUI gui;

    // --- Passage selection ---
    private JComboBox<String> passageComboBox;
    private JTextField customPassageField;

    // Pre-defined passages
    private static final String SHORT_PASSAGE = "The quick brown fox jumps over the lazy dog";
    private static final String MEDIUM_PASSAGE = "To be or not to be that is the question whether tis nobler in the mind to suffer";
    private static final String LONG_PASSAGE = "It was the best of times it was the worst of times it was the age of wisdom it was the age of foolishness it was the epoch of belief";

    // --- Number of typists ---
    private JComboBox<Integer> seatCountComboBox;

    // --- Difficulty modifiers ---
    private JCheckBox autocorrectCheckBox;
    private JCheckBox caffeineModeCheckBox;
    private JCheckBox nightShiftCheckBox;

    // --- Typist customisation (up to 3 typists) ---
    private JTextField[] nameFields;
    private JTextField[] symbolFields;
    private JComboBox[] styleComboBoxes;
    private JComboBox[] keyboardComboBoxes;
    private JCheckBox[] wristSupportBoxes;
    private JCheckBox[] energyDrinkBoxes;
    private JCheckBox[] headphonesBoxes;

    // Typing styles and their accuracy effects
    private static final String[] TYPING_STYLES = {
        "Touch Typist (+0.10)",
        "Hunt & Peck (-0.10)",
        "Phone Thumbs (-0.05)",
        "Voice-to-Text (-0.15)"
    };

    // Keyboard types and their effects
    private static final String[] KEYBOARD_TYPES = {
        "Mechanical (+0.05)",
        "Membrane (-0.05)",
        "Touchscreen (-0.10)",
        "Stenography (+0.15)"
    };

    /**
     * Constructor - builds the setup screen layout.
     *
     * @param gui the main TypingRaceGUI window
     */
    public SetupPanel(TypingRaceGUI gui)
    {
        this.gui = gui;
        setBackground(new Color(30, 30, 30));
        setLayout(new BorderLayout());

        // Title at the top
        JLabel titleLabel = new JLabel("TYPING RACE SIMULATOR", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 200, 255));
        add(titleLabel, BorderLayout.NORTH);

        // Main content panel with scrolling in case content is too tall
        JPanel contentPanel = new JPanel();
        // BoxLayout stacks components vertically
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(30, 30, 30));

        // Add each section
        contentPanel.add(buildPassageSection());
        // Box.createVerticalStrut adds fixed vertical spacing
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(buildDifficultySection());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(buildTypistSection());

        // JScrollPane adds scroll bars when content is too tall to fit
        // This is extended to wrap an entire panel
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(new Color(30, 30, 30));
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Start button at the bottom
        JButton startButton = new JButton("START RACE");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setBackground(new Color(0, 200, 100));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> startRace());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(30, 30, 30));
        bottomPanel.add(startButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }


    /**
     * Builds the passage selection section.
     */
    private JPanel buildPassageSection()
    {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(30, 30, 30));

        // Section header label
        JLabel sectionTitle = new JLabel("Passage Selection");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 14));
        sectionTitle.setForeground(new Color(0, 200, 255));
        wrapper.add(sectionTitle, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 5));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0, 200, 255)));

        // Passage dropdown bar using JComboBox<>
        JLabel passageLabel = new JLabel("Choose Passage:");
        passageLabel.setForeground(Color.WHITE);
        passageLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        passageComboBox = new JComboBox<>(new String[]{
            "Short (43 chars)", "Medium (80 chars)", "Long (131 chars)", "Custom"
        });
        passageComboBox.setBackground(new Color(50, 50, 50));
        passageComboBox.setForeground(Color.WHITE);

        // Custom passage field (only visible when Custom is selected)
        JLabel customLabel = new JLabel("Custom Passage:");
        customLabel.setForeground(Color.WHITE);
        customLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        customPassageField = new JTextField("Type your custom passage here");
        customPassageField.setBackground(new Color(50, 50, 50));
        customPassageField.setForeground(Color.GRAY);
        customPassageField.setEnabled(false);

        // Show/hide custom field based on selection
        passageComboBox.addActionListener(e -> {
            boolean isCustom = passageComboBox.getSelectedItem().equals("Custom");
            customPassageField.setEnabled(isCustom);
            customPassageField.setForeground(isCustom ? Color.WHITE : Color.GRAY);
        });

        // Number of typists
        JLabel seatLabel = new JLabel("Number of Typists:");
        seatLabel.setForeground(Color.WHITE);
        seatLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        //JComboBox<Integer>: holds Integer items instead of strings
        // holds two options: 2 and 3 for the number of typists
        seatCountComboBox = new JComboBox<>(new Integer[]{2, 3});
        seatCountComboBox.setBackground(new Color(50, 50, 50));
        seatCountComboBox.setForeground(Color.WHITE);
        seatCountComboBox.setSelectedItem(3);

        panel.add(passageLabel);
        panel.add(passageComboBox);
        panel.add(customLabel);
        panel.add(customPassageField);
        panel.add(seatLabel);
        panel.add(seatCountComboBox);

        // Place the form grid inside the wrapper so the section title sits above the controls
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Builds the difficulty modifiers section.
     */
    private JPanel buildDifficultySection()
    {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(30, 30, 30));

        // Styled label pinned to top( NORTH) so it always sits above the three modifier checkboxes
        JLabel sectionTitle = new JLabel("Difficulty Modifiers");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 14));
        sectionTitle.setForeground(new Color(255, 165, 0));
        wrapper.add(sectionTitle, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createLineBorder(new Color(255, 165, 0)));

        //check box of 3 modifiers: Autocorrect, Caffeine Mode, Night Shift
        autocorrectCheckBox = new JCheckBox("Autocorrect — slideBack amount is halved");
        autocorrectCheckBox.setForeground(Color.WHITE);
        autocorrectCheckBox.setBackground(new Color(30, 30, 30));
        autocorrectCheckBox.setFont(new Font("Arial", Font.PLAIN, 13));

        caffeineModeCheckBox = new JCheckBox("Caffeine Mode — speed boost for first 10 turns, then higher burnout risk");
        caffeineModeCheckBox.setForeground(Color.WHITE);
        caffeineModeCheckBox.setBackground(new Color(30, 30, 30));
        caffeineModeCheckBox.setFont(new Font("Arial", Font.PLAIN, 13));

        nightShiftCheckBox = new JCheckBox("Night Shift — all typists have slightly reduced accuracy");
        nightShiftCheckBox.setForeground(Color.WHITE);
        nightShiftCheckBox.setBackground(new Color(30, 30, 30));
        nightShiftCheckBox.setFont(new Font("Arial", Font.PLAIN, 13));

        panel.add(autocorrectCheckBox);
        panel.add(caffeineModeCheckBox);
        panel.add(nightShiftCheckBox);

        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    
    /**
    * Builds the typist customisation section for up to 3 typists.
    */
    private JPanel buildTypistSection()
    {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(30, 30, 30));

        // Styled label pinned to top(NORTH) so it sits above the three side-by-side typist columns
        JLabel sectionTitle = new JLabel("Customise Typists");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 14));
        sectionTitle.setForeground(new Color(0, 255, 100));
        wrapper.add(sectionTitle, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 100)));

        // Default typist names and symbols
        String[] defaultNames = {"TURBOFINGERS", "QWERTY_QUEEN", "HUNT_N_PECK"};
        String[] defaultSymbols = {"A", "B", "C"};
        double[] defaultAccuracies = {0.85, 0.60, 0.30};

        nameFields = new JTextField[3];
        symbolFields = new JTextField[3];
        styleComboBoxes = new JComboBox[3];
        keyboardComboBoxes = new JComboBox[3];
        wristSupportBoxes = new JCheckBox[3];
        energyDrinkBoxes = new JCheckBox[3];
        headphonesBoxes = new JCheckBox[3];

        // Build one UI column per typist, storing each control in its array for later retrieval
        for (int i = 0; i < 3; i++)
        {
            JPanel typistPanel = new JPanel();
            typistPanel.setLayout(new BoxLayout(typistPanel, BoxLayout.Y_AXIS));
            typistPanel.setBackground(new Color(40, 40, 40));

            // Typist number header
            JLabel header = new JLabel("Typist " + (i + 1));
            header.setFont(new Font("Arial", Font.BOLD, 14));
            header.setForeground(new Color(0, 200, 255));
            typistPanel.add(header);
            typistPanel.add(Box.createVerticalStrut(8));

            // Name
            typistPanel.add(makeLabel("Name:"));
            nameFields[i] = new JTextField(defaultNames[i]);
            nameFields[i].setBackground(new Color(60, 60, 60));
            nameFields[i].setForeground(Color.WHITE);
            // setMaximumSize limits how wide the field grows inside BoxLayout
            nameFields[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            typistPanel.add(nameFields[i]);
            typistPanel.add(Box.createVerticalStrut(5));

            // Symbol
            typistPanel.add(makeLabel("Symbol (1 char):"));
            symbolFields[i] = new JTextField(defaultSymbols[i]);
            symbolFields[i].setBackground(new Color(60, 60, 60));
            symbolFields[i].setForeground(Color.WHITE);
            // setMaximumSize
            symbolFields[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            typistPanel.add(symbolFields[i]);
            typistPanel.add(Box.createVerticalStrut(5));

            // Typing style
            typistPanel.add(makeLabel("Typing Style:"));
            styleComboBoxes[i] = new JComboBox<>(TYPING_STYLES);
            styleComboBoxes[i].setBackground(new Color(60, 60, 60));
            styleComboBoxes[i].setForeground(Color.WHITE);
            styleComboBoxes[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            typistPanel.add(styleComboBoxes[i]);
            typistPanel.add(Box.createVerticalStrut(5));

            // Keyboard type
            typistPanel.add(makeLabel("Keyboard:"));
            keyboardComboBoxes[i] = new JComboBox<>(KEYBOARD_TYPES);
            keyboardComboBoxes[i].setBackground(new Color(60, 60, 60));
            keyboardComboBoxes[i].setForeground(Color.WHITE);
            keyboardComboBoxes[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            typistPanel.add(keyboardComboBoxes[i]);
            typistPanel.add(Box.createVerticalStrut(8));

            // Accessories
            typistPanel.add(makeLabel("Accessories:"));
            wristSupportBoxes[i] = makeCheckBox("Wrist Support (less burnout)");
            energyDrinkBoxes[i] = makeCheckBox("Energy Drink (accuracy boost early)");
            headphonesBoxes[i] = makeCheckBox("Headphones (fewer mistypes)");
            typistPanel.add(wristSupportBoxes[i]);
            typistPanel.add(energyDrinkBoxes[i]);
            typistPanel.add(headphonesBoxes[i]);

            panel.add(typistPanel);
        }

        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Helper to create a styled white label.
     */
    private JLabel makeLabel(String text)
    {
        JLabel label = new JLabel(text);
        label.setForeground(Color.LIGHT_GRAY);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        return label;
    }

    /**
     * Helper to create a styled dark checkbox.
     */
    private JCheckBox makeCheckBox(String text)
    {
        JCheckBox cb = new JCheckBox(text);
        cb.setForeground(Color.WHITE);
        cb.setBackground(new Color(40, 40, 40));
        cb.setFont(new Font("Arial", Font.PLAIN, 11));
        return cb;
    }

    /**
     * Gets the passage string based on the user's selection.
     */
    private String getSelectedPassage()
    {
        String selected = (String) passageComboBox.getSelectedItem();
        if (selected.equals("Custom"))
        {
            String custom = customPassageField.getText().trim();
            if (custom.isEmpty() || custom.equals("Type your custom passage here"))
            {
                return SHORT_PASSAGE;
            }
            return custom;
        }
        else if (selected.startsWith("Short")) {
            return SHORT_PASSAGE;
        }
        else if (selected.startsWith("Medium")) {
            return MEDIUM_PASSAGE;
        }
        else {
            return LONG_PASSAGE;
        }
    }

    /**
     * Calculates the accuracy modifier from typing style selection.
     */
    private double getStyleModifier(int typistIndex)
    {
        int selected = styleComboBoxes[typistIndex].getSelectedIndex();
        double[] modifiers = {0.10, -0.10, -0.05, -0.15};
        return modifiers[selected];
    }

    /**
     * Calculates the accuracy modifier from keyboard selection.
     */
    private double getKeyboardModifier(int typistIndex)
    {
        int selected = keyboardComboBoxes[typistIndex].getSelectedIndex();
        double[] modifiers = {0.05, -0.05, -0.10, 0.15};
        return modifiers[selected];
    }

    

    /**
     * Reads all the setup values and launches the race.
     * Creates Typist objects and passes them to the RacePanel.
     * Reads all setup values, creates Typist objects and launches the race
     */
    private void startRace()
    {
        String passage = getSelectedPassage();
        int seatCount = (Integer) seatCountComboBox.getSelectedItem();

        // Base accuracies for each typist position
        double[] baseAccuracies = {0.85, 0.60, 0.30};

        // Build the typist array
        // Read each typist's UI inputs, calculate their final accuracy, and construct their Typist object
        Typist[] typists = new Typist[seatCount];

        for (int i = 0; i < seatCount; i++)
        {
            String name = nameFields[i].getText().trim();
            String symStr = symbolFields[i].getText().trim();
            // if the symbol field is empty, use the default symbol 'A' + i 
            // (A for typist 1, B for typist 2, C for typist 3)
            // if the symbol field is not empty, use the symbol from the field
            char symbol;
            if (symStr.isEmpty())
            {
                symbol = (char)('A' + i);
            }
            else
            {
                symbol = symStr.charAt(0);
            }

            // Start with base accuracy and apply modifiers
            double accuracy = baseAccuracies[i];
            accuracy += getStyleModifier(i);
            accuracy += getKeyboardModifier(i);

            // Accessories
            if (wristSupportBoxes[i].isSelected()) {
                accuracy += 0.02;
            }
            if (energyDrinkBoxes[i].isSelected()) {
                accuracy += 0.05;
            }
            if (headphonesBoxes[i].isSelected()) {
                accuracy += 0.03;
            }

            // Night shift reduces everyone's accuracy
            if (nightShiftCheckBox.isSelected()) {
                accuracy -= 0.05;
            }

            // apply purchased upgrades: better keyboard
            if (gui.getHasBetterKeyboard()[i]) {
                accuracy += 0.05;
            }

            // rank affects starting accuracy
            // Get cumulative points from GUI
            int[] points = gui.getCumulativePoints();
            int[] sortedIndexes = getSortedIndexes(points);
            for (int rank = 0; rank < sortedIndexes.length; rank++)
            {
                if (sortedIndexes[rank] == i)
                {
                    if (rank == 0) {
                        accuracy -= 0.03; // champion faces higher pressure
                    }
                    if (rank == 2){
                        accuracy += 0.03; // underdog gets a boost
                    }
                }
            }

            // Clamp accuracy between 0.1 and 1.0
            if (accuracy < 0.1) accuracy = 0.1;
            if (accuracy > 1.0) accuracy = 1.0;

            typists[i] = new Typist(symbol, name, accuracy);
        }

        // Pass settings to the RacePanel and start
        boolean autocorrect = autocorrectCheckBox.isSelected();
        boolean caffeineMode = caffeineModeCheckBox.isSelected();
        boolean[] wristSupport = new boolean[seatCount];
        boolean[] energyDrink = new boolean[seatCount];
        boolean[] headphones = new boolean[seatCount];
        for (int i = 0; i < seatCount; i++)
        {
            wristSupport[i] = wristSupportBoxes[i].isSelected();
            energyDrink[i] = energyDrinkBoxes[i].isSelected();
            headphones[i] = headphonesBoxes[i].isSelected();
        }


        gui.getRacePanel().setupRace(typists, passage, autocorrect, caffeineMode, wristSupport, energyDrink, headphones);
        gui.showScreen(TypingRaceGUI.RACE_SCREEN);
    }

    /**
     * Returns indexes sorted by descending score using bubble sort.
     * Index 0 in the result = the typist with the highest score.
     *
     * @param scores the scores to sort by
     * @return sorted array of typist indexes
     */
    private int[] getSortedIndexes(int[] scores)
    {
        int n = scores.length;
        int[] indexes = new int[n];

        // Initialise indexes as an identity mapping [0, 1, 2, ...].
        for (int i = 0; i < n; i++) {
            indexes[i] = i;
        }

        // Bubble sort (descending) on the indirection array.
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
}