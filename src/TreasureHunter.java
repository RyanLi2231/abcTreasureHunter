import java.util.Scanner;
import java.awt.Color;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private boolean hardMode;
    private boolean easyMode;
    private boolean samuraiMode;
    private boolean end = false;
    OutputWindow window = new OutputWindow();

    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }

    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        window.addTextToWindow("Welcome to TREASURE HUNTER!\n", Color.BLACK);
        window.addTextToWindow("Going hunting for the big treasure, eh?\n", Color.BLACK);
        window.addTextToWindow("What's your name, Hunter?", Color.BLACK);
        String name = SCANNER.nextLine().toLowerCase();

        // set hunter instance variable
        hunter = new Hunter(name, 20, window);
        window.clear();
        window.addTextToWindow("Easy, Normal, or Hard mode? (e/n/h): ", Color.BLACK);
        String difficulty = SCANNER.nextLine().toLowerCase();
        window.clear();
        if (difficulty.equals("h")) {
            hardMode = true;
        } else if (difficulty.equals("e")) {
            easyMode = true;
            hunter.changeGold(20);
        } else if (difficulty.equals("test")) {
            hunter.changeGold(80);
            hunter.test();
        } else if (difficulty.equals("s")) {
            samuraiMode = true;
        }
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.5;
        double toughness = 0.4;
        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.25;

            // and the town is "tougher"
            toughness = 0.75;
        }
        if (easyMode) {
            markdown = 1;

        }

        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown, window);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness, window);

        if (easyMode) {
            currentTown.easyMode();
        }
        if (samuraiMode) {
            currentTown.samuraiMode();
            hunter.samurai();
        }

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice;
        while (!end) {

            if (hunter.treasureKitIsFull()) {
                window.addTextToWindow("\nCongratulations, you have found the last of the three treasures, you win!", Color.darkGray);
                end = true;
            } else {
                String menu = "";
                menu += currentTown.getLatestNews();
                menu += ("\n***\n");
                window.addTextToWindow(menu, Color.black);
                hunter.infoString();
                currentTown.infoString();
                menu = ("\n(B)uy something at the shop.\n");
                menu += ("(S)ell something at the shop.\n");
                menu += ("(E)xplore surrounding terrain.\n");
                menu += ("(M)ove on to a different town.\n");
                menu += ("(L)ook for trouble!\n");
                menu += ("(D)ig for gold\n");
                menu += ("(H)unt for treasure\n");
                menu += ("Give up the hunt and e(X)it.\n");
                menu += ("What's your next move?");
                window.addTextToWindow(menu, Color.BLACK);
                choice = SCANNER.nextLine().toLowerCase();
                window.clear();
                processChoice(choice);
            }

        }
    }

    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice);
        } else if (choice.equals("e")) {
            currentTown.getTerrain().infoString();
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown()) {
                // This town is going away so print its news ahead of time.
                System.out.println(currentTown.getLatestNews());
                enterTown();
            }
        } else if (choice.equals("l")) {
            if (currentTown.lookForTrouble().equals("end")) {
                end = true;
                gameOver();
            }

        } else if (choice.equals("d")) {
            currentTown.digForGold(hunter);
        } else if (choice.equals("x")) {
            window.addTextToWindow("Fare thee well, " + hunter.getHunterName() + "!", Color.black);
            end = true;
        } else if (choice.equals("h")) {
            currentTown.searchTreasure();
        } else {
            window.addTextToWindow("Yikes! That's an invalid option! Try again.", Color.black);
        }
    }

    public void gameOver() {
        System.out.println("You have lost! :(");
    }
}