package tests;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class PrimitiveClientHandler {
    private DataInputStream in;
    private DataOutputStream out;

    public PrimitiveClientHandler(Socket socket) throws Exception {
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }


    public void sendString(String message) throws Exception {

        for(int i = 0; i < message.length(); i++) {
            out.writeChar(message.charAt(i));
        }
        out.writeChar('\n');
        out.flush();

    }


    public String getString() throws Exception {
        StringBuilder sb = new StringBuilder();
        while(true) {
            char currentChar = in.readChar();
            if (currentChar == '\n') break;
            sb.append(currentChar);
        }
        return sb.toString();
    }

}
