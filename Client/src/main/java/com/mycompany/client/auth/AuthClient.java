package com.mycompany.client.auth;

import com.mycompany.client.auth.model.User;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Client for communicating with the authentication server using Socket
 * communication.
 * Handles requests for login, registration, logout, and token validation.
 */
public class AuthClient {

    // Server configuration
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;
    private static final int SOCKET_TIMEOUT = 5000; // 5 seconds

    // Request types
    private static final String REQUEST_REGISTER = "REGISTER";
    private static final String REQUEST_LOGIN = "LOGIN";
    private static final String REQUEST_LOGOUT = "LOGOUT";
    private static final String REQUEST_VALIDATE = "VALIDATE";

    /**
     * Registers a new user account.
     *
     * @param username The desired username
     * @param email    The user's email address
     * @param password The user's password
     * @return An AuthResponse with User and token if successful
     * @throws AuthException if registration fails
     */
    public static AuthResponse register(String username, String email, String password) throws AuthException {
        try {
            // Create request JSON
            JSONObject request = new JSONObject();
            request.put("type", REQUEST_REGISTER);
            request.put("username", username);
            request.put("email", email);
            request.put("password", password);

            // Send request and get response
            String responseStr = sendRequest(request.toString());
            JSONObject response = new JSONObject(responseStr);

            if (response.getBoolean("success")) {
                JSONObject userData = response.getJSONObject("user");
                User user = new User(
                        userData.getInt("id"),
                        userData.getString("username"),
                        userData.getString("email"));
                String token = response.getString("token");

                System.out.println("Registration successful for user: " + username);
                return new AuthResponse(user, token);
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

    /**
     * Logs in a user with username and password.
     *
     * @param username The username
     * @param password The password
     * @return An AuthResponse with User and token if successful
     * @throws AuthException if login fails
     */
    public static AuthResponse login(String username, String password) throws AuthException {
        try {
            // Create request JSON
            JSONObject request = new JSONObject();
            request.put("type", REQUEST_LOGIN);
            request.put("username", username);
            request.put("password", password);

            // Send request and get response
            String responseStr = sendRequest(request.toString());
            JSONObject response = new JSONObject(responseStr);

            if (response.getBoolean("success")) {
                JSONObject userData = response.getJSONObject("user");
                User user = new User(
                        userData.getInt("id"),
                        userData.getString("username"),
                        userData.getString("email"));
                String token = response.getString("token");

                System.out.println("Login successful for user: " + username);
                return new AuthResponse(user, token);
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

    /**
     * Logs out a user by invalidating their token.
     *
     * @param token The authentication token
     * @throws AuthException if logout fails
     */
    public static void logout(String token) throws AuthException {
        try {
            // Create request JSON
            JSONObject request = new JSONObject();
            request.put("type", REQUEST_LOGOUT);
            request.put("token", token);

            // Send request and get response
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

    /**
     * Validates an authentication token and retrieves the associated user.
     *
     * @param token The authentication token to validate
     * @return The User associated with the token if valid, null otherwise
     */
    public static User validateToken(String token) {
        try {
            // Create request JSON
            JSONObject request = new JSONObject();
            request.put("type", REQUEST_VALIDATE);
            request.put("token", token);

            // Send request and get response
            String responseStr = sendRequest(request.toString());
            JSONObject response = new JSONObject(responseStr);

            if (response.getBoolean("valid")) {
                JSONObject userData = response.getJSONObject("user");
                User user = new User(
                        userData.getInt("id"),
                        userData.getString("username"),
                        userData.getString("email"));

                System.out.println("Token validated for user: " + user.getUsername());
                return user;
            } else {
                System.out.println("Token validation failed");
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error validating token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Sends a request to the server and receives the response.
     * Uses Socket with DataInputStream and DataOutputStream.
     *
     * @param requestJson The JSON request string
     * @return The JSON response string
     * @throws IOException if network error occurs
     */
    private static String sendRequest(String requestJson) throws IOException {
        Socket socket = null;
        DataOutputStream out = null;
        DataInputStream in = null;

        try {
            // Connect to server
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            socket.setSoTimeout(SOCKET_TIMEOUT);

            // Create streams
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            // Send request
            out.writeUTF(requestJson);
            out.flush();

            System.out.println("Request sent to server: " + requestJson);

            // Receive response
            String response = in.readUTF();
            System.out.println("Response received from server: " + response);

            return response;

        } finally {
            // Close resources
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

    /**
     * Response wrapper containing user and token information.
     */
    public static class AuthResponse {
        private final User user;
        private final String token;

        public AuthResponse(User user, String token) {
            this.user = user;
            this.token = token;
        }

        public User getUser() {
            return user;
        }

        public String getToken() {
            return token;
        }
    }

    /**
     * Custom exception for authentication errors.
     */
    public static class AuthException extends Exception {
        public AuthException(String message) {
            super(message);
        }
    }
}
