//TODO: return status to the controller
//TODO: initialize on open: allowed ips, max number of docks, the server ip

package Harbor;


import Utils.Utils;

import java.io.*;
import java.net.*;
import java.util.HashSet;


/**
 * The server class able to receive and (in the future) distribute and handle packages from the internet.
 */
public class Harbor extends Thread {
    private ServerSocket serverSocket;
    private int port;
    private int maxNumberOfConnections;
    private String externalIp;
    private String localSiteIp;
    private HashSet<String> allowedIps;
    private Settings settings;
    private boolean isRunning;
    private boolean setUp;

    /**
     * Constructor for the Harbor. Loads settings and sets the field setUp to true, or sets it to false if unable to load settings.
     *
     */
    public Harbor() {
        this.localSiteIp = Utils.getLocalSiteIp();
        this.externalIp = Utils.getExternalIp();
        allowedIps = new HashSet<>();
        settings = new Settings(".settings");
        try {
            settings.loadSettings();
            port = settings.getPort();
            maxNumberOfConnections = settings.getMaxNumberOfConnections();
            allowedIps = settings.getAllowedIps();
            setUp = true;
        } catch (FileNotFoundException e) {
            setUp = false;
        }
    }

    /**
     * Sets up the Harbor.
     * @param port the port to set up the server at
     * @param maxNumberOfConnections the maximum number of simultaneous connections
     * @param allowedIps all the ip addresses that are allowed to connect
     */
    public void setUp(int port, int maxNumberOfConnections, HashSet<String> allowedIps) {
        this.port = port;
        this.maxNumberOfConnections = maxNumberOfConnections;
        this.allowedIps = allowedIps;
        setUp = true;
    }

    /**
     * Saves the settings to disk.
     */
    public void saveSettings() {
        settings.setSettings(port, maxNumberOfConnections, allowedIps);
        settings.saveSettings();
    }

    /**
     * Returns if the server is running.
     * @return if the server is running.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Opens the Harbor, allows connections and creates Docks on separate threads that handle new incoming connections.
     */
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("server open\nlocal address: " + localSiteIp + ":" + port + "\nexternal address: " + externalIp + ":" + port + "\nmaximum number of connections: " + maxNumberOfConnections + "\nallowed ips: " + allowedIps);
            isRunning = true;
            while (true) {
                if (Thread.activeCount() < maxNumberOfConnections) {
                    Socket dock = serverSocket.accept();
                    if (allowedIps.contains(dock.getInetAddress().toString().substring(1)))
                        (new Dock(dock, externalIp, localSiteIp)).run();
                    else
                        dock.close();
                } else {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) { }
                }
            }
        } catch (IOException e) {
            System.out.println("unable to start server");
        }
    }

    /**
     * Returns if the harbor is set up and ready to open.
     * @return if the harbor is set up and ready to open
     */
    public boolean isSetUp() {
        return setUp;
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
     * Adds an allowed ip to the set of allowed ips.
     * @param ip
     */
    public void addAllowedIp(String ip) {
        allowedIps.add(ip);
    }

    /**
     * Removes an ip from the set of allowed ips.
     * @param ip the set which should not be allowed
     * @return if the ip was in the list of allowed ips and was successfully removed.
     */
    public boolean removeAllowedIp(String ip) {
        return allowedIps.remove(ip);
    }
}
