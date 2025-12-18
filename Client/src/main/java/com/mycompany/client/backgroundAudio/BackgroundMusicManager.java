package com.mycompany.client.backgroundAudio;

import com.mycompany.client.settings.SettingsManager;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.List;

public class BackgroundMusicManager {

    private static final List<String> TRACKS = List.of(
            "/assets/audios/Ground Theme.mp3",
            "/assets/audios/Underwater Theme.mp3");

    private static int currentIndex = 0;
    private static MediaPlayer mediaPlayer;
    private static double volume = 1.0; // range: 0.0 → 1.0

    public static void init() {
        if (mediaPlayer == null) {
            // Load saved settings
            SettingsManager.init();
            currentIndex = SettingsManager.getCurrentTrackIndex();
            volume = SettingsManager.isMasterVolumeOn()
                    ? SettingsManager.getMusicVolume() / 100.0
                    : 0;
            playCurrent();
        }
    }

    private static void playCurrent() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        URL resource = BackgroundMusicManager.class.getResource(TRACKS.get(currentIndex));
        Media media = new Media(resource.toExternalForm());

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(volume);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    public static void next() {
        currentIndex = (currentIndex + 1) % TRACKS.size();
        playCurrent();
    }

    public static void previous() {
        currentIndex = (currentIndex - 1 + TRACKS.size()) % TRACKS.size();
        playCurrent();
    }

    public static void setVolume(double newVolume) {
        volume = Math.max(0, Math.min(1, newVolume)); // clamp
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public static double getVolume() {
        return volume;
    }

    public static String getCurrentTrackName() {
        return TRACKS.get(currentIndex)
                .replace("/assets/audios/", "")
                .replace(".mp3", "");
    }

    public static int getTrackCount() {
        return TRACKS.size();
    }

    public static int getCurrentIndex() {
        return currentIndex;
    }

    public static void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public static void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }
}
