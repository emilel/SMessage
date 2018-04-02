package Sender;

import Shipments.Parcels.Container;
import Shipments.Parcels.EmptyContainer;
import Shipments.Request;
import Shipments.Parcels.RequestParcel;
import Shipments.Response;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * A class that extends a Sender. It can not only send Parcels, but also send requests to Harbors.
 */
public class Post extends Sender {

    /**
     * Constructor for the Post.
     */
    public Post() {
        super();
    }


    /**
     * Tries to download all the stored Parcels on a Harbor.
     * @param target the Harbor to download the Parcels from
     * @return a Container containing all the Parcels stored on the Harbor, or an EmptyContainer with an error message
     */
    public Container downloadAll(String target) {
        if (sendContainerRequest(new RequestParcel(target, "download all request",
                Request.DOWNLOAD_ALL)) == Response.SUCCESS) {
            return getContainer();
        } else {
            return new EmptyContainer("error while sending");
        }
    }

    /**
     * Tries to download the Parcels not previously downloaded from the Harbor.
     * @param target the Harbor to download the Parcels from
     * @return a Container containing all the Parcels stored on the Harbor, or an EmptyContainer with an error message
     */
    public Container downloadNew(String target) {
        if (sendContainerRequest(new RequestParcel(target, "download new request",
                Request.DOWNLOAD_NEW)) == Response.SUCCESS) {
            return getContainer();
        } else {
            return new EmptyContainer("error while sending");
        }
    }

    //sends a parcel containing a request for a container to the harbor
    private Response sendContainerRequest(RequestParcel requestParcel) {
        if (connect(requestParcel.getTarget()) != Response.SUCCESS) return Response.CONNECT_ERROR;
        if (send(requestParcel) != Response.SUCCESS) return Response.SEND_ERROR;
        return Response.SUCCESS;
    }

    //retrieves the container the server responds with, or if it failed, returns an empty container containing
    //the reason why it failed
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
}
