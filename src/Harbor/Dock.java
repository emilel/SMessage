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
    private String localSiteIp;

    /**
     * The constructor which creates a new Dock (connection to a host).
     * @param dock the Socket to the host
     * @param externalIp the external ip of this server
     * @param localSiteIp the local site ip of this server
     */
    Dock(Socket dock, String externalIp, String localSiteIp) {
        this.dock = dock;
        this.externalIp = externalIp;
        this.localSiteIp = localSiteIp;
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
                    if(!(receivedParcel.getRecipient().split(":")[0].equals(externalIp) || receivedParcel.getRecipient().split(":")[0].equals(localSiteIp))) {
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
            System.out.println(e);
        } catch(ClassCastException e) {
            System.out.println("unable to cast incoming object to receivedparcel");
            System.out.println(e);
        } catch(ClassNotFoundException e) {
            System.out.println("unknown object received");
            System.out.println(e);
        }
    }

    /**
     * Returns this machine's external ip.
     * @return this machine's external ip.
     */
    public String getExternalIp() {
        return externalIp;
    }

    /**
     * Returns this machine's local site ip.
     * @return this machine's local site ip.
     */
    public String getLocalSiteIp() {
        return localSiteIp;
    }

    //TODO: distribute received parcel to other server
    private void distribute(ReceivedParcel receivedParcel) {

    }

    //TODO: handle received parcel
    private void handle(ReceivedParcel receivedParcel) {

    }
}
