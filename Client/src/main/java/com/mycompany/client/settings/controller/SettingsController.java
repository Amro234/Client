package com.mycompany.client.settings.controller;

import com.mycompany.client.settings.manager.BackgroundMusicManager;
import com.mycompany.client.settings.manager.SettingsManager;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class SettingsController implements Initializable {

    @FXML
    private ToggleButton masterVolumeToggle;

    @FXML
    private Circle switchThumb;

    @FXML
    private Slider musicSlider;

    @FXML
    private Slider effectsSlider;

    @FXML
    private Text musicPercentText;

    @FXML
    private Text effectsPercentText;

    @FXML
    private Text currentSongText;

    @FXML
    private Button saveBtn;

    @FXML
    private Button discardBtn;

    private double originalMusicVolume;
    private double originalEffectsVolume;
    private boolean originalMasterVolumeOn;
    private int originalTrackIndex;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        storeOriginalValues();

        saveBtn.setVisible(false);
        discardBtn.setVisible(false);
        saveBtn.setManaged(false);
        discardBtn.setManaged(false);

        masterVolumeToggle.setSelected(SettingsManager.isMasterVolumeOn());
        switchThumb.setTranslateX(SettingsManager.isMasterVolumeOn() ? 10 : -10);

        masterVolumeToggle.selectedProperty().addListener((obs, oldVal, isOn) -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(180), switchThumb);
            tt.setToX(isOn ? 10 : -10);
            tt.play();
            SettingsManager.setMasterVolumeOn(isOn);
            checkForChanges();
        });

        musicSlider.setValue(SettingsManager.getMusicVolume());
        effectsSlider.setValue(SettingsManager.getEffectsVolume());

        BackgroundMusicManager.setVolume(SettingsManager.isMasterVolumeOn()
                ? SettingsManager.getMusicVolume() / 100.0
                : 0);

        musicSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue() / 100.0;
            BackgroundMusicManager.setVolume(SettingsManager.isMasterVolumeOn() ? volume : 0);
            SettingsManager.setMusicVolume(newVal.doubleValue());
            checkForChanges();
        });

        effectsSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SettingsManager.setEffectsVolume(newVal.doubleValue());
            checkForChanges();
        });

        setupSliderFill(musicSlider);
        setupSliderFill(effectsSlider);

        bindPercentageText(musicSlider, musicPercentText);
        bindPercentageText(effectsSlider, effectsPercentText);

        masterVolumeToggle.selectedProperty().addListener((obs, oldVal, isOn) -> {
            BackgroundMusicManager.setVolume(isOn ? musicSlider.getValue() / 100.0 : 0);
        });

    }

    private void storeOriginalValues() {
        originalMusicVolume = SettingsManager.getMusicVolume();
        originalEffectsVolume = SettingsManager.getEffectsVolume();
        originalMasterVolumeOn = SettingsManager.isMasterVolumeOn();
        originalTrackIndex = SettingsManager.getCurrentTrackIndex();
    }

    private void checkForChanges() {
        boolean hasChanges = Math.abs(musicSlider.getValue() - originalMusicVolume) > 0.01 ||
                Math.abs(effectsSlider.getValue() - originalEffectsVolume) > 0.01 ||
                masterVolumeToggle.isSelected() != originalMasterVolumeOn ||
                SettingsManager.getCurrentTrackIndex() != originalTrackIndex;

        saveBtn.setVisible(hasChanges);
        discardBtn.setVisible(hasChanges);
        saveBtn.setManaged(hasChanges);
        discardBtn.setManaged(hasChanges);
    }

    @FXML
    private void saveChanges() {
        SettingsManager.saveSettings();
        storeOriginalValues();
        checkForChanges();
    }

    @FXML
    private void discardChanges() {

        SettingsManager.reloadSettings();

        musicSlider.setValue(originalMusicVolume);
        effectsSlider.setValue(originalEffectsVolume);
        masterVolumeToggle.setSelected(originalMasterVolumeOn);

        while (BackgroundMusicManager.getCurrentIndex() != originalTrackIndex) {
            BackgroundMusicManager.next();
        }
        updateSongName();

        BackgroundMusicManager.setVolume(originalMasterVolumeOn
                ? originalMusicVolume / 100.0
                : 0);

        checkForChanges();
    }

    @FXML
    private void restoreDefaults() {

        musicSlider.setValue(SettingsManager.getDefaultMusicVolume());
        effectsSlider.setValue(SettingsManager.getDefaultEffectsVolume());
        masterVolumeToggle.setSelected(SettingsManager.getDefaultMasterVolumeOn());

        while (BackgroundMusicManager.getCurrentIndex() != SettingsManager.getDefaultTrackIndex()) {
            BackgroundMusicManager.next();
        }
        SettingsManager.setCurrentTrackIndex(SettingsManager.getDefaultTrackIndex());
        updateSongName();

        BackgroundMusicManager.setVolume(SettingsManager.getDefaultMasterVolumeOn()
                ? SettingsManager.getDefaultMusicVolume() / 100.0
                : 0);

        checkForChanges();
    }

    private void setupSliderFill(Slider slider) {

        javafx.application.Platform.runLater(() -> {

            updateSliderTrack(slider);

            slider.valueProperty().addListener((obs, oldVal, newVal) -> {
                updateSliderTrack(slider);
            });
        });

        updateSongName();
    }

    private void updateSliderTrack(Slider slider) {
        var track = slider.lookup(".track");
        if (track == null) {
            return;
        }

        double percent = (slider.getValue() - slider.getMin())
                / (slider.getMax() - slider.getMin()) * 100;

        track.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                        + "#2b8cee 0%, "
                        + "#2b8cee " + percent + "%, "
                        + "#d0d7de " + percent + "%, "
                        + "#d0d7de 100%);");
    }

    private void bindPercentageText(Slider slider, Text text) {

        text.setText((int) slider.getValue() + "%");

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            text.setText(newVal.intValue() + "%");
        });
    }

    @FXML
    private void nextSong() {
        BackgroundMusicManager.next();
        SettingsManager.setCurrentTrackIndex(BackgroundMusicManager.getCurrentIndex());
        updateSongName();
        checkForChanges();
    }

    @FXML
    private void previousSong() {
        BackgroundMusicManager.previous();
        SettingsManager.setCurrentTrackIndex(BackgroundMusicManager.getCurrentIndex());
        updateSongName();
        checkForChanges();
    }

    private void updateSongName() {
        currentSongText.setText(BackgroundMusicManager.getCurrentTrackName());
    }
}
