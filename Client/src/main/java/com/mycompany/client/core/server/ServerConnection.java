package com.mycompany.client.core.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerConnection {

    private static String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;
    private static final int SOCKET_TIMEOUT = 5000;

    private static Socket socket = null;
    private static DataOutputStream out = null;
    private static DataInputStream in = null;
    private static Thread listenerThread = null;
    private static boolean isConnected = false;
    private static ServerMessageListener messageListener = null;

    public static void setServerHost(String host) {
        SERVER_HOST = host;
    }

    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }

    public static void setMessageListener(ServerMessageListener listener) {
        messageListener = listener;
    }

    public static boolean testConnection() {
        Socket testSocket = null;
        try {
            testSocket = new Socket(SERVER_HOST, SERVER_PORT);
            testSocket.setSoTimeout(SOCKET_TIMEOUT);
            return true;
        } catch (IOException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        } finally {
            if (testSocket != null) {
                try {
                    testSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing test socket: " + e.getMessage());
                }
            }
        }
    }

    private static java.util.concurrent.ScheduledExecutorService heartbeatExecutor;

    public static synchronized boolean connect() {
        if (isConnected()) {
            System.out.println("Already connected to server");
            return true;
        }

        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            socket.setSoTimeout(0); // No timeout for persistent connection
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            isConnected = true;

            System.out.println("Connected to server: " + SERVER_HOST + ":" + SERVER_PORT);

            startHeartbeat();

            return true;
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            cleanup();
            return false;
        }
    }

    public static synchronized void startMessageListener() {
        if (!isConnected()) {
            System.err.println("Cannot start message listener: not connected to server");
            return;
        }

        if (listenerThread != null && listenerThread.isAlive()) {
            System.out.println("Message listener already running");
            return;
        }

        startListenerThread();
        System.out.println("Message listener started");
    }

    public static synchronized void disconnect() {
        System.out.println("Disconnecting from server...");
        isConnected = false;

        if (listenerThread != null) {
            listenerThread.interrupt();
        }

        stopHeartbeat();

        cleanup();
        System.out.println("Disconnected from server");
    }

    private static void startHeartbeat() {
        stopHeartbeat();
        heartbeatExecutor = java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "HeartbeatThread");
            t.setDaemon(true);
            return t;
        });

        heartbeatExecutor.scheduleAtFixedRate(() -> {
            if (isConnected) {
                try {
                    // Send PING
                    // Using direct write to avoid polluting logs with excessive PING messages
                    synchronized (ServerConnection.class) {
                        if (out != null) {
                            org.json.JSONObject ping = new org.json.JSONObject();
                            ping.put("type", "PING");
                            out.writeUTF(ping.toString());
                            out.flush();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Heartbeat failed: " + e.getMessage());
                    // This is the CRITICAL point where we detect connection loss actively
                    if (messageListener != null) {
                        messageListener.onConnectionLost();
                    }
                    disconnect();
                }
            }
        }, 2, 2, java.util.concurrent.TimeUnit.SECONDS);
    }

    private static void stopHeartbeat() {
        if (heartbeatExecutor != null && !heartbeatExecutor.isShutdown()) {
            heartbeatExecutor.shutdownNow();
        }
        heartbeatExecutor = null;
    }

    private static void startListenerThread() {
        listenerThread = new Thread(() -> {
            while (isConnected && !Thread.currentThread().isInterrupted()) {
                try {
                    if (in != null && in.available() > 0) {
                        String message = in.readUTF();

                        // Ignore PONG responses
                        if (message.contains("\"type\":\"PONG\"") || message.contains("\"type\": \"PONG\"")) {
                            continue;
                        }

                        System.out.println("Received from server: " + message);

                        if (messageListener != null) {
                            messageListener.onMessageReceived(message);
                        }
                    }
                    Thread.sleep(10); // Small delay to prevent CPU spinning
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (IOException e) {
                    if (isConnected) {
                        System.err.println("Connection lost: " + e.getMessage());
                        // Notify listener FIRST before cleaning up
                        if (messageListener != null) {
                            messageListener.onConnectionLost();
                        }
                        disconnect();
                    }
                    break;
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.setName("ServerListener");
        listenerThread.start();
    }

    public static synchronized String sendRequest(String requestJson) throws IOException {
        if (!isConnected()) {
            throw new IOException("Not connected to server");
        }

        try {
            out.writeUTF(requestJson);
            out.flush();
            System.out.println("Request sent to server: " + requestJson);

            // Wait for immediate response and ignore PONGs (Heartbeat responses)
            String response;
            do {
                response = in.readUTF();
            } while (response.contains("\"type\":\"PONG\"") || response.contains("\"type\": \"PONG\""));

            System.out.println("Response received from server: " + response);
            return response;

        } catch (IOException e) {
            System.err.println("Error sending request: " + e.getMessage());
            disconnect();
            throw e;
        }
    }

    public static synchronized void sendMessage(String message) throws IOException {
        if (!isConnected()) {
            throw new IOException("Not connected to server");
        }

        try {
            out.writeUTF(message);
            out.flush();
            System.out.println("Message sent to server: " + message);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            disconnect();
            throw e;
        }
    }

    private static void cleanup() {
        try {
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        } finally {
            out = null;
            in = null;
            socket = null;
            isConnected = false;
        }
    }

    public interface ServerMessageListener {
        void onMessageReceived(String message);

        void onConnectionLost();
    }
}
