package Parcels;

import java.io.IOException;

public class Command extends Parcel {
    private static final int SHUTDOWN_SERVER = 0;
    private static final int RESTART_SERVER = 1;
    private static final int RESTART_RASPOTIFY = 2;

    public Command(String sender, String server, String recipient, String title, Integer command) {
        super(sender, server, recipient, title, command);
    }

    Command() { }

    public void execute() {
        try {
            switch ((Integer) content) {
                case SHUTDOWN_SERVER:
                    Runtime.getRuntime().exec("sudo shutdown -h now");
                    break;
                case RESTART_SERVER:
                    Runtime.getRuntime().exec("sudo reboot");
                    break;

                case RESTART_RASPOTIFY:
                    Runtime.getRuntime().exec("sudo systemctl restart raspotify");
                    break;
                default:
                    System.out.println("unknown command");
            }
        } catch (SecurityException e) {
            System.out.println("command not allowed");
        } catch (IOException e) {
            System.out.println("an io exception occured");
        }
    }
}
