package Parcels;

import java.io.*;
import java.util.HashMap;

public class Command extends Parcel {
    public static HashMap<Integer, String> commands;

    public Command(String sender, String server, String recipient, String title, Integer command) {
        super(sender, server, recipient, title, command);
    }

    Command() { }
}
