package com.mycompany.client.auth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class TokenManager {

    private static final String TOKEN_DIR = "data";
    private static final String TOKEN_FILE = "session.dat";
    private static final Path TOKEN_PATH = Paths.get(TOKEN_DIR, TOKEN_FILE);


    public static boolean saveToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            System.err.println("Cannot save null or empty token");
            return false;
        }

        try {
            Path tokenDir = TOKEN_PATH.getParent();
            if (!Files.exists(tokenDir)) {
                Files.createDirectories(tokenDir);
            }

            Files.writeString(TOKEN_PATH, token);
            System.out.println("Token saved successfully to: " + TOKEN_PATH);
            return true;

        } catch (IOException e) {
            System.err.println("Error saving token: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

   
    public static String getToken() {
        if (!Files.exists(TOKEN_PATH)) {
            System.out.println("No token file found");
            return null;
        }

        try {
            String token = Files.readString(TOKEN_PATH).trim();
            if (token.isEmpty()) {
                System.out.println("Token file is empty");
                return null;
            }
            return token;

        } catch (IOException e) {
            System.err.println("Error reading token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean deleteToken() {
        if (!Files.exists(TOKEN_PATH)) {
            System.out.println("No token file to delete");
            return true;
        }

        try {
            Files.delete(TOKEN_PATH);
            System.out.println("Token deleted successfully");
            return true;

        } catch (IOException e) {
            System.err.println("Error deleting token: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

  
    public static boolean hasToken() {
        if (!Files.exists(TOKEN_PATH)) {
            return false;
        }

        try {
            String token = Files.readString(TOKEN_PATH).trim();
            return !token.isEmpty();

        } catch (IOException e) {
            System.err.println("Error checking token: " + e.getMessage());
            return false;
        }
    }

  
    public static String getTokenFilePath() {
        return TOKEN_PATH.toAbsolutePath().toString();
    }
}
