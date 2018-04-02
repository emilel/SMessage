package Shipments.Parcels;

import java.util.HashMap;

/**
 * A type of Parcel. Its content is an Integer representing a command (chosen by the user on the Harbor). Not possible
 * to forward, has to be sent directly to the Harbor.
 */
public class Command extends Parcel {
    public static HashMap<Integer, String> commands;

    /**
     * Constructs a Command.
     * @param sender the person sending the Command (name)
     * @param server the Harbor to execute the command on
     * @param command the Integer representing the command the sender wants to execute
     */
    public Command(String sender, String server, Integer command) {
        super(sender, server, server, "command", command);
    }

    //useful so a collection of different types of Parcels without content easily can be created
    Command() { }
}
