//TODO: return status to the controller
//TODO: initialize on open: allowed ips, max number of docks, the server ip

package Harbor;


import Utils.Utils;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


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
    private Properties properties;

    /**
     * Constructor for the Harbor.
     *
     */
    public Harbor() {
        this.localSiteIp = Utils.getLocalSiteIp();
        this.properties = new Properties();
        allowedIps = new HashSet<>();
        initialize();
    }

    /**
     * Opens the Harbor, allows connections and creates Docks on separate threads that handle new incoming connections.
     */
    public boolean open() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("server open\nlocal address: " + localSiteIp + ":" + port + "\nexternal address: " + externalIp + ":" + port + "\nallowed ips: " + allowedIps);
            while(true) {
                if(Thread.activeCount() < maxNumberOfConnections) {
                    Socket dock = serverSocket.accept();
                    if(allowedIps.contains(dock.getInetAddress().toString().substring(1)))
                        (new Dock(dock, externalIp, localSiteIp)).run();
                    else
                        dock.close();
                } else {
                    try {
                        Thread.sleep(5000);
                    } catch(InterruptedException e) {

                    }
                }
            }

        } catch(IOException e) {
            System.out.println("unable to start server");
            return false;
        }
    }

    public void addAllowedIp(String ip) {
        allowedIps.add(ip);
    }

    public void saveSettings() {
        properties = new Properties();
        properties.setProperty("port", String.valueOf(port));
        properties.setProperty("maxNumberOfConnections", String.valueOf(maxNumberOfConnections));
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("config.properties");
            properties.store(fileOutputStream, null);
            fileOutputStream = new FileOutputStream("allowedips.txt");
            //saveAllowedIps();
        } catch (IOException e) {
            System.out.println("unable to save settings");
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                System.out.println("unable to close fileoutputstream");
            } catch (NullPointerException e) {
                System.out.println("fileoutputstream was null");
            }
        }
    }

    private void saveAllowedIps() {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("allowedips.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(allowedIps);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (NullPointerException e) {
                System.out.println("fileoutputstream was null");
            } catch (IOException e) {
                System.out.println("unable to close file");
            }
        }
    }

    private void initialize() {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("config.properties");
            properties.load(fileInputStream);
            readProperties(properties);
        } catch (IOException e) {

            saveSettings();
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                System.out.println("unable to close fileinputstream");
            } catch (NullPointerException e) {
                //properties.config is now created
            }
        }
    }

    private void readProperties(Properties properties) {
        try {
            maxNumberOfConnections = Integer.parseInt(properties.getProperty("maxNumberOfConnections"));
            if(maxNumberOfConnections == 0) maxNumberOfConnections = 3;
        } catch (NumberFormatException e) {
            System.out.println("could not read maxnumberofconnections in config.properties, default value of 3 set");
            maxNumberOfConnections = 3;
        }
        try {
            port = Integer.parseInt(properties.getProperty("port"));
            if(port == 0) port = 6135;
        } catch (NumberFormatException e) {
            System.out.println("unable to read port in config.properties, default value of 6135 set");
            port = 6135;
        }
        try {
            externalIp = Utils.getExternalIp("http://bot.whatismyipaddress.com/");
        } catch (NumberFormatException e) {
            System.out.println("port in config.properties of wrong type");
        }
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("allowedips.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            allowedIps = (HashSet<String>) objectInputStream.readObject();
        } catch (IOException e) {
            saveAllowedIps();
        } catch (ClassNotFoundException e) {
            System.out.println("something wrong with allowedips.txt");
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                System.out.println("unable to close fileinputstream");
            } catch (NullPointerException e) {
                System.out.println("allowedips fileinputstream was null on close");
            }
        }

    }
}
