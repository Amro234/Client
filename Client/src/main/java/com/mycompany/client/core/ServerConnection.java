package com.mycompany.client.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerConnection {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;
    private static final int SOCKET_TIMEOUT = 5000;

    public static String sendRequest(String requestJson) throws IOException {
        Socket socket = null;
        DataOutputStream out = null;
        DataInputStream in = null;

        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            socket.setSoTimeout(SOCKET_TIMEOUT);

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            out.writeUTF(requestJson);
            out.flush();

            System.out.println("Request sent to server: " + requestJson);

            String response = in.readUTF();
            System.out.println("Response received from server: " + response);

            return response;

        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
