import java.awt.*;

/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean treasureSearched;
    private String treasure;
    private boolean dug;
    private boolean easyMode;
    private boolean samuraiMode;
    private OutputWindow window;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness, OutputWindow window) {
        this.window = window;
        this.shop = shop;
        this.terrain = getNewTerrain();
        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        int treasureChance = (int) (Math.random() * 4);
        if (treasureChance == 0) {
            treasure = "dust";
        } else if (treasureChance == 1) {
            treasure = "trophy";
        } else if (treasureChance == 2) {
            treasure = "crown";
        } else {
            treasure = "gem";
        }
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        String printMessage = "";
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
        window.addTextToWindow(printMessage, Color.black);
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        window.clear();
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            window.addTextToWindow("You used your ", Color.black);
            window.addTextToWindow(item, Color.magenta);
            window.addTextToWindow(" to cross the ", Color.black);
            window.addTextToWindow(terrain.getTerrainName() + ".\n", Color.cyan);
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                window.addTextToWindow("\nUnfortunately, your ", Color.black);
                window.addTextToWindow(item, Color.magenta);
                window.addTextToWindow(" broke.\n", Color.black);
            }
            return true;
        }

        window.addTextToWindow("You can't leave town, " + hunter.getHunterName() + ".\nYou don't have a ", Color.black);
        window.addTextToWindow(terrain.getNeededItem() + ".", Color.cyan);
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public String lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }
        if (Math.random() > noTroubleChance) {
            window.addTextToWindow("You couldn't find any trouble", Color.black);
        } else {
            int goldDiff = (int) (Math.random() * 10) + 1;
            String printMessage = "";
            if (samuraiMode && hunter.hasItemInKit("sword")) {
                printMessage += ("I see you want trouble str....ir.");
                printMessage += ("Apologies for angering you so dear samurai, please take my gold.");
                printMessage += ("You have recieved " + goldDiff + " gold.");
                window.addTextToWindow(printMessage, Color.blue);
                hunter.changeGold(goldDiff);
            } else {
                printMessage += ("You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n");
                double rand = Math.random();
                if (easyMode) {
                    rand += .10;
                }
                if (rand > noTroubleChance) {
                    printMessage += ("Okay, stranger! You proved yer mettle. Here, take my gold.");
                    printMessage += ("\nYou won the brawl and receive " + goldDiff + " gold.");
                    hunter.changeGold(goldDiff);
                    window.addTextToWindow(printMessage, Color.red);
                } else {
                    printMessage += ("That'll teach you to go lookin' fer trouble in MY town! Now pay up!");
                    printMessage += ("\nYou lost the brawl and pay " + goldDiff + " gold.");
                    window.addTextToWindow(printMessage, Color.red);
                    hunter.changeGold(-goldDiff);
                    if (hunter.getGold() < 0) {
                        return "end";
                    }
                }
            }
        }
        return "";
    }

    /**
     * Allows the hunter to dig for gold as long as they have a shovel
     * and they haven't dug in the town yet
     * @param hunter Allows the code to access the hunter's info and
     *               decide actions accordingly
     */
    public void digForGold(Hunter hunter) {
        String print = "";
        if (hunter.hasItemInKit("shovel")) {
            if (dug) {
                print += ("You already dug for gold in this town.");
            } else if ((int) (Math.random() * 2) == 1) {
                int numOfGold = (int) (Math.random() * 20 + 1);
                hunter.changeGold(numOfGold);
                print += ("You dug up ");
                window.addTextToWindow(print, Color.black);
                print = "";
                window.addTextToWindow(numOfGold + " gold!", Color.yellow);
            } else {
                print += ("You dug but only found dirt");
            }
            dug = true;
        } else {
            print += ("You can't dig for gold without a shovel");
        }
        window.addTextToWindow(print, Color.black);
    }

    /**
     * Creates a string that prints the terrain
     * @return returns the description for the terrain
     */
    public void infoString() {
        window.addTextToWindow("This nice little town is surrounded by ", Color.black);
        window.addTextToWindow(terrain.getTerrainName() + ".", Color.cyan);
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = (int) (Math.random() * 6);
        if (rnd == 0) {
            return new Terrain("Mountains", "Rope", window);
        } else if (rnd == 1) {
            return new Terrain("Ocean", "Boat", window);
        } else if (rnd == 2) {
            return new Terrain("Plains", "Horse", window);
        } else if (rnd == 3) {
            return new Terrain("Desert", "Water", window);
        } else if (rnd == 4) {
            return new Terrain("Marsh", "Boots", window);
        } else {
            return new Terrain("Jungle", "Machete", window);
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        if (!easyMode) {
            double rand = Math.random();
            return (rand < 0.5);
        }
        return false;
    }

    /**
     * Enables easy mode for the Town
     */
    public void easyMode() {
        easyMode = true;
    }

    public void samuraiMode() {
        samuraiMode = true;
        shop.setSamurai(true);
    }
    public void searchTreasure() {
        String printMessage = "";
        if (treasureSearched) {
            printMessage += ("You have already searched this town.");
        } else if (treasure.equals("dust")) {
            printMessage += ("You found dust");
        } else {
            treasureSearched = true;
            if (hunter.findItemInTreasureKit(treasure) != -1) {
                printMessage += ("You found a " + treasure + " but you already had it");
            } else {
                hunter.addToTreasureKit(treasure);
                printMessage += ("You found a " + treasure);
            }
        }
        window.addTextToWindow(printMessage, Color.green);
    }
}