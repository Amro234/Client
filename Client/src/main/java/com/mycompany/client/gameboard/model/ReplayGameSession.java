/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client.gameboard.model;

/**
 *
 * @author Mohamed_Ali
 */
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import match_recording.GameRecording;

import java.util.ArrayList;
import java.util.List;

public class ReplayGameSession extends ReplaySession {

    private Timeline timeline;
    private GameRecording recording;

    public ReplayGameSession(SessionListener listener,
            String p1Name,
            String p2Name,
            GameRecording recording) {
        super(listener, p1Name, p2Name);
        this.recording = recording;
        loadMovesFromRecording();
    }

    private void loadMovesFromRecording() {
        recordedMoves = new ArrayList<>();

        boolean isX = recording.firstPlayer == 'X';

        List<String> keys = new ArrayList<>(recording.steps.keySet());
        keys.sort((a, b)
                -> Integer.compare(
                        Integer.parseInt(a.replace("step", "")),
                        Integer.parseInt(b.replace("step", ""))
                )
        );

        for (String key : keys) {
            String[] parts = recording.steps.get(key).split(",");
            recordedMoves.add(
                    new Move(
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]),
                            isX ? 'X' : 'O',
                            0,
                            isX ? player1Name : player2Name
                    )
            );
            isX = !isX;
        }
    }

    @Override
    public void play() {

        if (isPlaying) {
            return;
        }

        timeline = new Timeline();
        timeline.setCycleCount(1);

        for (int i = currentMoveIndex; i < recordedMoves.size(); i++) {
            final int index = i;

            KeyFrame frame = new KeyFrame(
                    Duration.seconds((i - currentMoveIndex) * 1.0 / playbackSpeed),
                    e -> {
                        Move m = recordedMoves.get(index);
                        listener.onBoardUpdate(m.row, m.col, m.symbol);
                        currentMoveIndex = index + 1;
                    }
            );

            timeline.getKeyFrames().add(frame);
        }

        timeline.setOnFinished(e -> {
            isPlaying = false;
            listener.onReplayFinished();
        });

        isPlaying = true;
        timeline.play();
    }

    @Override
    public void pause() {
        if (timeline != null) {
            timeline.pause();
        }
    }

    @Override
    public void resume() {
        if (timeline != null) {
            timeline.play();
            isPlaying = true;
        }
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
        isPlaying = false;
        currentMoveIndex = 0;
    }

    public void reset() {
        if (timeline != null) {
            timeline.stop();
        }
        currentMoveIndex = 0;
        isPlaying = false;
    }

    @Override
    public void stepForward() {
    }

    @Override
    public void stepBackward() {
    }

    @Override
    public void setPlaybackSpeed(double speed) {
        this.playbackSpeed = speed;
    }

    @Override
    public int getTotalMoves() {
        return recordedMoves.size();
    }

    @Override
    public int getCurrentMoveIndex() {
        return currentMoveIndex;
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void handleCellClick(int row, int col) {

    }

    @Override
    public void loadRecording(String recordingJson) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void loadRecordingFromFile(String filePath) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void jumpToMove(int moveIndex) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
