package Sender;

import Shipments.Parcels.Parcel;
import Shipments.Response;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Sender {
    protected Socket socket;
    protected ObjectOutputStream objectOutputStream;

    /**
     * Creates an instance of a Sender.
     */
    public Sender() {
        socket = null;
    }

    /**
     * Sends a Parcel.
     * @param parcel the Parcel to send
     * @return a Response explaining what happened.
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
                System.out.println("created objectoutputstream");
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
     * Disconnects the server from the current connection.
     */
    public Response disconnect() {
        try {
            socket.close();
            System.out.println("closed connection to " + socket.getInetAddress().toString().substring(1));
            return Response.SUCCESS;
        } catch (IOException e) {
            System.out.println("unable to disconnect from server");
            return Response.DISCONNECT_ERROR;
        } catch (NullPointerException e) {
            return Response.NOSUCHSERVER_ERROR;
        }
    }

    //sends parcel (assumes the sender is already connected)
    protected Response send(Parcel parcel) {
        try {
            objectOutputStream.writeObject(parcel);
            System.out.println("successfully sent parcel");
            return Response.SUCCESS;
        } catch (SocketException e) {
            System.out.println("error sending");
            return Response.SEND_ERROR;
        } catch (IOException e) {
            System.out.println("unable to send parcel");
            e.printStackTrace();
            return Response.SEND_ERROR;
        }
    }
}
