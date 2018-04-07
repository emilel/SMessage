package Receiver;


import Shipments.Parcels.Parcel;
import Sender.Sender;
import Shipments.Parcels.Archive;
import Utils.Utils;
import Person.Person;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * The server class able to receive and distribute and handle Parcels from other machines. It can run independently
 * as a thread.
 *
 * A Harbor has:
 *      A port it operates on
 *      An external IP
 *      A local site IP
 *      A set of allowed Persons who are allowed to connect
 *      A set of admins allowed to execute commands and retrieve Parcels
 *      A Sender for distributing Parcels
 *      A map containing Integers that map to commands allowed to execute on the machine running it
 *      An Archive containing all Parcels received
 *      An instance of Settings containing its settings and methods to save them to disk
 *      A map that maps IP addresses to all Parcels received by that sender
 *
 * Upon creating a Harbor, Settings and the Archive are loaded from disk (if they exist). The method initialize()
 * retrieves the external and local site IP. Before calling the method run() that opens the Harbor and waits for
 * connections, the person who instantiated it should make sure that it is connected to a network (isConnected()) and
 * preferably if it is connected to the internet (hasInternetConnection()).
 */
public class Harbor extends Thread {
    public final static int DEFAULT_PORT = 6135;
    private ServerSocket serverSocket;
    private String externalIp;
    private String localSiteIp;
    private Settings settings;
    private boolean isRunning;
    private boolean hasInternetConnection;
    private boolean connected;
    private boolean isSetUp;
    private Sender sender;
    private Archive archive;
    private HashMap<String, ArrayList<Parcel>> newParcels;

    /**
     * Constructor for the Receiver. Loads settings and sets the field isSetUp to true, or sets it to false if unable
     * to load settings.
     *
     */
    public Harbor() {
        this.sender = new Sender();
        this.settings = new Settings(".settings");
        this.newParcels = new HashMap<>();
        this.connected = false;
        this.hasInternetConnection = false;
        try {
            settings.loadSettings();
            this.isSetUp = true;
        } catch (FileNotFoundException e) {
            this.isSetUp = false;
        }

        this.archive = new Archive(".archive");
        try {
            archive.loadParcels();
        } catch (FileNotFoundException e) {
            //no earlier parcels found in storage
        }
    }

    /**
     * Initializes the Harbor. Retrieves the local site IP and external IP, and sets the fields connected and
     * hasInternetConnection accordingly. This method needs to return 'true' before opening Harbor.
     * @return true if Harbor is connected to a network (of any type)
     */
    public boolean initialize() {
        this.localSiteIp = Utils.getLocalSiteIp();
        this.externalIp = Utils.getExternalIp();
        if (externalIp == null) {
            hasInternetConnection = false;
            if (localSiteIp == null) {
                connected = false;
            } else {
                connected = true;
            }
        } else {
            hasInternetConnection = true;
            connected = true;
        }
        return connected;
    }


    /**
     * Opens the Harbor, allows connections if they are either admins or in the set of allowed people,
     * and creates Handlers on separate threads that handle new incoming connections.
     */
    @Override
    public void run() {
        try {
            if(externalIp == null) {
                System.out.println("not connected, harbor not opened");
                return;
            }
            serverSocket = new ServerSocket(settings.getPort());
            isRunning = true;
            System.out.println(getInformation());
            while (true) {
                Socket dock = serverSocket.accept();
                System.out.println("\nnew request");
                if (settings.getAdmins().contains(new Person(null, dock.getInetAddress().toString().substring(1),
                        false)) || settings.getAllowedPeople().contains(new Person(null,
                        dock.getInetAddress().toString().substring(1), false))) {
                    System.out.println("request accepted");
                    (new Handler(dock, this)).start();
                }
                else {
                    dock.close();
                    System.out.println("request denied");
                }
            }
        } catch (IOException e) {
            System.out.println("server down");
        }
    }

    /**
     * Adds Persons who are allowed to connect to the Harbor.
     * @param people the people allowed to connect
     */
    public void addAllowedPeople(Collection<Person> people) {
        settings.getAllowedPeople().addAll(people);
        System.out.println("people updated");
    }

    /**
     * Returns if the Harbor has had an internet connection.
     * @return if the Harbor has had an internet connection
     */
    public boolean hasInternetConnection() {
        return hasInternetConnection;
    }

    /**
     * Saves the Harbor's current settings to disk.
     */
    public void saveSettings() {
        settings.saveSettings();
    }

    /**
     * Clears the admins, prints this out if there were any admins.
     */
    public void clearAdmins() {
        if(!settings.getAdmins().isEmpty()) System.out.println("admins cleared");
        settings.getAdmins().clear();
    }

    /**
     * Clears the allowed people, prints this out if there were any allowed people.
     */
    public void clearAllowedPeople() {
        if (!settings.getAllowedPeople().isEmpty()) System.out.println("allowed people cleared");
        settings.getAllowedPeople().clear();
    }

    /**
     * Sends a Parcel.
     * @param parcel the Parcel to be sent
     */
    public void sendParcel(Parcel parcel) {
        sender.sendParcel(parcel);
    }

    /**
     * Returns the external IP.
     * @return the external IP
     */
    public String getExternalIp() {
        return externalIp;
    }

    /**
     * Returns the local site IP.
     * @return the local site IP
     */
    public String getLocalSiteIp() {
        return localSiteIp;
    }

    /**
     * Adds a Parcel to newParcels not previously retrieved, and adds it to archive.
     * @param parcel
     */
    public void store(Parcel parcel) {
        if(newParcels.containsKey(parcel.getSource()))
            newParcels.get(parcel.getSource()).add(parcel);
        else {
            ArrayList<Parcel> parcelsFromSender = new ArrayList<>();
            parcelsFromSender.add(parcel);
            newParcels.put(parcel.getSource(), parcelsFromSender);
        }
        archive.addParcel(parcel);
    }

    /**
     * Clears the list of new Parcels, preferably because they have been retrieved.
     */
    public void clearNewParcels() {
        newParcels.clear();
        System.out.println("new parcels cleared");
    }

    /**
     * Saves all Parcels received to disk.
     */
    public void saveArchive() {
        archive.saveArchive();
    }

    /**
     * Returns a String containing this Harbor's local address, external address, admins and allowed people.
     * @return Harbor information
     */
    public String getInformation() {
        StringBuilder sb = new StringBuilder();
        if (isRunning) sb.append("harbor open\n");
        sb.append("local address: " + localSiteIp + ":" + serverSocket.getLocalPort() +
                    "\nexternal address: " + externalIp + ":" + serverSocket.getLocalPort() +
                "\nadmins: " + settings.getAdmins() + "\nallowed people: " + settings.getAllowedPeople());
        return sb.toString();
    }

    /**
     * Returns all commands allowed to execute on this machine.
     * @return all commands allowed to execute on this machine
     */
    public Map<Integer, String> getCommands() {
        return settings.getCommands();
    }

    /**
     * Returns if the Harbor is connected to a network.
     * @return if the Harbor is connected to a network
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Returns if the harbor is set up.
     * @return if the harbor is set up
     */
    public boolean isSetUp() {
        return isSetUp;
    }

    /**
     * Closes the Harbor for incoming connections.
     * @return true if it was successfully closed.
     */
    public boolean close() {
        try {
            serverSocket.close();
            return true;
        } catch (IOException e) {
            System.out.println("unable to close server");
            return false;
        }
    }

    /**
     * Clears the commands allowed to execute on the machine, prints this out if there were any allowed commands.
     */
    public void clearCommands() {
        if(!settings.getCommands().isEmpty()) System.out.println("commands cleared");
        settings.getCommands().clear();
    }

    /**
     * Adds a command to the list of allowed commands.
     * @param command the command
     * @param number a number representing a command, and what is being sent in a Command for execution
     */
    public void addCommand(String command, int number) {
        settings.getCommands().put(number, command);
        System.out.println("command added");
    }

    /**
     * Sets the port for the Harbor.
     * @param port the port for the Harbor
     */
    public void setPort(int port) {
        assert(port < 65535 && port > 1023);
        settings.setPort(port);
        String message = "port set to " + port;
        if(isRunning) message += ", server restart required";
        System.out.println(message);
    }

    /**
     * Returns all new Parcels not previously retrieved.
     * @return all new Parcels not previously retrieved
     */
    public HashMap<String, ArrayList<Parcel>> getNewParcels() {
        return newParcels;
    }

    /**
     * Removes an ip from the set of allowed ips.
     * @param person the Person which should not be allowed
     * @return if the ip was in the list of allowed ips and was successfully removed.
     */
    public boolean removeAllowedPerson(Person person) {
        return settings.getAllowedPeople().remove(person);
    }

    /**
     * Returns all parcels stored.
     * @return all parcels stored
     */
    public HashMap<String, ArrayList<Parcel>> getParcels() {
        return archive.getParcels();
    }

    /**
     * Returns the port this Harbor operates on.
     * @return the port this Harbor operates on
     */
    public int getPort() {
        return settings.getPort();
    }

    /**
     * Returns the Persons allowed to connect to this Harbor.
     * @return
     */
    public HashSet<Person> getAllowedPeople() {
        return settings.getAllowedPeople();
    }

    /**
     * Adds admins to this Harbor.
     * @param people the admins to add
     */
    public void addAdmins(Collection<Person> people) {
        settings.getAdmins().addAll(people);
        System.out.println("admins updated");
    }

    /**
     * Returns this Harbor's admins.
     * @return this Harbor's admins
     */
    public HashSet<Person> getAdmins() {
        return settings.getAdmins();
    }
}
