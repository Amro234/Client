package com.mycompany.client.matches.data;

/**
 * Data class representing a match in the match history.
 */




public class MatchData {

    private final String opponent;
    private final MatchResult result;
    private final String date;
    private final String time;
    private final int movesCount;

    
    private final String recordingFileName;

    public MatchData(String opponent,
                     MatchResult result,
                     String date,
                     String time,
                     int movesCount,
                     String recordingFileName) {

        this.opponent = opponent;
        this.result = result;
        this.date = date;
        this.time = time;
        this.movesCount = movesCount;
        this.recordingFileName = recordingFileName;
    }

    public String getOpponent() { return opponent; }
    public MatchResult getResult() { return result; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getMovesCount() { return movesCount; }
    public String getRecordingFileName() { return recordingFileName; }

    public boolean matchesSearch(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) return true;
        return opponent.toLowerCase().contains(searchText.toLowerCase().trim());
    }
}

