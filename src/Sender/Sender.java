package Sender;

import Shipments.Parcels.Parcel;
import Shipments.Response;
import Shipments.Parcels.Container;
import Shipments.Parcels.EmptyContainer;
import Shipments.Request;
import Shipments.RequestParcel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Sender {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;

    /**
     * Creates an instance of a Sender.
     */
    public Sender() {
        socket = null;
    }

    /**
     * Sends one shipment.
     * @param parcel the shipment to send
     * @return 0 if all the parcels were successfully sent, otherwise the number of the (first) shipment that failed.
     *          If it failed connecting, the number is positive. If it failed sending, the number is negative.
     *          Index starts at one.
     */
    public Response sendParcel(Parcel parcel) {
        if (connect(parcel.getTarget()) != Response.SUCCESS) return Response.CONNECT_ERROR;
        else if (send(parcel) != Response.SUCCESS) return Response.SEND_ERROR;
        else return Response.SUCCESS;
    }

    /**
     * Tries to connect the Sender to the server, prints the error to the console if it failed.
     * @param server the server to connect to (ip address and port separated by a comma, ip:port)
     * @return true if the socket was successfully made, otherwise false
     */
    public Response connect(String server) {
        String[] ipAndPort = server.split(":");
        try {
            if(socket == null || !socket.getInetAddress().toString().substring(1).equals(ipAndPort[0])) {
                disconnect();
                socket = new Socket(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
                System.out.println("connected to server");
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            }
            return Response.SUCCESS;
        } catch (IOException e) {
            System.out.println("unable to connect to server");
            return Response.CONNECT_ERROR;
        } catch (NumberFormatException e) {
            System.out.println("not a valid port");
            return Response.INVALID_FORMAT_ERROR;
        }
    }

    /**
     * Disconnects the server from all the current connections.
     */
    public Response disconnect() {
                try {
                    socket.close();
                    System.out.println("closed socket to " + socket.getInetAddress().toString());
                    return Response.SUCCESS;
                } catch (IOException e) {
                    System.out.println("unable to disconnect from server");
                    return Response.DISCONNECT_ERROR;
                } catch (NullPointerException e) {
                    return Response.NOSUCHSERVER_ERROR;
                }
    }

    private Response send(Parcel parcel) {
        try {
            objectOutputStream.writeObject(parcel);
            System.out.println("successfully sent parcel");
            return Response.SUCCESS;
        } catch (IOException e) {
            System.out.println("unable to send parcel");
            e.printStackTrace();
            return Response.SEND_ERROR;
        }
    }

    private Response sendContainerRequest(RequestParcel requestParcel) {
        if (connect(requestParcel.getTarget()) != Response.SUCCESS) return Response.CONNECT_ERROR;
        if (send(requestParcel) != Response.SUCCESS) return Response.SEND_ERROR;
        return Response.SUCCESS;
    }

    private Container getContainer() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return (Container) objectInputStream.readObject();
        } catch (IOException e) {
            System.out.println("an io error occurred while trying to receive response from server");
            return new EmptyContainer("error receiving response from server");
        } catch (ClassNotFoundException e) {
            System.out.println("received unknown response from server");
            return new EmptyContainer("unknown response from server");
        }
    }

    public Container downloadAll(String target) {
        if (sendContainerRequest(new RequestParcel(target, "download all request", Request.DOWNLOAD_ALL)) == Response.SUCCESS) {
            return getContainer();
        } else {
            return new EmptyContainer("error while sending");
        }
    }

    public Container downloadNew(String target) {
        if (sendContainerRequest(new RequestParcel(target, "download new request", Request.DOWNLOAD_NEW)) == Response.SUCCESS) {
            return getContainer();
        } else {
            return new EmptyContainer("error while sending");
        }
    }
}
