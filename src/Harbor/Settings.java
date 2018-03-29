package Harbor;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Settings implements Serializable {
    private int port;
    private int maxNumberOfConnections;
    private HashSet<String> allowedIps;
    private String settingsFile;

    public Settings(String settingsFile) {
        this.settingsFile = settingsFile;
    }

    public void loadSettings() throws FileNotFoundException {
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

    public void setSettings(int port, int maxNumberOfConnections, Set<String> allowedIps) {
        this.port = port;
        this.maxNumberOfConnections = maxNumberOfConnections;
        this.allowedIps = new HashSet<>(allowedIps);
    }

    public boolean saveSettings() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(settingsFile)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
        } catch (IOException e) {
            System.out.println(e.toString());
            return false;
        }
        return true;
    }

    public HashSet<String> getAllowedIps() {
        return allowedIps;
    }

    public int getMaxNumberOfConnections() {
        return maxNumberOfConnections;
    }

    public int getPort() {
        return port;
    }
}
