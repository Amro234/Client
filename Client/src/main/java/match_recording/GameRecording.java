/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package match_recording;

import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author Mohamed_Ali
 */

    public class GameRecording {

    public String playerName;
    public String opponentPlayerName;
    public String duration;
    public String date;
    public String time;
    public String status;
    public char myCharacter;
    public char firstPlayer;
    public Map<String, String> steps = new LinkedHashMap<>();
    public Map<String, String> getSteps() {
    return steps;
}
public char getFirstPlayer() {
    return firstPlayer;
}


    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("playerName", playerName);
        json.put("opponentPlayerName", opponentPlayerName);
        json.put("duration", duration);
        json.put("date", date);
        json.put("time", time);
        json.put("status", status);
        json.put("myCharacter", String.valueOf(myCharacter));
        json.put("firstPlayer", String.valueOf(firstPlayer));
        json.put("steps", steps);
        return json;
    }

    public static GameRecording fromJSON(JSONObject json) {
        GameRecording r = new GameRecording();
        r.playerName = json.getString("playerName");
        r.opponentPlayerName = json.getString("opponentPlayerName");
        r.duration = json.getString("duration");
        r.date = json.getString("date");
        r.time = json.getString("time");
        r.status = json.getString("status");
        r.myCharacter = json.getString("myCharacter").charAt(0);
        r.firstPlayer = json.getString("firstPlayer").charAt(0);

        JSONObject stepsJson = json.getJSONObject("steps");
        for (String key : stepsJson.keySet()) {
            r.steps.put(key, stepsJson.getString(key));
        }
        return r;
    }
}


