package y_netty;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 8189);
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

        writeString("echoString", dataOutputStream);
        System.out.println(readString(dataInputStream));

        outputStream.close();
        socket.close();
    }

    private static void writeString(String message, DataOutputStream dataOutputStream) throws Exception {
        char[] chars = message.toCharArray();
        for(int i = 0; i < chars.length; i++) {
            dataOutputStream.writeChar(chars[i]);
        }
        dataOutputStream.writeChar('\n');
        dataOutputStream.flush();
    }

    private static String readString(DataInputStream dataInputStream) throws Exception {
        StringBuilder sb = new StringBuilder();

        while(true) {
            char currentChar = dataInputStream.readChar();
            if (currentChar == '\n') break;
            sb.append(currentChar);
        }

        return sb.toString();
    }
}
