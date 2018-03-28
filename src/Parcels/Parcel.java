//TODO: Command, File, Image
//TODO: Add sender IP on receive

package Parcels;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * An abstract superclass that represents all the packages sent and received by the program.
 * @param <E> the type of the content
 */
public abstract class Parcel<E> implements Serializable {
    private String sender;
    private String recipient;
    private String title;
    private E content;
    private LocalDateTime timeSent;
    private String type;

    private Parcel(String sender, String recipient, String title, E content, LocalDateTime timeSent) {
        this.sender = sender;
        this.recipient = recipient;
        this.title = title;
        this.content = content;
        this.timeSent = timeSent;
    }

    Parcel(String sender, String recipient, String title, E content) {
        this(sender, recipient, title, content, LocalDateTime.now());
    }

    /**
     * Returns the class of the content.
     * @return the class of the content.
     */
    public String getContentType() {
        return content.getClass().getSimpleName();
    }

    /**
     * Returns the type of the Parcel.
     * @return the type of the Parcel.
     */
    public String getParcelType() {
        return this.getClass().toString();
    }

    /**
     * Returns the content of this Parcel.
     * @return the content of this Parcel
     */
    public E getContent() {
        return content;
    }

    /**
     * Returns this parcel as a String.
     * @return this parcel as a String.
     */
    public String toString() {
        return "sender: " + sender + "\nrecipient: " + recipient + "\n\ncontent (" + getContentType().toLowerCase() + "):\n" + content.toString();
    }

    /**
     * Returns the name (which the sender wrote upon creation).
     * @return the name (which the sender wrote upon creation)
     */
    public String getSender() {
        return sender;
    }

    /**
     * Returns the recipient on the form ip:port.
     * @return the recipient on the form ip:port
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Returns the LocalDateTime when the package was instantiated.
     * @return the LocalDateTime when the package was instantiated
     */
    public LocalDateTime getTimeSent() {
        return timeSent;
    }

    /**
     * Returns a ReceivedParcel which adds the time the Parcel was received by the server.
     * @return a ReceivedParcel which adds the time the Parcel was received by the server
     */
    public ReceivedParcel receive() {
        return new ReceivedParcel(this);
    }

    /**
     * The class the server transforms an incoming package to upon receiving to include the time it was received.
     */
    public class ReceivedParcel extends Parcel {
        private LocalDateTime timeReceived;

        private ReceivedParcel(Parcel parcel) {
            super(sender, recipient, title, content, timeSent);
            this.timeReceived = LocalDateTime.now();
        }

        /**
         * Returns the time the Parcel was received.
         * @return the time the Parcel was received
         */
        public LocalDateTime getTimeReceived() {
            return timeReceived;
        }
    }
}
