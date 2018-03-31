//TODO: Command, File, Image
//TODO: Add sender IP on receive

package Parcels;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * An abstract superclass that represents all the packages sent and received by the program.
 * @param <E> the type of the content
 */
public abstract class Parcel<E> implements Serializable {
    public static final List<Parcel> parcelTypes = Arrays.asList(new Letter(), new Command());
    private String sender;
    private String server;
    private String recipient;
    private String title;
    private LocalDateTime timeSent;
    private String target;
    E content;
    String type;
    LocalDateTime timeReceived;
    String source;
    String distributor;

    Parcel(String sender, String server, String recipient, String title, E content, LocalDateTime timeSent) {
        this.sender = sender;
        this.server = server;
        this.target = server;
        this.recipient = recipient;
        this.title = title;
        this.content = content;
        this.timeSent = timeSent;
    }

    Parcel() { }

    Parcel(String sender, String server, String recipient, String title, E content) {
        this(sender, server, recipient, title, content, LocalDateTime.now());
    }

    public String getServer() {
        return server;
    }

    public String getTarget() {
        return target;
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
        return this.getClass().getSimpleName();
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
        return "type: " + getParcelType().toLowerCase() + "\nsender: " + sender + "\nrecipient: " + recipient + "\nserver: " + server + "\ntitle: " + title + "\ncontent (" + getContentType().toLowerCase() + "): " + content.toString();
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
    public Parcel receive(String source) {
        this.timeReceived = LocalDateTime.now();
        this.source = source;
        return this;
    }

    /**
     * Returns a DistributedParcel with the distributor added
     * @param distributor the server that distributed the package
     * @return a DistributedParcel with the distributor added
     */
    public Parcel distribute(String distributor) {
        this.distributor = distributor;
        this.target = recipient;
        return this;
    }

    /**
     * Returns the distributor of the package.
     * @return the distributor of the package
     */
    public String getDistributor() {
        return distributor;
    }

    /**
     * Returns the source of the package.
     * @return the source of the package
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns the time the Parcel was received.
     * @return the time the Parcel was received
     */
    public LocalDateTime getTimeReceived() {
        return timeReceived;
    }
}
