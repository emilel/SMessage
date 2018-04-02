//TODO: Command, File, Image
//TODO: Add sender IP on receive

package Shipments.Parcels;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * An abstract superclass that represents all the Parcels sent and received between programs.
 * @param <E> the type of the content
 */
public abstract class Parcel<E> implements Serializable {
    public static final List<Parcel> parcelTypes = Arrays.asList(new Letter(), new Command());
    private String sender;
    private String server;
    private String recipient;
    private String title;
    private LocalDateTime timeSent;
    private E content;
    private LocalDateTime timeReceived;
    private String source;
    private String distributor;
    private String target;

    //constructor
    private Parcel(String sender, String server, String recipient, String title, E content, LocalDateTime timeSent) {
        this.target = server;
        this.sender = sender;
        this.server = server;
        this.recipient = recipient;
        this.title = title;
        this.content = content;
        this.timeSent = timeSent;
    }

    //useful so a collection of different types of Parcels without content easily can be created
    Parcel() { }

    /**
     * Constructs a Parcel.
     * @param sender the sender of this Parcel (name)
     * @param server the server this Parcel passes through (needs to be in this format - ip:port)
     * @param recipient the recipient of this Parcel (a Harbor or a Person, needs to be in this format - ip:port)
     * @param title the title of the Parcel
     * @param content the content that can be of any type
     */
    public Parcel(String sender, String server, String recipient, String title, E content) {
        this(sender, server, recipient, title, content, LocalDateTime.now());
    }

    /**
     * Returns the server the Parcel was first sent to
     * @return the server the Parcel was first sent to
     */
    public String getServer() {
        return server;
    }

    /**
     * Returns where the Parcel is going to next.
     * @return when the Parcel is created, the server is returned. When a Parcel is received and is going to another
     * machine, it returns the recipient.
     */
    public String getTarget() {
        return target;
    }

    /**
     * Returns the class of the content.
     * @return the class of the content
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
     * Returns the title of this Parcel.
     * @return the title of this Parcel
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns this parcel as a String (type, sender, recipient, title, content and content type).
     * @return this parcel as a String
     */
    public String toString() {
        return "type: " + getParcelType().toLowerCase() + "\nsender: " + sender + "\nrecipient: " + recipient + "\ntitle: " + title + "\ncontent (" + getContentType().toLowerCase() + "): " + content.toString();
    }

    /**
     * Returns the name of the sender.
     * @return the name of the sender
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
     * Adds the information of who distributed the Parcel, and changes the target to the recipient.
     * @param distributor the server that distributed this Parcel
     * @return a reference to this Parcel
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
     * Returns the source of the package (IP address of the sender).
     * @return the source of the package (IP address of the sender)
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
