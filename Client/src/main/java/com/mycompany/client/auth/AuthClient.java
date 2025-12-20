package com.mycompany.client.auth;

import com.mycompany.client.auth.model.User;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AuthClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;
    private static final int SOCKET_TIMEOUT = 5000;

    private static final String REQUEST_REGISTER = "REGISTER";
    private static final String REQUEST_LOGIN = "LOGIN";
    private static final String REQUEST_LOGOUT = "LOGOUT";

    public static AuthResponse register(String username, String email, String password) throws AuthException {
        try {
            JSONObject request = new JSONObject();
            request.put("type", REQUEST_REGISTER);
            request.put("username", username);
            request.put("email", email);
            request.put("password", password);

            String responseStr = sendRequest(request.toString());
            JSONObject response = new JSONObject(responseStr);

            if (response.getBoolean("success")) {
                JSONObject userData = response.getJSONObject("user");
                User user = new User(
                        userData.getInt("id"),
                        userData.getString("username"),
                        userData.getString("email"),
                        userData.optInt("score", 0));

                System.out.println("Registration successful for user: " + username);
                return new AuthResponse(user);
            } else {
                String errorMessage = response.optString("message", "Registration failed");
                throw new AuthException(errorMessage);
            }

        } catch (IOException e) {
            System.err.println("Network error during registration: " + e.getMessage());
            throw new AuthException("Unable to connect to server. Please check your connection.");
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            throw new AuthException("Registration failed: " + e.getMessage());
        }
    }

    public static AuthResponse login(String username, String password) throws AuthException {
        try {
            JSONObject request = new JSONObject();
            request.put("type", REQUEST_LOGIN);
            request.put("username", username);
            request.put("password", password);

            String responseStr = sendRequest(request.toString());
            JSONObject response = new JSONObject(responseStr);

            if (response.getBoolean("success")) {
                JSONObject userData = response.getJSONObject("user");
                User user = new User(
                        userData.getInt("id"),
                        userData.getString("username"),
                        userData.getString("email"),
                        userData.optInt("score", 0));

                System.out.println("Login successful for user: " + username);
                return new AuthResponse(user);
            } else {
                String errorMessage = response.optString("message", "Invalid credentials");
                throw new AuthException(errorMessage);
            }

        } catch (IOException e) {
            System.err.println("Network error during login: " + e.getMessage());
            throw new AuthException("Unable to connect to server. Please check your connection.");
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            throw new AuthException("Login failed: " + e.getMessage());
        }
    }

    public static void logout(String token) throws AuthException {
        try {
            JSONObject request = new JSONObject();
            request.put("type", REQUEST_LOGOUT);

            String responseStr = sendRequest(request.toString());
            JSONObject response = new JSONObject(responseStr);

            if (response.getBoolean("success")) {
                System.out.println("Logout successful");
            } else {
                String errorMessage = response.optString("message", "Logout failed");
                throw new AuthException(errorMessage);
            }

        } catch (IOException e) {
            System.err.println("Network error during logout: " + e.getMessage());
            throw new AuthException("Unable to connect to server. Please check your connection.");
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            throw new AuthException("Logout failed: " + e.getMessage());
        }
    }

    

    private static String sendRequest(String requestJson) throws IOException {
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

    public static class AuthResponse {
        private final User user;

        public AuthResponse(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

       
    }

    public static class AuthException extends Exception {
        public AuthException(String message) {
            super(message);
        }
    }
}
