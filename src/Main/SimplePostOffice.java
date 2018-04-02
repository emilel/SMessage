package Main;

import Shipments.Parcels.Command;
import Shipments.Parcels.Parcel;
import Sender.Sender;
import Shipments.Parcels.Letter;
import Shipments.Response;

import java.util.Scanner;

/**
 * A class that instantiates a Sender, asking the user what it wants to do.
 */
public class SimplePostOffice {
    private static Sender postOffice = new Sender();

    /**
     * The main method that opens the Sender and asks what to do.
     * @param args
     */
    public static void main(String[] args) {
        askAndSend();
    }

    private static void askAndSend() {
        Scanner sc = new Scanner(System.in);
        System.out.println("what do you want to do?");
        for(Parcel p : Parcel.parcelTypes) System.out.println(Character.toLowerCase(p.getParcelType().charAt(0)) + " - send " + p.getParcelType().toLowerCase());
        System.out.println("q - quit\n");
        char type = Character.toLowerCase(sc.nextLine().charAt(0));
        System.out.println("\nwhats your name?");
        String sender = sc.nextLine();
        System.out.println("\nwhat is the server (ip:port)?");
        String server = sc.nextLine();
        System.out.println("\nwho is the recipient (ip:port)?");
        String recipient = sc.nextLine();
        System.out.println("\nwhat is the title?");
        String title = sc.nextLine();
        System.out.println("\nwhat is the content?");
        String content = sc.nextLine();
        Parcel parcel = null;
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
        System.out.println("\n" + parcel.toString());
        System.out.println("\ndo you want to send?");
        char ans = sc.next().charAt(0);
        switch(ans) {
            case 'y':
                Response response = postOffice.sendParcel(parcel);
                if(response == Response.SUCCESS)
                    System.out.println(parcel.getParcelType().toLowerCase() + " sent");
                else if(response == Response.CONNECT_ERROR)
                    System.out.println("unable to connect to server");
                else
                    System.out.println("unable to send " + parcel.getParcelType().toLowerCase());
                break;
            default:
                System.out.println(parcel.getParcelType().toLowerCase() + " discarded");
                break;
        }
    }
}
