package com.mycompany.client.settings.manager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsManager {

    private static final String SETTINGS_FILE = getAppDataPath()
            + File.separator + "tic tac toe" + File.separator + "settings.dat";

    private static String getAppDataPath() {
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null) {
            return localAppData;
        }

        return System.getProperty("user.home") + File.separator + ".local" + File.separator + "share";
    }

    // Default values
    private static final double DEFAULT_MUSIC_VOLUME = 100.0;
    private static final double DEFAULT_EFFECTS_VOLUME = 100.0;
    private static final boolean DEFAULT_MASTER_VOLUME_ON = true;
    private static final int DEFAULT_TRACK_INDEX = 0;

    // Getters for default values
    public static double getDefaultMusicVolume() {
        return DEFAULT_MUSIC_VOLUME;
    }

    public static double getDefaultEffectsVolume() {
        return DEFAULT_EFFECTS_VOLUME;
    }

    public static boolean getDefaultMasterVolumeOn() {
        return DEFAULT_MASTER_VOLUME_ON;
    }

    public static int getDefaultTrackIndex() {
        return DEFAULT_TRACK_INDEX;
    }

    // Current settings
    private static double musicVolume = DEFAULT_MUSIC_VOLUME;
    private static double effectsVolume = DEFAULT_EFFECTS_VOLUME;
    private static boolean masterVolumeOn = DEFAULT_MASTER_VOLUME_ON;
    private static int currentTrackIndex = DEFAULT_TRACK_INDEX;

    private static boolean initialized = false;

    public static void init() {
        if (!initialized) {
            loadSettings();
            initialized = true;
        }
    }

    private static void loadSettings() {
        File file = new File(SETTINGS_FILE);

        if (!file.exists()) {
            // Create directory if it doesn't exist
            file.getParentFile().mkdirs();
            // Create file with default values
            saveSettings();
            return;
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            musicVolume = dis.readDouble();
            effectsVolume = dis.readDouble();
            masterVolumeOn = dis.readBoolean();
            currentTrackIndex = dis.readInt();
        } catch (IOException e) {
            System.err.println("Error loading settings: " + e.getMessage());
            // Reset to defaults if file is corrupted
            resetToDefaults();
            saveSettings();
        }
    }

    public static void saveSettings() {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(SETTINGS_FILE))) {
            dos.writeDouble(musicVolume);
            dos.writeDouble(effectsVolume);
            dos.writeBoolean(masterVolumeOn);
            dos.writeInt(currentTrackIndex);
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }

    private static void resetToDefaults() {
        musicVolume = DEFAULT_MUSIC_VOLUME;
        effectsVolume = DEFAULT_EFFECTS_VOLUME;
        masterVolumeOn = DEFAULT_MASTER_VOLUME_ON;
        currentTrackIndex = DEFAULT_TRACK_INDEX;
    }

    // Getters and Setters

    public static double getMusicVolume() {
        return musicVolume;
    }

    public static void setMusicVolume(double volume) {
        musicVolume = Math.max(0, Math.min(100, volume));
    }

    public static double getEffectsVolume() {
        return effectsVolume;
    }

    public static void setEffectsVolume(double volume) {
        effectsVolume = Math.max(0, Math.min(100, volume));
    }

    public static boolean isMasterVolumeOn() {
        return masterVolumeOn;
    }

    public static void setMasterVolumeOn(boolean isOn) {
        masterVolumeOn = isOn;
    }

    public static int getCurrentTrackIndex() {
        return currentTrackIndex;
    }

    public static void setCurrentTrackIndex(int index) {
        currentTrackIndex = index;
    }

    public static void reloadSettings() {
        loadSettings();
    }
}
