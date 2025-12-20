package com.mycompany.client.auth;

import com.mycompany.client.auth.model.User;


public class UserSession {

    private static UserSession instance;

    private User currentUser;
    private String token;
    private boolean isLoggedIn;

    
    private UserSession() {
        this.isLoggedIn = false;
    }

   
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

   
    public void login(User user, String token) {
        if (user == null || token == null || token.trim().isEmpty()) {
            System.err.println("Cannot login with null user or token");
            return;
        }

        this.currentUser = user;
        this.token = token;
        this.isLoggedIn = true;

        boolean saved = TokenManager.saveToken(token);
        if (saved) {
            System.out.println("User logged in: " + user.getUsername());
        } else {
            System.err.println("Warning: Token not saved to local storage");
        }
    }

    
    public void logout() {
        if (currentUser != null) {
            System.out.println("User logged out: " + currentUser.getUsername());
        }

        this.currentUser = null;
        this.token = null;
        this.isLoggedIn = false;

        TokenManager.deleteToken();
    }

   
    public boolean isLoggedIn() {
        return isLoggedIn && currentUser != null && token != null;
    }

    
    public User getCurrentUser() {
        return currentUser;
    }

    
    public String getToken() {
        return token;
    }

   
    public String getCurrentUsername() {
        return (currentUser != null) ? currentUser.getUsername() : "Guest";
    }

   
    public void clearSession() {
        this.currentUser = null;
        this.token = null;
        this.isLoggedIn = false;
    }
}
