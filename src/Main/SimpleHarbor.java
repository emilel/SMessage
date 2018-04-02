package Main;

import Receiver.*;
import Person.Person;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

/**
 * Instantiates a Receiver and opens it.
 */
public class SimpleHarbor {
    private static Harbor harbor;
    private static Map<Character, String> actions = new HashMap<>();
    private static Scanner sc = new Scanner(System.in);

    /**
     * Opens the harbor and checks if it is set up. If it is, a set up is open, otherwise the harbor opens.
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
            System.out.println("harbor needs to be setup before opening");
            setUp();
        }
        harbor.start();

        while(!harbor.isRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { }
        }
        System.out.println("enter 'h' for help\n");
        while (true) {
            try {
                askForAction();
            } catch (Exception e) {
                System.out.println("illegal input: " + e.getClass().getSimpleName());
            }
        }
    }

    public static void askForAction() {
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

    private static void clearSettings() {
        System.out.println("\ndo you want to clear people (p), admins (a), commands (c) or everything (e)?");
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

    private static void viewInformation() {
        System.out.println("\n" + harbor.getInformation());
        System.out.println("enter 'h' for help");
    }

    private static void help() {
        System.out.println("\navailable options:");
        for (Map.Entry<Character, String> a : actions.entrySet()) System.out.println(a.getKey() + " - " + a.getValue());
    }

    private static void viewCommands() {
        System.out.println("\nthe following commands are available:");
        if(harbor.getCommands().isEmpty()) System.out.println("no available commands");
        for (Map.Entry<Integer, String> c : harbor.getCommands().entrySet()) System.out.println(c.getKey() + " - " + c.getValue());
    }

    private static void addCommands() {
        System.out.println("\nenter the commands you wish to be able to execute followed by a colon and a number to represent it (command:number), enter 'q' when finished");
        HashSet<String[]> commands = new HashSet<>();
        while(true) {
            String[] ans = sc.nextLine().split(":");
            if (Character.toLowerCase(ans[0].charAt(0)) == 'q') {
                break;
            } else if (ans.length != 2) {
                System.out.println("wrong format of input");
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

    /**
     * Sets up the Receiver.
     */
    private static void setUp() {
        System.out.print("\n---setup---");
        harbor.getAdmins().clear();
        harbor.getAllowedPeople().clear();
        harbor.clearCommands();
        setPort();
        addAdmins();
        addAllowedPeople();
        addCommands();
        harbor.saveSettings();
        System.out.println("---setup finished---\n");
    }

    private static void addAllowedPeople() {
        System.out.println("\nadd allowed people (name:ip) separated by new line, enter 'q' when finished");
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

    private static void setPort() {
        System.out.println("\nwhich port do you want to use?");
        int port;
        try {
            port = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("unable to parse integer, default value set (" + Harbor.DEFAULT_PORT + ")");
            port = Harbor.DEFAULT_PORT;
        }
        harbor.setPort(port);
    }

    private static void addAdmins() {
        System.out.println("\nadd admins (name:ip) separated by new line, enter 'q' when finished");
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
