package com.mycompany.client.auth;

import com.mycompany.client.auth.model.User;

/**
 * Singleton class that manages the current user session.
 * Maintains the logged-in user state and authentication token in memory.
 */
public class UserSession {

    private static UserSession instance;

    private User currentUser;
    private String token;
    private boolean isLoggedIn;

    /**
     * Private constructor to prevent direct instantiation.
     */
    private UserSession() {
        this.isLoggedIn = false;
    }

    /**
     * Gets the singleton instance of UserSession.
     *
     * @return The UserSession instance
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Logs in a user by setting the current user and token.
     * Automatically saves the token to local storage.
     *
     * @param user  The user to log in
     * @param token The authentication token
     */
    public void login(User user, String token) {
        if (user == null || token == null || token.trim().isEmpty()) {
            System.err.println("Cannot login with null user or token");
            return;
        }

        this.currentUser = user;
        this.token = token;
        this.isLoggedIn = true;

        // Save token to local storage
        boolean saved = TokenManager.saveToken(token);
        if (saved) {
            System.out.println("User logged in: " + user.getUsername());
        } else {
            System.err.println("Warning: Token not saved to local storage");
        }
    }

    /**
     * Logs out the current user by clearing the session.
     * Automatically deletes the token from local storage.
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("User logged out: " + currentUser.getUsername());
        }

        this.currentUser = null;
        this.token = null;
        this.isLoggedIn = false;

        // Delete token from local storage
        TokenManager.deleteToken();
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return isLoggedIn && currentUser != null && token != null;
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return The current user, or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Gets the current authentication token.
     *
     * @return The current token, or null if not logged in
     */
    public String getToken() {
        return token;
    }

    /**
     * Gets the username of the current user.
     *
     * @return The username, or "Guest" if not logged in
     */
    public String getCurrentUsername() {
        return (currentUser != null) ? currentUser.getUsername() : "Guest";
    }

    /**
     * Clears the session without deleting the local token file.
     * Used for temporary session clearing.
     */
    public void clearSession() {
        this.currentUser = null;
        this.token = null;
        this.isLoggedIn = false;
    }
}
