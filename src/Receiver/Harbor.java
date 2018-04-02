//TODO: return status to the controller
//TODO: initialize on open: allowed ips, max number of docks, the server ip

package Receiver;


import Shipments.Parcels.Parcel;
import Sender.Sender;
import Shipments.Parcels.Archive;
import Shipments.Response;
import Utils.Utils;
import Person.Person;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * The server class able to receive and distribute and handle packages from the internet.
 */
public class Harbor extends Thread {
    public final static int DEFAULT_PORT = 6135;

    private ServerSocket serverSocket;
    private int port;
    private String externalIp;
    private String localSiteIp;
    private HashSet<Person> allowedPeople;
    private HashSet<Person> admins;
    private Settings settings;
    private boolean isRunning;
    private boolean isSetUp;
    private Sender sender;
    private Archive archive;
    private Map<Integer, String> commands;
    private HashMap<String, ArrayList<Parcel>> newParcels;

    /**
     * Constructor for the Receiver. Loads settings and sets the field isSetUp to true, or sets it to false if unable to load settings.
     *
     */
    public Harbor() {
        this.localSiteIp = Utils.getLocalSiteIp();
        this.externalIp = Utils.getExternalIp();
        this.sender = new Sender();
        this.settings = new Settings(".settings");
        this.commands = new HashMap<>();
        this.admins = new HashSet<Person>();
        this.newParcels = new HashMap<>();
        try {
            settings.loadSettings();
            port = settings.getPort();
            allowedPeople = settings.getAllowedPeople();
            admins = settings.getAdmins();
            commands = settings.getCommands();
            isSetUp = true;
        } catch (FileNotFoundException e) {
            port = DEFAULT_PORT;
            allowedPeople = new HashSet<>();
            isSetUp = false;
        }

        this.archive = new Archive(".archive");
        try {
            archive.loadParcels();
        } catch (FileNotFoundException e) {
            //no earlier parcels found in storage
        }
    }

    public void addAllowedPeople(Collection<Person> people) {
        allowedPeople.addAll(people);
        System.out.println("people updated");
    }

    /**
     * Saves the settings to disk.
     */
    public void saveSettings() {
        settings.setSettings(port, admins, allowedPeople, commands);
        settings.saveSettings();
    }

    public void clearAdmins() {
        admins.clear();
        System.out.println("admins cleared");
    }

    public void clearAllowedPeople() {
        allowedPeople.clear();
        System.out.println("allowed people cleared");
    }

    /**
     * Returns if the server is running.
     * @return if the server is running.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Opens the Receiver, allows connections and creates Docks on separate threads that handle new incoming connections.
     */
    @Override
    public void run() {
        try {
            if(externalIp == null) {
                System.out.println("not connected, harbor not opened");
                return;
            }
            serverSocket = new ServerSocket(port);
            isRunning = true;
            System.out.println(getInformation());
            while (true) {
                Socket dock = serverSocket.accept();
                System.out.println("\nnew request");
                if (admins.contains(new Person(null, dock.getInetAddress().toString().substring(1), false))
                        || allowedPeople.contains(new Person(null, dock.getInetAddress().toString().substring(1), false))) {
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

    public void sendParcel(Parcel parcel) {
        sender.sendParcel(parcel);
    }

    public String getExternalIp() {
        return externalIp;
    }

    public String getLocalSiteIp() {
        return localSiteIp;
    }

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

    public void clearNewShipments() {
        newParcels.clear();
    }

    public void saveArchive() {
        archive.saveArchive();
    }

    public String getInformation() {
        StringBuilder sb = new StringBuilder();
        if(isRunning) sb.append("harbor open\n");
        sb.append("local address: " + localSiteIp + ":" + serverSocket.getLocalPort() + "\nexternal address: " + externalIp + ":" + serverSocket.getLocalPort() + "\nadmins: " + admins + "\nallowed people: " + allowedPeople);
        return sb.toString();
    }

    public Map<Integer, String> getCommands() {
        return commands;
    }

    /**
     * Returns if the harbor is set up and ready to open.
     * @return if the harbor is set up and ready to open
     */
    public boolean isSetUp() {
        return isSetUp;
    }

    /**
     * Closes the Receiver for incoming connections.
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

    public void clearCommands() {
        commands.clear();
        System.out.println("commands cleared");
    }

    public void addCommand(String command, int number) {
        commands.put(number, command);
        System.out.println("command added");
    }

    public void setPort(int port) {
        assert(port < 65535 && port > 1023);
        this.port = port;
        String message = "port set to " + port;
        if(isRunning) message += ", server restart required";
        System.out.println(message);
    }

    public HashMap<String, ArrayList<Parcel>> getNewParcels() {
        return newParcels;
    }

    public void sendResponse(Response response, Socket dock) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(dock.getOutputStream())) {
            objectOutputStream.writeObject(response);
        } catch (IOException e) {
            System.out.println("unable to send response");
        }
    }

    /**
     * Removes an ip from the set of allowed ips.
     * @param person the set which should not be allowed
     * @return if the ip was in the list of allowed ips and was successfully removed.
     */
    public boolean removeAllowedPerson(Person person) {
        return allowedPeople.remove(person);
    }

    /**
     * Returns all parcels stored.
     * @return all parcels stored
     */
    public HashMap<String, ArrayList<Parcel>> getParcels() {
        return archive.getParcels();
    }

    public int getPort() {
        return port;
    }

    public HashSet<Person> getAllowedPeople() {
        return allowedPeople;
    }

    public void addAdmins(Collection<Person> people) {
        admins.addAll(people);
        System.out.println("admins updated");
    }

    public HashSet<Person> getAdmins() {
        return admins;
    }
}
