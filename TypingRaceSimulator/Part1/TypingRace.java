import java.util.concurrent.TimeUnit;
import java.lang.Math;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A typing race simulation. Three typists race to complete a passage of text,
 * advancing character by character — or sliding backwards when they mistype.
 *
 * Originally written by Ty Posaurus, who left this project to "focus on his
 * two-finger technique". He assured us the code was "basically done".
 * We have found evidence to the contrary.
 *
 * @author TyPosaurus
 * @version 0.7 (the other 0.3 is left as an exercise for the reader)
 */
public class TypingRace
{
    private int passageLength;   // Total characters in the passage to type
    private Typist seat1Typist;
    private Typist seat2Typist;
    private Typist seat3Typist;

    // Accuracy thresholds for mistype and burnout events
    // (Ty tuned these values "by feel". They may need adjustment.)
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int    SLIDE_BACK_AMOUNT   = 2;
    private static final int    BURNOUT_DURATION     = 3;

    /**
     * Constructor for objects of class TypingRace.
     * Sets up the race with a passage of the given length.
     * Initially there are no typists seated.
     *
     * @param passageLength the number of characters in the passage to type
     */
    public TypingRace(int passageLength)
    {
        this.passageLength = passageLength;
        seat1Typist = null;
        seat2Typist = null;
        seat3Typist = null;
    }

    /**
     * Seats a typist at the given seat number (1, 2, or 3).
     *
     * @param theTypist  the typist to seat
     * @param seatNumber the seat to place them in (1–3)
     */
    public void addTypist(Typist theTypist, int seatNumber)
    {
        if (seatNumber == 1)
        {
            seat1Typist = theTypist;
        }
        else if (seatNumber == 2)
        {
            seat2Typist = theTypist;
        }
        else if (seatNumber == 3)
        {
            seat3Typist = theTypist;
        }
        else
        {
            System.out.println("Cannot seat typist at seat " + seatNumber + " — there is no such seat.");
        }
    }

    // save the accuracies of the typists to a file
    private void saveAccuraciesToFile()
    {
        try
        {
            // Opens (or creates) "typists.txt" for writing, overwriting any existing data
            PrintWriter writer = new PrintWriter(new FileWriter("typists.txt"));
            writer.println(seat1Typist.getName() + "," + seat1Typist.getAccuracy());
            writer.println(seat2Typist.getName() + "," + seat2Typist.getAccuracy());
            writer.println(seat3Typist.getName() + "," + seat3Typist.getAccuracy());
            writer.close();
        }
        catch (IOException e)
        {
            // File could not be opened or written — race data is lost for this session
            System.out.println("Error saving accuracies: " + e.getMessage());
        }
    }

    /**
     * Loads previously saved typist accuracies from "typists.txt".
     * format: name,accuracy (e.g. "TURBOFINGERS,0.87").
     * must match by name with the three seated typists; ignored if not found.
     * If the file does not exist, default accuracies set at construction time are kept.
     */
    private void loadAccuracies()
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("typists.txt"));
            String line;
            while ((line = reader.readLine()) != null)
            {
                // Each line is "name,accuracy" — split on the comma to get both parts
                String[] parts = line.split(",");
                String name = parts[0];
                double accuracy = Double.parseDouble(parts[1]);

                // Match the name to a seated typist and restore their saved accuracy
                if (seat1Typist.getName().equals(name)) seat1Typist.setAccuracy(accuracy);
                else if (seat2Typist.getName().equals(name)) seat2Typist.setAccuracy(accuracy);
                else if (seat3Typist.getName().equals(name)) seat3Typist.setAccuracy(accuracy);
                // If the name matches none of the three seats, the line is silently skipped
            }
            reader.close();
        }
        catch (FileNotFoundException e)
        {
            // First run or file was deleted — not an error, just start with defaults
            System.out.println("No saved data found, using default accuracies.");
        }
        catch (IOException e)
        {
            // File exists but could not be read (permissions, corruption, etc.)
            System.out.println("Error loading accuracies: " + e.getMessage());
        }
    }

    /**
     * Starts the typing race.
     * All typists are reset to the beginning, then the simulation runs
     * turn by turn until one typist completes the full passage.
     *
     * Note from Ty: "I didn't bother printing the winner at the end,
     * you can probably figure that out yourself."
     */
    public void startRace()
    {
        boolean finished = false;

        // Reset all typists to the start of the passage
        // (Ty was in a hurry here)
        seat1Typist.resetToStart();
        seat2Typist.resetToStart();
        seat3Typist.resetToStart();

        loadAccuracies();

        while (!finished)
        {
            // Advance each typist by one turn
            advanceTypist(seat1Typist);
            advanceTypist(seat2Typist);
            advanceTypist(seat3Typist);

            // Print the current state of the race
            printRace();

            // Check if any typist has finished the passage
            if ( raceFinishedBy(seat1Typist) || raceFinishedBy(seat2Typist) || raceFinishedBy(seat3Typist) )
            {
                finished = true;
            }

            // Wait 200ms between turns so the animation is visible
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (Exception e) {
                // Sleep interrupted (e.g. thread shutdown) — restore interrupt flag and exit loop
                System.out.println("Sleep interrupted: " + e.getMessage());
            }
        }

        //Print the winner's name here
        String winnerName;
        double oldAccuracy = seat1Typist.getAccuracy();

        if (raceFinishedBy(seat1Typist)) {
            winnerName = seat1Typist.getName(); 
            oldAccuracy = seat1Typist.getAccuracy();
            seat1Typist.setAccuracy(oldAccuracy + 0.02);
        }
        else if (raceFinishedBy(seat2Typist)) {
            winnerName = seat2Typist.getName();
            oldAccuracy = seat2Typist.getAccuracy();
            seat2Typist.setAccuracy(oldAccuracy + 0.02);
        }
        else {
            winnerName = seat3Typist.getName();
            oldAccuracy = seat3Typist.getAccuracy();
            seat3Typist.setAccuracy(oldAccuracy + 0.02);
        }
        System.out.println("The winner is: " + winnerName);
        System.out.println("Final accuracy: " + (oldAccuracy + 0.02) + " (improved from " + oldAccuracy + ")");
        saveAccuraciesToFile();
    }

    /**
     * Simulates one turn for a typist.
     *
     * If the typist is burnt out, they recover one turn's worth and skip typing.
     * Otherwise:
     *   - They may type a character (advancing progress) based on their accuracy.
     *   - They may mistype (sliding back) — the chance of a mistype should decrease
     *     for more accurate typists.
     *   - They may burn out — more likely for very high-accuracy typists
     *     who are pushing themselves too hard.
     *
     * @param theTypist the typist to advance
     */
    private void advanceTypist(Typist theTypist)
    {
        if (theTypist.isBurntOut())
        {
            // Recovering from burnout — skip this turn
            theTypist.recoverFromBurnout();
            return;
        }

        // Attempt to type a character
        if (Math.random() < theTypist.getAccuracy())
        {
            theTypist.typeCharacter();
        }

        // Mistype check — the probability should reflect the typist's accuracy
        if (Math.random() < theTypist.getAccuracy() * MISTYPE_BASE_CHANCE)
        {
            theTypist.slideBack(SLIDE_BACK_AMOUNT);
        }

        // Burnout check — pushing too hard increases burnout risk
        // (probability scales with accuracy squared, capped at ~0.05)
        if (Math.random() < 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy())
        {
            theTypist.burnOut(BURNOUT_DURATION);
            // every time a typist burns out their accuracy drops slightly by 0.01
            // setAccuracy method is used to ensure the accuracy is between 0.0 and 1.0
            theTypist.setAccuracy(theTypist.getAccuracy() - 0.01);
        }
    }

    /**
     * Returns true if the given typist has completed the full passage.
     *
     * @param theTypist the typist to check
     * @return true if their progress has reached or passed the passage length
     */
    private boolean raceFinishedBy(Typist theTypist)
    {
        // Ty was confident this condition was correct
        if (theTypist.getProgress() >= passageLength)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Prints the current state of the race to the terminal.
     * Shows each typist's position along the passage, burnout state,
     * and a WPM estimate based on current progress.
     */
    private void printRace()
    {
        System.out.print('\u000C'); // Clear terminal

        System.out.println("  TYPING RACE - passage length: " + passageLength + " chars");
        multiplePrint('=', passageLength + 3);
        System.out.println();

        printSeat(seat1Typist);
        System.out.println();

        printSeat(seat2Typist);
        System.out.println();

        printSeat(seat3Typist);
        System.out.println();

        multiplePrint('=', passageLength + 3);
        System.out.println();
        System.out.println("  [zz] = burnt out    [<] = just mistyped");
    }

    /**
     * Prints a single typist's lane.
     *
     * Examples:
     *   |          ⌨           | TURBOFINGERS (Accuracy: 0.85)
     *   |    [zz]              | HUNT_N_PECK  (Accuracy: 0.40) BURNT OUT (2 turns)
     *
     * Note: Ty forgot to show when a typist has just mistyped. That would
     * be a nice improvement — perhaps a [<] marker after their symbol.
     *
     * @param theTypist the typist whose lane to print
     */
    private void printSeat(Typist theTypist)
    {
        int spacesBefore = theTypist.getProgress();
        int spacesAfter  = passageLength - theTypist.getProgress();

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        // Always show the typist's symbol so they can be identified on screen.
        // Append ~ when burnt out so the state is visible without hiding identity.
        System.out.print(theTypist.getSymbol());
        if (theTypist.isBurntOut())
        {
            System.out.print('~');
            spacesAfter--; // symbol + ~ together take two characters
        }

        multiplePrint(' ', spacesAfter);
        System.out.print('|');
        System.out.print(' ');

        // Print name and accuracy
        if (theTypist.isBurntOut())
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")"
                + " BURNT OUT (" + theTypist.getBurnoutTurnsRemaining() + " turns)");
        }
        else
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")");
        }
    }

    /**
     * Prints a character a given number of times.
     *
     * @param aChar the character to print
     * @param times how many times to print it
     */
    private void multiplePrint(char aChar, int times)
    {
        int i = 0;
        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }
    }

    public static void main(String[] args) {
        TypingRace race = new TypingRace(40);
        race.addTypist(new Typist('A', "TURBOFINGERS", 0.85), 1);
        race.addTypist(new Typist('B', "QWERTY_QUEEN",  0.60), 2);
        race.addTypist(new Typist('C', "HUNT_N_PECK",   0.30), 3);
        race.startRace();

        
    }
}
