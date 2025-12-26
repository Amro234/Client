/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client.match_recording;

import com.mycompany.client.gameboard.model.ReplayGameSession;

/**
 *
 * @author Mohamed_Ali
 */


public class GameReplayManager {

    private ReplayGameSession replaySession;
    private boolean fastSpeed = false;

    public void startReplay(
            ReplayGameSession session,
            Runnable onReplayFinished
    ) {
        this.replaySession = session;

        replaySession.setPlaybackSpeed(1.0);
        replaySession.play();

        replaySession.setOnReplayFinished(onReplayFinished);
    }

    public void play() {
        if (replaySession != null) {
            replaySession.play();
        }
    }

    public  void pause() {
        if (replaySession != null) {
            replaySession.pause();
        }
    }

    public void restart(GameRecording recording,
                        ReplayGameSession newSession,
                        Runnable onReplayFinished) {

        replaySession.stop();
        startReplay(newSession, onReplayFinished);
    }

    public void toggleSpeed() {
        if (replaySession == null) return;

        if (fastSpeed) {
            replaySession.setPlaybackSpeed(1.0);
            fastSpeed = false;
        } else {
            replaySession.setPlaybackSpeed(2.0);
            fastSpeed = true;
        }
    }

    public boolean isFastSpeed() {
        return fastSpeed;
    }

    public void reset() {
        if (replaySession != null) {
            replaySession.stop();
        }
        replaySession = null;
        fastSpeed = false;
    }
}
