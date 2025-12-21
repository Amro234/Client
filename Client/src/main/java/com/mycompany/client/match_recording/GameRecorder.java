/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client.match_recording;

import com.mycompany.client.gameboard.model.GameMode;
import java.time.LocalDateTime;

/**
 *
 * @author Mohamed_Ali
 */
public class GameRecorder {

    private GameRecording recording;
    private long startTime;
    private int stepCount;
    private boolean isRecording;

    public void startRecording(GameMode mode, String player, String opponent, char myChar) {
        recording = new GameRecording();
        recording.playerName = player;
        recording.opponentPlayerName = opponent;
        recording.myCharacter = myChar;
        recording.firstPlayer = 'X';

        startTime = System.currentTimeMillis();
        stepCount = 0;
        isRecording = true;
    }

    public void recordMove(int row, int col) {
        if (!isRecording)
            return;
        stepCount++;
        recording.steps.put("step" + stepCount, row + "," + col);
    }

    public void stopRecording(String status) {
        if (!isRecording)
            return;

        long durationMs = System.currentTimeMillis() - startTime;
        recording.duration = (durationMs / 1000) + "s";
        recording.status = status;

        LocalDateTime now = LocalDateTime.now();
        recording.date = now.toLocalDate().toString();
        recording.time = now.toLocalTime().withNano(0).toString();

        isRecording = false;
    }

    public GameRecording getRecording() {
        return recording;
    }
}
