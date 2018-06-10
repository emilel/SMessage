package Main;

import Sender.Post;
import Shipments.Parcels.Letter;

public class Test {
    public static void main(String[] args) {
        Post post = new Post();

        System.out.println(post.sendParcel(new Letter("Emil", "192.168.1.241:3456",
                                                "192.168.1.241:3456", "Titel", "Meddelande")));
        System.out.println(post.downloadAll("192.168.1.241:3456"));
        System.out.println("filip was here");

    }
}
