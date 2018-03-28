package Parcels;

/**
 * A class that adds the field "message" to a Parcel.
 */
public class Letter extends Parcel {
    private String content;
    private String type;

    /**
     * Constructor which creates a Letter.
     * @param sender the name of the sender
     * @param recipient the recipient (port:ip)
     * @param title the title of the Letter
     * @param message the message
     */
    public Letter(String sender, String recipient, String title, String message) {
        super(sender, recipient, title, message);
        type = "Letter";
    }

    /**
     * Returns this letter as a String.
     * @return this letter as a String
     */
    @Override
    public String getParcelType() {
        return type;
    }

    /**
     * Returns this Letter as a String.
     * @return this Letter as a String.
     */
    @Override
    public String toString() {
        return getParcelType().toLowerCase() + "\n" + super.toString();
    }
}
