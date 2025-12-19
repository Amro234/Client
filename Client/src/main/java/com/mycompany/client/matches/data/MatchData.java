package com.mycompany.client.matches.data;

/**
 * Data class representing a match in the match history.
 */
public class MatchData {
    private final String opponent;
    private final MatchResult result;
    private final String date;
    private final String time;
    private final int score;

    public MatchData(String opponent, MatchResult result, String date, String time, int score) {
        this.opponent = opponent;
        this.result = result;
        this.date = date;
        this.time = time;
        this.score = score;
    }

    public String getOpponent() {
        return opponent;
    }

    public MatchResult getResult() {
        return result;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getScore() {
        return score;
    }

    /**
     * Check if opponent name contains the search text (case-insensitive).
     */
    public boolean matchesSearch(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return true;
        }
        return opponent.toLowerCase().contains(searchText.toLowerCase().trim());
    }
}
