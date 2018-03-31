package Receiver;

import Parcels.Command;
import Parcels.Parcel;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Map;

import Sender.Sender;
import Parcels.Archive;

/**
 * The class representing a connection to a Sender, able to receive packages and open independently as a Thread.
 */
public class Handler extends Thread {
    private Socket dock;
    private String externalIp;
    private String localSiteIp;
    private Sender postOffice;
    private Archive archive;
    private Map<Integer, String> commands;

    /**
     * The constructor which creates a new Handler (connection to a host).
     * @param dock the Socket to the host
     * @param externalIp the external ip of this server
     * @param localSiteIp the local site ip of this server
     */
    Handler(Socket dock, String externalIp, String localSiteIp, Sender postOffice, Archive parcelList, Map<Integer, String> commands) {
        this.dock = dock;
        this.externalIp = externalIp;
        this.localSiteIp = localSiteIp;
        this.postOffice = postOffice;
        this.archive = parcelList;
        this.commands = commands;
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
                    while (true) {
                        Parcel parcel = (Parcel) conveyor.readObject();
                        System.out.println("parcel received");
                        handleIncoming(parcel);
                    }
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
            System.out.println(e.toString());
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

    private void distribute(Parcel parcel) {
        postOffice.sendParcels(parcel.distribute(externalIp));
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
    
    private void handleServerParcel(Parcel parcel) {
        System.out.println(parcel + "\n");
        archive.addParcel(parcel);
        archive.saveParcelList();
        if(parcel instanceof Command) {
            handleCommand((Command) parcel);
        }
    }

    private void handleCommand(Command command) {
        try {
            Runtime.getRuntime().exec(commands.get(command.getContent()));
        } catch (SecurityException e) {
            System.out.println("command not allowed");
        } catch (IOException e) {
            System.out.println("an io exception occured");
        }
    }
}
