package Shipments.Parcels;

import java.util.ArrayList;
import java.util.HashMap;

public class EmptyContainer extends Container {
    public EmptyContainer(String reason) {
        super(null, null, reason, new HashMap<String, ArrayList<Parcel>>());
    }

    @Override
    public String toString() {
        return "empty container: no content";
    }
}
