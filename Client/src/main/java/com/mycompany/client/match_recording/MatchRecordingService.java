/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client.match_recording;

import com.mycompany.client.core.session.UserSession;
import com.mycompany.client.gameboard.model.GameMode;

/**
 *
 * @author Mohamed_Ali
 */


public class MatchRecordingService {

    private final GameRecorder gameRecorder = new GameRecorder();
    private final RecordingManager recordingManager = new RecordingManager();

    private boolean recordingEnabled = false;
    private boolean stoppedManually = false;

    public void startRecording(GameMode mode,
                               String player1,
                               String player2,
                               char starterSymbol) {

        recordingEnabled = true;
        stoppedManually = false;

        gameRecorder.startRecording(
                mode,
                player1,
                player2,
                starterSymbol
        );
    }

    public void recordMove(int row, int col) {
        if (!recordingEnabled) return;
        gameRecorder.recordMove(row, col);
    }

    public void finishRecording(String status) {
        if (!recordingEnabled || stoppedManually) return;

        gameRecorder.stopRecording(status);

        String username = UserSession.getInstance().getCurrentUser().getUsername();

        recordingManager.saveRecording(
                gameRecorder.getRecording(),
                username
        );

        recordingEnabled = false;
    }

    public void cancelRecording() {
        if (!recordingEnabled) return;

        stoppedManually = true;
        recordingEnabled = false;

        gameRecorder.stopRecording("CANCELLED");
    }

    public void reset() {
        recordingEnabled = false;
        stoppedManually = false;
    }

    public boolean isRecordingEnabled() {
        return recordingEnabled;
    }
}

