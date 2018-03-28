package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class containing useful methods.
 */
public class Utils {
    /**
     * Returns the ip of this machine.
     * @param ipWebsite which ip to fetch the ip from
     * @return the external ip
     */
    public static String fetchExternalIp(String ipWebsite) {
        try {
            URL ipCheckingWebsite = new URL(ipWebsite);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ipCheckingWebsite.openStream()));
            return bufferedReader.readLine().trim();
        } catch(MalformedURLException e) {
            System.out.println("could not parse website url to check ip");
            return null;
        } catch(IOException e) {
            System.out.println("could not read ip address from website");
            return null;
        }
    }
}
