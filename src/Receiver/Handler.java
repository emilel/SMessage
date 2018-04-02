package Receiver;

import Person.Person;
import Shipments.Parcels.Command;
import Shipments.Parcels.Container;
import Shipments.Parcels.EmptyContainer;
import Shipments.Parcels.Parcel;
import Shipments.Request;
import Shipments.Parcels.RequestParcel;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * The class a Harbor uses to handle connections to clients, and handles the incoming Parcels.
 */
public class Handler extends Thread {
    private Socket socket;
    private Harbor harbor;
    private String connectorIp;

    /**
     * Constructor for the Handler.
     * @param socket the Socket connecting the Handler to the client
     * @param harbor the Harbor this Handler belongs to
     */
    Handler(Socket socket, Harbor harbor) {
        this.socket = socket;
        this.harbor = harbor;
        this.connectorIp = this.socket.getInetAddress().toString().substring(1);
    }

    /**
     * Receives Parcels from the client and handles them. This method can run on it's own thread, by calling the
     * Handler's method start().
     */
    @Override
    public void run() {
        System.out.println("connected to " + connectorIp + " on port " + socket.getPort() + "\n");

        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            while (true) {
                try {
                    Parcel parcel = (Parcel) objectInputStream.readObject();
                    System.out.println("parcel received");
                    handle(parcel);
                } catch (ClassNotFoundException e) {
                    System.out.println("unknown object received");
                }
            }
        } catch (EOFException e) {
            System.out.println("stream ended");
        } catch (SocketException e) {
            System.out.println("socket closed");
        } catch (IOException e) {
            System.out.println("an io error occured");
            e.printStackTrace();
        }
        System.out.println("connection " + " to " + connectorIp + " on port " + socket.getPort() + " ended\n");
    }

    //distributes a parcel not intended for this Harbor, and adds to the parcel the external IP address of this server
    // that distributed it. Uses the Sender of this Handler's harbor to distribute the parcel.
    private void distribute(Parcel parcel) {
        harbor.sendParcel(parcel.distribute(harbor.getExternalIp()));
        System.out.println("shipment sent for distribution");
    }

    //sends all the parcels that have not been previously retrieved to the client this Handler is connected to
    private void sendNew() {
        sendContainer(new Container(harbor.getExternalIp(), connectorIp, "All new Parcels from Harbor",
                        harbor.getNewParcels()));
        harbor.clearNewParcels();
        System.out.println("sent all parcels not previously retrieved to an admin");
    }

    //sends all parcels stored on this Harbor to the client this Handler is connected to
    private void sendAll() {
        sendContainer(new Container(harbor.getExternalIp(), connectorIp, "All Parcels stored on Harbor", harbor.getParcels()));
        System.out.println("sent all stored parcels to an admin");
    }

    //sends a container containing parcels to the client this Handler is connected to
    private void sendContainer(Container container) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            objectOutputStream.writeObject(container);
        } catch (IOException e) {
            System.out.println("an io error occurred");
        }
    }

    //handles incoming parcels and determines if they should be handled by this server or just distributed
    private void handle(Parcel parcel) {
        String recipientIp = parcel.getRecipient().split(":")[0];
        if (recipientIp.equals(harbor.getLocalSiteIp()) || recipientIp.equals(harbor.getExternalIp())) {
            handleServerParcel(parcel.receive(connectorIp));
        } else {
            distribute(parcel.distribute(harbor.getExternalIp()));
        }
    }

    //handles parcels intended for this server. prints them out, and sends them to the right method depending on what
    //type of parcel it is
    private void handleServerParcel(Parcel parcel) {
        System.out.println(parcel + "\n");
        if(parcel instanceof Command) {
            handleCommand((Command) parcel);
        } else if (parcel instanceof RequestParcel) {
            handleRequestParcel((RequestParcel) parcel);
        }
        harbor.store(parcel);
        harbor.saveArchive();
    }

    //handles a parcel containing a request of some sort
    private void handleRequestParcel(RequestParcel requestParcel) {
        if(harbor.getAdmins().contains(new Person(null, connectorIp, false))) {
            switch ((Request) requestParcel.getContent()) {
                case DOWNLOAD_ALL:
                    sendAll();
                    break;
                case DOWNLOAD_NEW:
                    sendNew();
                    break;
            }
        } else {
            sendContainer(new EmptyContainer("retrieval request denied"));
            System.out.println("a request to retrieve parcels stored was made but was denied due to lack of admin " +
                                "status");
        }
    }

    //handles parcels containing commands (executes them if the sender is an admin)
    private void handleCommand(Command command) {
        try {
            if(harbor.getAdmins().contains(new Person(null, connectorIp, false))) {
                Runtime.getRuntime().exec(harbor.getCommands().get(command.getContent()));
                System.out.println("command executed");
            } else {
                System.out.println("command not allowed due to lack of admin status");
            }
        } catch (SecurityException e) {
            System.out.println("command not allowed");
        } catch (IOException e) {
            System.out.println("an io problem occured");
        }
    }
}
