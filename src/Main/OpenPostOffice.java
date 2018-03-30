package Main;

import Parcels.Command;
import Parcels.Parcel;
import PostOffice.PostOffice;
import Parcels.Letter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

/**
 * A class that instantiates a PostOffice, asking the user what it wants to do.
 */
public class OpenPostOffice {
    private static String[] actions = new String[]{"send a letter"};
    private static PostOffice postOffice = new PostOffice();
    private static HashSet<Parcel> parcelTypes = new HashSet<>();

    /**
     * The main method that opens the PostOffice and asks what to do.
     * @param args
     */
    public static void main(String[] args) {
        askAndSend();
    }

    private static void askAndSend() {
        Scanner sc = new Scanner(System.in);
        System.out.println("what do you want to do?");
        for(Parcel p : Parcel.getParcelTypes()) System.out.println(p.getParcelType().charAt(0) + " - send " + p.getParcelType());
        System.out.println("q - quit");
        char type = Character.toLowerCase(sc.nextLine().charAt(0));
        System.out.println("whats your name?");
        String sender = sc.nextLine();
        System.out.println("what is the server (ip:port)?");
        String server = sc.nextLine();
        System.out.println("who is the recipient (ip:port)?");
        String recipient = sc.nextLine();
        System.out.println("what is the title?");
        String title = sc.nextLine();
        System.out.println("what is the content?");
        String content = sc.nextLine();
        Parcel parcel;
        switch(type) {
            case 'l':
                parcel = new Letter(sender, server, recipient, title, content);
                break;
            case 'c':
                parcel = new Command(sender, server, recipient, title, Integer.parseInt(content));
                break;
            case 'q':
                System.exit(0);
        }
        Letter letter = new Letter(sender, server, recipient, title, content);
        System.out.println("preview of the parcel:\n" + letter.toString());
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
