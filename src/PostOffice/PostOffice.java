package PostOffice;

import Parcels.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class PostOffice {
    private Map<String, ObjectOutputStream> connections;

    /**
     * Creates an instance of a PostOffice.
     */
    public PostOffice() {
        connections = new HashMap<>();
    }

    /**
     * Sends one or more parcels.
     * @param parcels the parcels to send
     * @return 0 if all the parcels were successfully sent, otherwise the number of the (first) parcel that failed.
     *          If it failed connecting, the number is positive. If it failed sending, the number is negative.
     *          Index starts at one.
     */
    public int sendParcels(Parcel... parcels) {
        for(int i = 0; i < parcels.length; i++) {
            if(!connect(parcels[i].getTarget())) return i + 1;
            if(!send(parcels[i])) return - i - 1;
        }
        return 0;
    }

    /**
     * Tries to connect the PostOffice to the server, prints the error to the console if it failed.
     * @param server the server to connect to (ip address and port separated by a comma, ip:port)
     * @return true if the connection was successfully made, otherwise false
     */
    public boolean connect(String server) {
        String[] ipAndPort = server.split(":");
        try {
            connections.putIfAbsent(ipAndPort[0], new ObjectOutputStream((new Socket(ipAndPort[0], Integer.parseInt(ipAndPort[1]))).getOutputStream()));
            System.out.println("connected to server");
            return true;
        } catch(IOException e) {
            System.out.println("unable to connect to server");
            System.out.println(e);
            return false;
        } catch(NumberFormatException e) {
            System.out.println("not a valid port");
            System.out.println(e);
            return false;
        }
    }

    /**
     * Disconnects the server from all the current connections.
     * @param servers the servers to disconnect from
     * @return true
     */
    public boolean disconnect(String... servers) {
            for(String s : servers) {
                try {
                    connections.get(s).close();
                    connections.remove(s);
                } catch(IOException e) {
                    System.out.println("unable to disconnect from server");
                }
            }
        return true;
    }

    private boolean send(Parcel parcel) {
        try {
            connections.get(parcel.getRecipient().split(":")[0]).writeObject(parcel);
            System.out.println("letter sent");
            return true;
        } catch(IOException e) {
            System.out.println("unable to send package");
            return false;
        }
    }
}
