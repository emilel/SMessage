package Shipments.Parcels;

import Shipments.Request;

/**
 * A type of Parcel containing a Request.
 */
public class RequestParcel extends Parcel {

    /**
     * Constructs a RequestParcel.
     * @param target the server the Request is intended for
     * @param title the title of the Parcel
     * @param request the request
     */
    public RequestParcel(String target, String title, Request request) {
        super("", target, target, title, request);
    }

}
