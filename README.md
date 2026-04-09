# TypingRaceSimulator

Object Oriented Programming Project — ECS414U

## Project Structure

```
TypingRaceSimulator/
├── Part1/    # Textual simulation (Java, command-line)
└── Part2/    # GUI simulation (Java Swing)
```

## Part 1 — Textual Simulation

### Dependencies

- Java Development Kit (JDK) 11 or higher
- No external libraries required

The main method is already set up with 3 typists:
```java
public static void main(String[] args) {
    TypingRace race = new TypingRace(40);
    race.addTypist(new Typist('A', "TURBOFINGERS", 0.85), 1);
    race.addTypist(new Typist('B', "QWERTY_QUEEN",  0.60), 2);
    race.addTypist(new Typist('C', "HUNT_N_PECK",   0.30), 3);
    race.startRace();
}
```

### How to compile

```bash
cd Part1
javac Typist.java TypingRace.java
```

### How to run

```bash
java TypingRace
```

## Part 2 — GUI Simulation


### Dependencies

- Java Development Kit (JDK) 11 or higher
- Java Swing (included in standard JDK)
- No external libraries required

The main method is:
```java
public static void main(String[] args) {
    TypingRaceGUI gui = new TypingRaceGUI();
    gui.startRaceGUI();
}
```

The Part 2 main method is intentionally simple because the constructor handles all 
setup — creating the JFrame, CardLayout, and all 5 screen panels. 
startRaceGUI() then shows the setup screen first.


### How to compile
```bash
cd Part2
javac Typist.java SetupPanel.java RacePanel.java ResultsPanel.java StatsPanel.java LeaderboardPanel.java TypingRaceGUI.java
```

### How to run
```bash
java TypingRaceGUI
```

## Notes
- Part 1 saves typist accuracy to typists.txt between runs
- Part 2 GUI is started by calling startRaceGUI() on a TypingRaceGUI object
- All code should compile and run using standard command-line tools without any IDE-specific configuration.

