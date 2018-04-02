package Shipments.Parcels;

import java.util.HashMap;

/**
 * A type of Parcel representing an empty Container. Indicator that something wrong has happened.
 */
public class EmptyContainer extends Container {

    /**
     * Constructor for the empty container.
     * @param reason the reason this container was constructed (preferably an error message or similar)
     */
    public EmptyContainer(String reason) {
        super(null, null, reason, new HashMap<>());
    }

    /**
     * Returns a String representing this EmptyContainer.
     * @return a String saying that this is an empty container and its title
     */
    @Override
    public String toString() {
        return "empty container: " + getTitle();
    }
}
