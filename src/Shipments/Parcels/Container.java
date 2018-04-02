package Shipments.Parcels;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A type of Parcel containing multiple Parcels.
 */
public class Container extends Parcel {

    /**
     * Constructs a Container.
     * @param sender the sender of this Container (probably the Harbor)
     * @param recipient the client who requested the container
     * @param title the title, preferably describing the content of the Container
     * @param parcels the content of this Container, the parcels
     */
    public Container(String sender, String recipient, String title, HashMap<String, ArrayList<Parcel>> parcels) {
        super(sender, sender, recipient, title, parcels);
    }

    /**
     * Returns a String representing the container.
     * @return Container, and on separate lines every Parcel with its title and sender
     */
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