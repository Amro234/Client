package com.mycompany.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        masterVolumeToggle.selectedProperty().addListener((obs, oldVal, isOn) -> {
            TranslateTransition tt
                    = new TranslateTransition(Duration.millis(180), switchThumb);
            tt.setToX(isOn ? 10 : -10);
            tt.play();
        });

        setupSliderFill(musicSlider);
        setupSliderFill(effectsSlider);
        bindPercentageText(musicSlider, musicPercentText);
        bindPercentageText(effectsSlider, effectsPercentText);
    }

    private void setupSliderFill(Slider slider) {

        javafx.application.Platform.runLater(() -> {

            updateSliderTrack(slider);

            slider.valueProperty().addListener((obs, oldVal, newVal) -> {
                updateSliderTrack(slider);
            });
        });
    }

    private void updateSliderTrack(Slider slider) {
        var track = slider.lookup(".track");
        if (track == null) {
            return;
        }

        double percent
                = (slider.getValue() - slider.getMin())
                / (slider.getMax() - slider.getMin()) * 100;

        track.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                + "#2b8cee 0%, "
                + "#2b8cee " + percent + "%, "
                + "#d0d7de " + percent + "%, "
                + "#d0d7de 100%);"
        );
    }

    private void bindPercentageText(Slider slider, Text text) {
        
        text.setText((int) slider.getValue() + "%");

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            text.setText(newVal.intValue() + "%");
        });
    }
}
