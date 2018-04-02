package Shipments;

import Shipments.Parcels.Parcel;

public class RequestParcel extends Parcel {

    public RequestParcel(String target, String title, Request request) {
        super("admin", target, target, title, request);
    }

}
