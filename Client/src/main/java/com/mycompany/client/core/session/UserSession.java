package com.mycompany.client.core.session;

import com.mycompany.client.auth.model.User;

/**
 * Singleton class to manage the current logged-in user session
 */
public class UserSession {

    private static UserSession instance;
    private User currentUser;
    private String guestUsername = "Player";

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setGuestUsername(String name) {
        if (name != null && !name.isBlank()) {
            this.guestUsername = name;
        }
    }

    public String getUsername() {
        if (isLoggedIn()) {
            return currentUser.getUsername();
        }
        return guestUsername;
    }

    public void clearSession() {
        currentUser = null;
        guestUsername = "Player";
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
