package client;

import java.nio.file.Paths;

public class Client {
    public static void main(String[] args) throws Exception {
        ClientThread clientThread = new ClientThread(Paths.get("someFiles"));
        clientThread.start();
        ClientHandler handler = clientThread.getHandler();
        handler.tryAuth("another_client", "password1");
        ClientSender clientSender = handler.getClientSender();
        /*clientSender.create_dir("newDir1");
        clientSender.post("hegel.pdf");
        clientSender.get("post.txt");
        */
        // clientSender.move("6.txt", "renamed.txt");
         // clientSender.delete("renamed.txt");
         clientSender.getfilelist();
    }

}
