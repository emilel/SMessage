package Main;

import Harbor.*;

/**
 * Instantiates a Harbor and opens it.
 */
public class OpenHarbor {
    public static void main(String[] args) {
        Harbor harbor = new Harbor(1337, 3);
        harbor.open();
    }
}
