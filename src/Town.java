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

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        treasureSearched = false;
        treasure = randomizeTreasure();
    }

    public String randomizeTreasure() {
        int number = (int) (Math.random() * 4);
        if (number == 0) {
            return "dust";
        } else if (number == 1) {
            return "crown";
        } else if (number == 2) {
            return "trophy";
        } else {
            return "gem";
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
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + Colors.PURPLE + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, your " + Colors.PURPLE + item + " broke.";
            }
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
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
            System.out.println("You couldn't find any trouble");
        } else {
            System.out.println(Colors.RED +"You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n");
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > noTroubleChance) {
                System.out.println(Colors.RED + "Okay, stranger! You proved yer mettle. Here, take my gold.");
                System.out.println("\nYou won the brawl and receive " + goldDiff + " gold.");
                hunter.changeGold(goldDiff);
            } else {
                System.out.println(Colors.RED + "That'll teach you to go lookin' fer trouble in MY town! Now pay up!");
                System.out.println(Colors.RED + "\nYou lost the brawl and pay " + goldDiff + " gold.");
                hunter.changeGold(-goldDiff);
                if (hunter.getGold() < 0) {
                    return "end";
                }
            }
        }
        return "";
    }

    public String infoString() {
        return "This nice little town is surrounded by " + Colors.CYAN + terrain.getTerrainName() + "." + Colors.RESET;
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = (int) (Math.random() * 6);
        if (rnd == 0) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd == 1) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd == 2) {
            return new Terrain("Plains", "Horse");
        } else if (rnd == 3) {
            return new Terrain("Desert", "Water");
        } else if (rnd == 4) {
            return new Terrain("Marsh", "Boots");
        } else {
            return new Terrain("Jungle", "Machete");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }

    public boolean isTreasureSearched() {
        return treasureSearched;
    }

    public void setTreasureSearched() {
        treasureSearched = true;
    }
}