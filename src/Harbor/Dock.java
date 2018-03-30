//TODO: receive package, distribute or handle

package Harbor;

import Parcels.Parcel;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import Parcels.Parcel.ReceivedParcel;
import Parcels.Parcel.DistributedParcel;
import PostOffice.PostOffice;

/**
 * The class representing a connection to a PostOffice, able to receive packages and run independently as a Thread.
 */
public class Dock extends Thread {
    private Socket dock;
    private String externalIp;
    private String localSiteIp;
    private PostOffice postOffice;

    /**
     * The constructor which creates a new Dock (connection to a host).
     * @param dock the Socket to the host
     * @param externalIp the external ip of this server
     * @param localSiteIp the local site ip of this server
     */
    Dock(Socket dock, String externalIp, String localSiteIp, PostOffice postOffice) {
        this.dock = dock;
        this.externalIp = externalIp;
        this.localSiteIp = localSiteIp;
        this.postOffice = postOffice;
    }

    /**
     * Receives a package from the host and distributes.
     */
    @Override
    public void run() {
        try {
            System.out.println("connected to " + dock.getInetAddress() + " on port " + dock.getPort() + "\n");
            ObjectInputStream conveyor = new ObjectInputStream(dock.getInputStream());
            while (true) {
                try {
                    Parcel parcel = (Parcel) conveyor.readObject();
                    System.out.println("parcel received");
                    handleIncoming(parcel);
                } catch (EOFException e) {
                    dock.close();
                    System.out.println("\nconnection " + " to " + dock.getInetAddress() + " on port " + dock.getPort() + " ended\n");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("unable to put up objectinputstream");
        } catch (ClassCastException e) {
            System.out.println("unable to cast incoming object to parcel");
        } catch (ClassNotFoundException e) {
            System.out.println("unknown object received");
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

    private void distribute(DistributedParcel distributedParcel) {
        postOffice.sendParcels(distributedParcel.distribute(externalIp));
    }

    private void handleIncoming(Parcel parcel) {
        String recipient = parcel.getRecipient().split(":")[0];
        if (recipient.equals(localSiteIp) || recipient.equals(externalIp)) {
            handleServerParcel(parcel.receive(dock.getInetAddress().toString()));
        } else {
            distribute(parcel.distribute(externalIp));
            System.out.println("parcel sent for distribution");
        }
    }

    //TODO: handleServerParcel parcel intended for server
    private void handleServerParcel(ReceivedParcel receivedParcel) {
        System.out.println(receivedParcel);
    }
}
