/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client.match_recording;

import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mohamed_Ali
 */
public class RecordingManager {

    private final String recordingsPath = System.getProperty("user.home") + "/.tic_tac_toe/recordings";

    public void saveRecording(GameRecording recording, String username) {
        try {
            File dir = getRecordingsDirectory(username);
            if (!dir.exists())
                dir.mkdirs();

            String fileName = "game_" + System.currentTimeMillis() + ".json";
            File file = new File(dir, fileName);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(recording.toJSON().toString(4));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameRecording loadRecording(String filename, String username) throws Exception {
        File file = new File(getRecordingsDirectory(username), filename);
        String content = Files.readString(file.toPath());
        return GameRecording.fromJSON(new JSONObject(content));
    }

    public List<GameRecording> getAllRecordings(String username) {
        List<GameRecording> list = new ArrayList<>();
        File dir = getRecordingsDirectory(username);

        if (!dir.exists())
            return list;

        for (File file : dir.listFiles()) {
            try {
                String content = Files.readString(file.toPath());
                list.add(GameRecording.fromJSON(new JSONObject(content)));
            } catch (Exception ignored) {
            }
        }
        return list;
    }

    private File getRecordingsDirectory(String username) {
        return new File(recordingsPath + "/" + username);
    }
}
