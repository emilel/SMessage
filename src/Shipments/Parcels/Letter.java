package Shipments.Parcels;

/**
 * A type of Parcel containing a String (a message).
 */
public class Letter extends Parcel {

    /**
     * Constructor which creates a Letter.
     * @param sender the name of the sender
     * @param recipient the recipient
     * @param title the title of the Letter
     * @param message the message
     */
    public Letter(String sender, String server, String recipient, String title, String message) {
        super(sender, server, recipient, title, message);
    }

    //useful so a collection of different types of Parcels without content easily can be created
    Letter() { }
}
