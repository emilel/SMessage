package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;

/**
 * A class containing useful methods.
 */
public class Utils {
    /**
     * Returns the external ip of this machine.
     * @return the external ip
     */
    public static String getExternalIp() {
        try {
            URL ipCheckingWebsite = new URL("http://bot.whatismyipaddress.com/");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ipCheckingWebsite.openStream()));
            return bufferedReader.readLine().trim();
        } catch(MalformedURLException e) {
            System.out.println("could not parse website url to check ip");
            return null;
        } catch(IOException e) {
            return null;
        }
    }

    /**
     * Returns the local site ip of this machine.
     * @return the local site ip of this machine.
     */
    public static String getLocalSiteIp() {
        try {
            Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) enumeration.nextElement();
                enumeration = n.getInetAddresses();
                while (enumeration.hasMoreElements()) {
                    InetAddress inetAddress = ((InetAddress) enumeration.nextElement());
                    if(inetAddress.isSiteLocalAddress()) return inetAddress.toString().substring(1);
                }
            }
        } catch(SocketException e) {
            System.out.println("unable to get local site ip");
        }
        return null;
    }
}
