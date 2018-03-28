//TODO: ta emot paket, skicka vidare, få svar, kommandon

package Harbor;

import Parcels.Parcel;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import Parcels.Parcel.ReceivedParcel;

/**
 * The class representing a connection to a PostOffice, able to receive packages and run independently as a Thread.
 */
public class Dock extends Thread {
    private Socket dock;
    private String externalIp;
    private String internalIp;

    /**
     * The constructor which creates a new Dock (connection to a host).
     * @param dock the Socket to the host
     * @param externalIp the external ip of this server
     * @param host the name of this server
     */
    Dock(Socket dock, String externalIp, String host) {
        this.dock = dock;
        this.externalIp = externalIp;
        this.internalIp = host.split("/")[1];
    }

    /**
     * Receives a package from the host and distributes.
     */
    @Override
    public void run() {
        try {
            System.out.println("connected to " + dock.getInetAddress() + " on port " + dock.getPort() + "\n");
            ObjectInputStream conveyor = new ObjectInputStream(dock.getInputStream());
            while(true) {
                try {
                    ReceivedParcel receivedParcel= ((Parcel) conveyor.readObject()).receive();
                    System.out.println(receivedParcel);
                    if(!(receivedParcel.getRecipient().split(":")[0].equals(externalIp) || receivedParcel.getRecipient().split(":")[0].equals(internalIp))) {
                        distribute(receivedParcel);
                    } else {
                        handle(receivedParcel);
                    }
                } catch(EOFException e) {
                    dock.close();
                    System.out.println("\nconnection " + " to " + dock.getInetAddress() + " on port " + dock.getPort() + " ended\n");
                    break;
                }
            }
        } catch(IOException e) {
            System.out.println("unable to put up objectinputstream");
        } catch(ClassCastException e) {
            System.out.println("unable to cast incoming object to receivedparcel");
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            System.out.println("unknown object received");
        }
    }

    //TODO: distribute received parcel to other server
    /**
     * Distributes a received Parcel if it was not intended for this server.
     * @param receivedParcel the incoming Parcel
     */
    private void distribute(ReceivedParcel receivedParcel) {

    }

    //TODO: handle received parcel
    /**
     * Handles the parcel.
     * @param receivedParcel the incoming Parcel
     */
    private void handle(ReceivedParcel receivedParcel) {

    }
}
