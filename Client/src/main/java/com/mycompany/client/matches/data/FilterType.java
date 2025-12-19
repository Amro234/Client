package com.mycompany.client.matches.data;

/**
 * Enum representing the filter types for match history.
 */
public enum FilterType {
    ALL("All Games"),
    VICTORY("Wins"),
    DEFEAT("Losses"),
    DRAW("Draws");

    private final String displayName;

    FilterType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if a match result passes this filter.
     */
    public boolean matches(MatchResult result) {
        if (this == ALL) {
            return true;
        }
        return this.name().equals(result.name());
    }
}
