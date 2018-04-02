package Shipments.Parcels;

/**
 * A class that adds the field "message" to a Parcel.
 */
public class Letter extends Parcel {

    /**
     * Constructor which creates a Letter.
     * @param sender the name of the sender
     * @param recipient the recipient (port:ip)
     * @param title the title of the Letter
     * @param message the message
     */
    public Letter(String sender, String server, String recipient, String title, String message) {
        super(sender, server, recipient, title, message);
    }

    Letter() { }
}
