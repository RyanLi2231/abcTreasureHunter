import java.awt.*;
import java.util.Scanner;

/**
 * The Shop class controls the cost of the items in the Treasure Hunt game. <p>
 * The Shop class also acts as a go between for the Hunter's buyItem() method. <p>
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Shop {
    // constants
    private static final int WATER_COST = 2;
    private static final int ROPE_COST = 4;
    private static final int MACHETE_COST = 6;
    private static final int HORSE_COST = 12;
    private static final int BOAT_COST = 20;
    private static final int BOOTS_COST = 8;
    private static final int SHOVEL_COST = 8;
    private static final int SWORD_COST = 0;

    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private double markdown;
    private Hunter customer;
    private boolean samurai;
    private OutputWindow window;

    /**
     * The Shop constructor takes in a markdown value and leaves customer null until one enters the shop.
     *
     * @param markdown Percentage of markdown for selling items in decimal format.
     */
    public Shop(double markdown, OutputWindow window) {
        this.window = window;
        this.markdown = markdown;
        customer = null; // customer is set in the enter method
    }

    public void setSamurai(boolean samurai) {
        this.samurai = samurai;
    }

    /**
     * Method for entering the shop.
     *
     * @param hunter the Hunter entering the shop
     * @param buyOrSell String that determines if hunter is "B"uying or "S"elling
     * @return a String to be used for printing in the latest news
     */
    public String enter(Hunter hunter, String buyOrSell) {
        customer = hunter;
        if (buyOrSell.equals("b")) {
            String print = "";
            print += ("Welcome to the shop! We have the finest wares in town.");
            print += ("Currently we have the following items:");
            window.addTextToWindow(print + "\n", Color.black);
            window.addTextToWindow(inventory(), Color.black);
            window.addTextToWindow("What're you lookin' to buy? \n", Color.black);
            String item = SCANNER.nextLine().toLowerCase();
            int cost = checkMarketPrice(item, true);
            if (cost == 0 && !item.equals("sword")) {
                window.clear();
                window.addTextToWindow("We ain't got none of those.\n", Color.black);
            } else {
                if (hunter.hasItemInKit("sword") && !hunter.hasItemInKit(item)) {
                    window.clear();
                    window.addTextToWindow("Oh Legendary Samurai, my store is yours, you can have this, free of cost!\n", Color.black);
                    buyItem(item);
                } else {
                    window.addTextToWindow("It'll cost you ", Color.black);
                    window.addTextToWindow(cost + " gold.\n", Color.yellow);
                    window.addTextToWindow("Buy it (y/n)? ", Color.black);
                    String option = SCANNER.nextLine().toLowerCase();
                    window.clear();
                    if (option.equals("y")) {
                        buyItem(item);
                    }
                }

            }
        } else {
            window.addTextToWindow("What're you lookin' to sell? \n", Color.black);
            window.addTextToWindow("You currently have the following items: \n", Color.black);
            window.addTextToWindow(customer.getInventory() + "\n", Color.magenta);
            String item = SCANNER.nextLine().toLowerCase();
            int cost = checkMarketPrice(item, false);
            if (cost == 0) {
                window.addTextToWindow("We don't want none of those.", Color.black);
            } else {
                window.addTextToWindow("It'll get you ", Color.black);
                window.addTextToWindow(cost + " gold.\n", Color.yellow);
                window.addTextToWindow("Sell it (y/n)? ", Color.black);
                String option = SCANNER.nextLine().toLowerCase();
                window.clear();
                if (option.equals("y")) {
                    sellItem(item);
                }
            }
        }
        window.addTextToWindow("You left the shop", Color.black);
        return "";
    }

    /**
     * A method that returns a string showing the items available in the shop
     * (all shops sell the same items).
     *
     * @return the string representing the shop's items available for purchase and their prices.
     */
    public String inventory() {
        String str = "Water: " + WATER_COST + " gold\n";
        str += "Rope: " + ROPE_COST + " gold\n";
        str += "Machete: " + MACHETE_COST + " gold\n";
        str += "Boots: " + BOOTS_COST + " gold\n";
        str += "Horse: " + HORSE_COST + " gold\n";
        str += "Boat: " + BOAT_COST + " gold\n";
        str += "Shovel: " + SHOVEL_COST + " gold\n";
        if (samurai) {
            str += "Sword: " + SWORD_COST + " gold\n";
        }
        return str;
    }

    /**
     * A method that lets the customer (a Hunter) buy an item.
     *
     * @param item The item being bought.
     */
    public void buyItem(String item) {
        int costOfItem = checkMarketPrice(item, true);
        if (item.equals("sword") && !samurai) {
            window.addTextToWindow("You can't buy this!", Color.black);
        } else if (customer.buyItem(item, costOfItem)) {
            if (!samurai) {
                window.addTextToWindow("Ye' got yerself a ", Color.black);
                window.addTextToWindow(item, Color.magenta);
                window.addTextToWindow(". \nCome again soon.\n", Color.black);
            }
        } else {
            window.addTextToWindow("Hmm, either you don't have enough gold or you've already got one of those!", Color.black);
        }
    }

    /**
     * A pathway method that lets the Hunter sell an item.
     *
     * @param item The item being sold.
     */
    public void sellItem(String item) {
        int buyBackPrice = checkMarketPrice(item, false);
        if (customer.sellItem(item, buyBackPrice)) {
            window.addTextToWindow("Pleasure doin' business with you.\n", Color.black);
        } else {
            window.addTextToWindow("Stop stringin' me along!\n",Color.black);
        }
    }

    /**
     * Determines and returns the cost of buying or selling an item.
     *
     * @param item The item in question.
     * @param isBuying Whether the item is being bought or sold.
     * @return The cost of buying or selling the item based on the isBuying parameter.
     */
    public int checkMarketPrice(String item, boolean isBuying) {
        if (isBuying) {
            return getCostOfItem(item);
        } else {
            return getBuyBackCost(item);
        }
    }

    /**
     * Checks the item entered against the costs listed in the static variables.
     *
     * @param item The item being checked for cost.
     * @return The cost of the item or 0 if the item is not found.
     */
    public int getCostOfItem(String item) {
        if (item.equals("water")) {
            return WATER_COST;
        } else if (item.equals("rope")) {
            return ROPE_COST;
        } else if (item.equals("machete")) {
            return MACHETE_COST;
        } else if (item.equals("horse")) {
            return HORSE_COST;
        } else if (item.equals("boat")) {
            return BOAT_COST;
        } else if (item.equals("boots")) {
            return BOOTS_COST;
        } else if (item.equals("shovel")) {
            return SHOVEL_COST;
        } else if (item.equals("sword")) {
            return SWORD_COST;
        }
        else {
            return 0;
        }
    }

    /**
     * Checks the cost of an item and applies the markdown.
     *
     * @param item The item being sold.
     * @return The sell price of the item.
     */
    public int getBuyBackCost(String item) {
        int cost = (int) (getCostOfItem(item) * markdown);
        return cost;
    }
}