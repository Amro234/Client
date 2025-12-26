package com.mycompany.client.settings.manager;

import javafx.scene.media.AudioClip;
import java.net.URL;

public class SoundEffectsManager {

    private static AudioClip challengeSound;
    private static AudioClip clickSound;

    public static void init() {
        // Load saved settings
        SettingsManager.init();

        // Load sound effects
        loadSounds();
    }

    private static void loadSounds() {
        try {
            URL challengeUrl = SoundEffectsManager.class.getResource("/assets/audios/Challenge.wav");
            URL clickUrl = SoundEffectsManager.class.getResource("/assets/audios/Click-Sound.wav");

            if (challengeUrl != null) {
                challengeSound = new AudioClip(challengeUrl.toExternalForm());
            } else {
                System.err.println("Challenge.wav not found!");
            }

            if (clickUrl != null) {
                clickSound = new AudioClip(clickUrl.toExternalForm());
            } else {
                System.err.println("Click-Sound.wav not found!");
            }
        } catch (Exception e) {
            System.err.println("Error loading sound effects: " + e.getMessage());
        }
    }

    private static double getCurrentEffectsVolume() {
        // Always get the current volume from SettingsManager
        if (!SettingsManager.isMasterVolumeOn()) {
            return 0.0;
        }
        return SettingsManager.getEffectsVolume() / 100.0;
    }

    public static void playChallenge() {
        double volume = getCurrentEffectsVolume();
        if (challengeSound != null && volume > 0) {
            System.out.println("Playing challenge sound at volume: " + volume);
            challengeSound.play(volume);
        } else {
            System.out.println("Challenge sound is null or volume is zero.");
        }
    }

    public static void playClick() {
        double volume = getCurrentEffectsVolume();
        if (clickSound != null && volume > 0) {
            clickSound.play(volume);
        }
    }
}
