package Main;

import PostOffice.PostOffice;
import Parcels.Letter;

import java.util.Scanner;

/**
 * A class that instantiates a PostOffice, asking the user what it wants to do.
 */
public class OpenPostOffice {
    private static String[] actions = new String[]{"send a letter"};
    private static PostOffice postOffice = new PostOffice();;

    /**
     * The main method that opens the PostOffice and asks what to do.
     * @param args
     */
    public static void main(String[] args) {
        ask();
    }

    private static void ask() {
        Scanner sc = new Scanner(System.in);
        System.out.println("what do you want to do?");
        for(String s : actions) System.out.println(s.charAt(0) + " - " + s);
        char action = Character.toLowerCase(sc.next().charAt(0));
        switch(action) {
            case 's':   writeLetter();
                        break;
            case 'q':   System.exit(0);
        }
    }

    private static void writeLetter() {
        Scanner sc = new Scanner(System.in);
        System.out.println("whats your name?");
        String name = sc.nextLine();
        System.out.println("what is the server (ip:port)?");
        String server = sc.nextLine();
        System.out.println("who is the recipient (ip:port)?");
        String recipient = sc.nextLine();
        System.out.println("what is the title?");
        String title = sc.nextLine();
        System.out.println("what is the message?");
        String message = sc.nextLine();
        Letter letter = new Letter(name, server, recipient, title, message);
        System.out.println("preview of the message:\n" + letter.toString());
        System.out.println("do you want to send?");
        char ans = sc.next().charAt(0);
        switch(ans) {
            case 'y':
                int response = postOffice.sendParcels(letter);
                if(response == 0)
                    System.out.println("letter sent");
                else if(response > 0)
                    System.out.println("connection was refused by the server");
                else
                    System.out.println("unable to send letter");
                break;
            default:
                System.out.println("letter discarded");
                break;
        }
    }
}
