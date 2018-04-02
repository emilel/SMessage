package Main;

import Receiver.*;
import Person.Person;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

/**
 * Opens a Harbor and controls it via the command line. Among possible user actions are:
 *      Quit (q)
 *      Help (h)
 *      Add a command for a client to execute (c)
 *      Add a person allowed to connect to the Harbor (a)
 *      Add an admin allowed to retrieve messages stored on the Harbor (d)
 *      Run setup (r)
 *      View commands admins are allowed to execute (v)
 *      View server information (i)
 *      Save settings to disk (s)
 *      Clear any or all of the settings allowed people, admins and commands (u)
 *
 *      If any settings are changed, user has to save settings manually to have them saved to disk unless running the
 *      setup.
 */
public class CHarbor {
    private static Harbor harbor;
    private static Map<Character, String> actions = new HashMap<>();
    private static Scanner sc = new Scanner(System.in);

    /**
     * Opens a Harbor and starts the program. Checks if it is setup and runs a setup wizard if it is not. Initializes
     * the Harbor, and if it is successful the program starts and the user can choose what to do by entering characters
     * and pressing enter. If the Harbor was not able to connect to a network, an attempt is made every five seconds
     * and if it still isn't successful for a minute, the program exits.
     * @param args
     */
    public static void main(String[] args) {
        actions.put('q', "quit");
        actions.put('h', "help");
        actions.put('c', "add command");
        actions.put('a', "add allowed person");
        actions.put('d', "add admin");
        actions.put('r', "run setup");
        actions.put('v', "view commands");
        actions.put('i', "view server information");
        actions.put('s', "save settings to disk");
        actions.put('u', "clear settings");
        Scanner sc = new Scanner(System.in);

        harbor = new Harbor();
        if(!harbor.isSetUp()) {
            System.out.println("harbor needs to be setup before opening\n");
            setUp();
        }

        int i = 0;
        System.out.println("connecting...");
        while(!harbor.initialize() && i < 60) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) { }
            i++;
        }

        if(i == 10) {
            System.out.println("harbor could not get a connection, exiting program");
            System.exit(0);
        }

        harbor.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) { }
        System.out.println("enter 'h' for help\n");

        while (true) {
            try {
                waitForAction();
            } catch (Exception e) {
                System.out.println("illegal input: " + e.getClass().getSimpleName());
            }
        }
    }

    //waits for the user to input a character representing an action
    private static void waitForAction() {
        char action = Character.toLowerCase(sc.nextLine().charAt(0));
        switch (action) {
            case 'h':
                help();
                break;
            case 'q':
                harbor.close();
                sc.close();
                System.exit(0);
                break;
            case 'c':
                addCommands();
                break;
            case 'a':
                addAllowedPeople();
                break;
            case 'd':
                addAdmins();
                break;
            case 'v':
                viewCommands();
                break;
            case 'r':
                setUp();
                break;
            case 'i':
                viewInformation();
                break;
            case 's':
                harbor.saveSettings();
                break;
            case 'u':
                clearSettings();
                break;
            default:
                System.out.println("unknown action");
                break;
        }
    }

    //allows the user to clear any of the following settings - people who are allowed to connect, admins, commands or
    //everything
    private static void clearSettings() {
        System.out.println("do you want to clear people (p), admins (a), commands (c) or everything (e)?");
        switch (Character.toLowerCase(sc.nextLine().charAt(0))) {
            case 'p':
                harbor.clearAllowedPeople();
                break;
            case 'a':
                harbor.clearAdmins();
                break;
            case 'c':
                harbor.clearCommands();
                break;
            case 'e':
                harbor.clearAllowedPeople();
                harbor.clearAdmins();
                harbor.clearCommands();
                break;
            default:
                System.out.println("unknown action");
                break;
        }
    }

    //prints the server information
    private static void viewInformation() {
        System.out.println(harbor.getInformation());
        System.out.println("enter 'h' for help");
    }

    //prints all of the possible actions
    private static void help() {
        System.out.println("available options:");
        for (Map.Entry<Character, String> a : actions.entrySet())
            System.out.println(a.getKey() + " - " + a.getValue());
    }

    //prints the commands an admin is allowed to execute
    private static void viewCommands() {
        System.out.println("the following commands are available:");
        if(harbor.getCommands().isEmpty()) System.out.println("no available commands");
        for (Map.Entry<Integer, String> c : harbor.getCommands().entrySet())
            System.out.println(c.getKey() + " - " + c.getValue());
    }

    //adds a command an admin is allowed to execute
    private static void addCommands() {
        System.out.println("enter the commands you wish to be able to execute followed by a colon and a number to" +
                            "represent it (command:number), enter 'q' when finished");
        HashSet<String[]> commands = new HashSet<>();
        while(true) {
            String[] ans = sc.nextLine().split(":");
            if (Character.toLowerCase(ans[0].charAt(0)) == 'q') {
                break;
            } else if (ans.length != 2) {
                System.out.println("wrong format of input, try again");
            } else {
                try {
                    commands.add(ans);
                } catch (NumberFormatException e) {
                    System.out.println("unable to parse integer");
                }
            }
        }
        for (String s[] : commands) {
            harbor.addCommand(s[0], Integer.parseInt(s[1]));
        }
    }

    //the setup wizard, where the user gets to add people who are allowed to connect, admins and commands
    private static void setUp() {
        harbor.clearAdmins();
        harbor.clearAllowedPeople();
        harbor.clearCommands();
        System.out.println("---setup---");
        setPort();
        addAdmins();
        addAllowedPeople();
        addCommands();
        saveSettings();
        System.out.println("---setup finished---\n");
    }

    private static void saveSettings() {
        harbor.saveSettings();
    }

    //adds people who are allowed to connect
    private static void addAllowedPeople() {
        System.out.println("add allowed people (name:ip) separated by new line, enter 'q' when finished");
        HashSet<Person> allowedPeople = new HashSet<>();
        while(true) {
            String[] ans = sc.nextLine().split(":");
            if(ans[0].charAt(0) == 'q') break;
            else if(ans.length > 1) {
                allowedPeople.add(new Person(ans[0], ans[1], false));
            } else {
                System.out.println("please enter valid person or enter 'q'");
            }
        }
        harbor.addAllowedPeople(allowedPeople);
    }

    //sets the port for the harbor to use
    private static void setPort() {
        System.out.println("which port do you want to use?");
        int port;
        try {
            port = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("unable to parse integer, default value set (" + Harbor.DEFAULT_PORT + ")");
            port = Harbor.DEFAULT_PORT;
        }
        harbor.setPort(port);
    }

    //adds an admin to the harbor
    private static void addAdmins() {
        System.out.println("add admins (name:ip) separated by new line, enter 'q' when finished");
        HashSet<Person> admins = new HashSet<Person>();
        while(true) {
            String[] ans = sc.nextLine().split(":");
            if(ans[0].charAt(0) == 'q') break;
            else if(ans.length > 1) {
                admins.add(new Person(ans[0], ans[1], true));
            } else {
                System.out.println("please enter valid admins or enter 'q'");
            }
        }
        harbor.addAdmins(admins);
    }
}
