//TODO: return status to the controller

package Harbor;


import Utils.Utils;

import java.io.IOException;
import java.net.*;


/**
 * The server class able to receive and (in the future) distribute and handle packages from the internet.
 */
public class Harbor {
    ServerSocket serverSocket;
    private int port;
    private int maxNumberOfDocks;
    private String externalIp;
    private String localSiteIp;

    /**
     * Constructor for the Harbor.
     * @param port which port for the Harbor to use
     * @param maxNumberOfDocks the maximal number of simultaneous connections
     * @param ipWebsite which website to use for finding out external ip
     */
    public Harbor(int port, int maxNumberOfDocks, String ipWebsite) {
        this.port = port;
        this.maxNumberOfDocks = maxNumberOfDocks;
        fetchIps(ipWebsite);
    }

    /**
     * Alternative constructor for the Harbor without giving the argument of which website to fetch external ip from.
     * @param port which port for the Harbor to use
     * @param maxNumberOfDocks the maximal number of simultaneous connections
     */
    public Harbor(int port, int maxNumberOfDocks) {
        this(port, maxNumberOfDocks, "http://bot.whatismyipaddress.com/");
    }

    /**
     * Opens the Harbor, allows connections and creates Docks on separate threads that handle new incoming connections.
     */
    public void open() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("server open\nlocal address: " + localSiteIp + ":" + port + "\nexternal address: " + externalIp + ":" + port + "\n");
            while(true) {
                if(Thread.activeCount() < maxNumberOfDocks) {
                    Socket dock = serverSocket.accept();
                    (new Dock(dock, externalIp, localSiteIp)).run();
                } else {
                    try {
                        Thread.sleep(5000);
                    } catch(InterruptedException e) {

                    }
                }
            }

        } catch(IOException e) {
            System.out.println("unable to start server");
        }
    }

    private void fetchIps(String ipWebsite) {
        localSiteIp = Utils.getLocalSiteIp();
        externalIp = Utils.getExternalIp(ipWebsite);
    }
}
