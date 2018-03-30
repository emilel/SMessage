package Harbor;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A class containing the necessary settings for a Harbor to operate.
 */
class Settings implements Serializable {
    private int port;
    private int maxNumberOfConnections;
    private HashSet<String> allowedIps;
    private String settingsFile;

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
            this.allowedIps = settings.getAllowedIps();
            this.maxNumberOfConnections = settings.getMaxNumberOfConnections();
        } catch (IOException | ClassNotFoundException e) {
            throw new FileNotFoundException();
        }
    }

    /**
     * Saves the settings to disk.
     * @param port port for the Harbor to operate at
     * @param maxNumberOfConnections the maximum number of simultaneous connections
     * @param allowedIps the set of ips which are allowed to connect
     */
    void setSettings(int port, int maxNumberOfConnections, Set<String> allowedIps) {
        this.port = port;
        this.maxNumberOfConnections = maxNumberOfConnections;
        this.allowedIps = new HashSet<>(allowedIps);
    }

    /**
     * Saves the settings to disk.
     * @return if the settings were successfully saved
     */
    boolean saveSettings() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(settingsFile)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
        } catch (IOException e) {
            System.out.println(e.toString());
            return false;
        }
        return true;
    }

    /**
     * Returns a set of all the allowed ips.
     * @return a set of all the allowed ips.
     */
    HashSet<String> getAllowedIps() {
        return allowedIps;
    }

    /**
     * Returns the maximum number of simultaneous connections.
     * @return the maximum number of simultaneous connections
     */
    int getMaxNumberOfConnections() {
        return maxNumberOfConnections;
    }

    /**
     * Returns the port.
     * @return the port
     */
    int getPort() {
        return port;
    }
}
