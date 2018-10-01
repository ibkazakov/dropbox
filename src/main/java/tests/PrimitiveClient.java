package tests;

import java.net.Socket;

public class PrimitiveClient {
    private PrimitiveClientHandler primitiveClientHandler;

    private Thread printThread = new Thread() {
        @Override
        public void run() {
            while (true) {
                try {
                    String acceptedString = primitiveClientHandler.getString();
                    System.out.println(acceptedString);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    };

    public PrimitiveClient(Socket socket) throws Exception {
        primitiveClientHandler = new PrimitiveClientHandler(socket);
        printThread.start();
    }

    public void sendString(String message) throws Exception {
        primitiveClientHandler.sendString(message);
    }


}
