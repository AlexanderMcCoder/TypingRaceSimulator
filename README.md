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


## Dependencies

- Java Development Kit (JDK) 11 or higher
- Java Swing (included in standard JDK)
- No external libraries required

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

