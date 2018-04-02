package Receiver;

import Person.Person;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class containing the necessary settings for a Harbor to operate (port, people allowed to connect, what file
 * the settings are stored in, who the admins are and allowed commands).
 */
class Settings implements Serializable {
    private int port;
    private HashSet<Person> allowedPeople;
    private String settingsFile;
    private HashSet<Person> admins;
    private HashMap<Integer, String> commands;

    /**
     * Constructor for the class, saves the name of the settings file.
     * @param settingsFile the name of the settings file
     */
    Settings(String settingsFile) {
        this.settingsFile = settingsFile;
    }

    /**
     * Loads the settings from disk if possible.
     * @throws FileNotFoundException if no settings were found or if method was unable to read them
     */
    void loadSettings() throws FileNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream(settingsFile)) {
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Settings settings = (Settings) objectInputStream.readObject();
            this.port = settings.getPort();
            this.allowedPeople = settings.getAllowedPeople();
            this.admins = settings.getAdmins();
            this.commands = settings.getCommands();
        } catch (IOException | ClassNotFoundException e) {
            throw new FileNotFoundException();
        }
    }

    /**
     * Sets the settings the Harbor uses in memory.
     * @param port the port the Harbor should operate at
     * @param admins the admins of this Harbor
     * @param allowedPeople people who are allowed to connect
     * @param commands the commands that can be executed on this Harbor
     */
    void setSettings(int port, Set<Person> admins, Set<Person> allowedPeople, Map<Integer, String> commands) {
        this.port = port;
        this.admins = new HashSet<Person>(admins);
        this.allowedPeople = new HashSet<Person>(allowedPeople);
        this.commands = new HashMap<>(commands);
    }

    /**
     * Saves the settings to disk.
     */
    void saveSettings() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(settingsFile)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            System.out.println("settings saved to disk");
        } catch (IOException e) {
            System.out.println("unable to save settings");
        }
    }

    /**
     * Returns the people allowed to connect to the Harbor.
     * @return the people allowed to connect to the Harbor
     */
    public HashSet<Person> getAllowedPeople() {
        return allowedPeople;
    }

    /**
     * Returns the port.
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the admins.
     * @return the admins
     */
    public HashSet<Person> getAdmins() {
        return admins;
    }

    /**
     * Returns the commands.
     * @return the commands.
     */
    public HashMap<Integer, String> getCommands() {
        return commands;
    }
}
