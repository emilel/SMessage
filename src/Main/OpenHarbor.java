package Main;

import Harbor.*;

import java.util.HashSet;
import java.util.Scanner;

/**
 * Instantiates a Harbor and opens it.
 */
public class OpenHarbor {
    static Harbor harbor;

    /**
     * Opens the harbor and checks if it is set up. If it is, a set up is run, otherwise the harbor opens.
     * @param args
     */
    public static void main(String[] args) {
        harbor = new Harbor();
        if(!harbor.isSetUp()) {
            setUp();
            harbor.saveSettings();
        }
        harbor.run();
    }

    /**
     * Sets up the Harbor.
     */
    public static void setUp() {
        Scanner sc = new Scanner(System.in);
        System.out.println("harbor is not setup.");
        System.out.println("which port do you want to use?");
        int port = Integer.parseInt(sc.nextLine());
        System.out.println("how many connections do you want to allow simultaneously?");
        int maximumNumberOfConnections = Integer.parseInt(sc.nextLine());
        System.out.println("add allowed ips, enter 'q' when finished");
        HashSet<String> allowedIps = new HashSet<>();
        while(true) {
            String ans = sc.nextLine();
            if(!(ans.charAt(0) == 'q')) allowedIps.add(ans);
            else break;
        }
        harbor.setUp(port, maximumNumberOfConnections, allowedIps);
    }
}
