package Shipments.Parcels;

import Shipments.Parcels.Parcel;

import java.util.ArrayList;
import java.util.HashMap;

public class Container extends Parcel {

    public Container(String sender, String recipient, String title, HashMap<String, ArrayList<Parcel>> parcelSet) {
        super(sender, sender, recipient, title, parcelSet);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Container\n");
        for(ArrayList<Parcel> ap : ((HashMap<String, ArrayList<Parcel>>) getContent()).values()) {
            for(Parcel p : ap) {
                sb.append(p.getTitle() + " - " + p.getSender() + "\n");
            }
        }
        return sb.toString();
    }
}