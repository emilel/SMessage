package Receiver;

import Shipments.*;
import Person.Person;
import Shipments.Parcels.Command;
import Shipments.Parcels.Container;
import Shipments.Parcels.EmptyContainer;
import Shipments.Parcels.Parcel;
import Shipments.Request;
import Shipments.RequestParcel;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * The class representing a connection to a Sender, able to receive packages and open independently as a Thread.
 */
public class Handler extends Thread {
    private Socket socket;
    private Harbor harbor;
    private String connectorIp;
    /**
     * The constructor which creates a new Handler (connection to a host).
     * @param dock the Socket to the host
     */
    Handler(Socket dock, Harbor harbor) {
        this.socket = dock;
        this.harbor = harbor;
        this.connectorIp = socket.getInetAddress().toString().substring(1);
    }

    /**
     * Receives a package from the host and distributes.
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

    /**
     * Returns this machine's local site connectorIp.
     * @return this machine's local site connectorIp.
     */

    private void distribute(Parcel parcel) {
        harbor.sendParcel(parcel.distribute(harbor.getExternalIp()));
        System.out.println("shipment sent for distribution");
    }

    private void sendNew() {
        if(harbor.getAdmins().contains(new Person(null, connectorIp, false))) {
            sendContainer(new Container(harbor.getExternalIp(), connectorIp, "All new Parcels from Harbor", harbor.getNewParcels()));
            harbor.clearNewShipments();
            System.out.println("sent all parcels not previously retrieved to an admin");
        } else {
            sendContainer(new EmptyContainer("retrieval request denied"));
            System.out.println("a request for all new parcels was made but was denied due to lack of admin status");
        }
    }
//TODO: send through the already open socket god damn it
    private void sendAll() {
        if(harbor.getAdmins().contains(new Person(null, connectorIp, false))) {
            sendContainer(new Container(harbor.getExternalIp(), connectorIp, "All Parcels stored on Harbor", harbor.getParcels()));
            System.out.println("sent all stored parcels to an admin");
        } else {
            sendContainer(new EmptyContainer("retrieval request denied"));
            System.out.println("a request for all parcels stored was made but was denied due to lack of admin status");
        }
    }

    private void sendContainer(Container container) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            objectOutputStream.writeObject(container);
        } catch (IOException e) {
            System.out.println("an io error occurred");
        }
    }

    private void handle(Parcel parcel) {
        String recipientIp = parcel.getRecipient().split(":")[0];
        if (recipientIp.equals(harbor.getLocalSiteIp()) || recipientIp.equals(harbor.getExternalIp())) {
            handleServerParcel(parcel.receive(connectorIp));
        } else {
            distribute(parcel.distribute(harbor.getExternalIp()));
        }
    }
    
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

    private void handleRequestParcel(RequestParcel requestParcel) {
        switch ((Request) requestParcel.getContent()) {
            case DOWNLOAD_ALL:
                sendAll();
                break;
            case DOWNLOAD_NEW:
                sendNew();
                break;
            case NEW_MESSAGES:
                sendIfNewMessages();
                break;
        }
    }

    private void sendIfNewMessages() {
        if(harbor.getNewParcels().isEmpty()) harbor.sendResponse(Response.NO_NEW_PARCELS, socket);
        else harbor.sendResponse(Response.NEW_PARCELS, socket);
    }

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
