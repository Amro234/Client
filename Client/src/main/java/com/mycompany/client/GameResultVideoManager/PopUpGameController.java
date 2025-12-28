package com.mycompany.client.GameResultVideoManager;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PopUpGameController implements Initializable {

    @FXML
    private MediaView mediaView;

    private MediaPlayer mediaPlayer;

    @FXML
    private Label lblStatus;

    @FXML
    private Button btnPlayAgain;

    @FXML
    private Button btnClosePopup;

    private Stage popupStage;
    private Media media;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization if needed
    }

    public void setPopupStage(Stage stage) {
        this.popupStage = stage;
    }

    public void setPlayAgainVisablility(boolean isVisible) {
        btnPlayAgain.setVisible(isVisible);
        btnPlayAgain.setManaged(isVisible);
    }

    public void setPlayAgainBtnFunc(Runnable func) {
        btnPlayAgain.setOnAction(e -> {
            if (func != null)
                func.run();
            closePopup();
            stopVideo();
        });
    }

    public void setCloseBtnFunc(Runnable func) {
        btnClosePopup.setOnAction(e -> {
            if (func != null)
                func.run();
            closePopup();
            stopVideo();
        });
    }

    public void closePopup() {
        if (popupStage != null) {
            popupStage.close();
        }
    }

    public void stopVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    public void setPopupStatusMsg(String msg, String videoPath) {
        lblStatus.setText(msg);

        try {
            URL videoUrl = getClass().getResource(videoPath);
            if (videoUrl != null) {
                media = new Media(videoUrl.toExternalForm());
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
                mediaPlayer.setOnEndOfMedia(() -> {
                    // Logic to handle end of media
                    // User's code had: mediaPlayer.seek(Duration.seconds(1))
                    // But usually we want to stop or close if it's a game over video.
                    // However, I'll follow the provided pattern.
                    mediaPlayer.seek(Duration.seconds(0));
                });
                mediaPlayer.setAutoPlay(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closePopupBtnHandler(ActionEvent event) {
        closePopup();
        stopVideo();
    }
}
