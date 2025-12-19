package com.mycompany.client.matches.data;

/**
 * Enum representing the possible results of a match.
 */
public enum MatchResult {
    VICTORY("VICTORY", "/assets/images/vic_cup.png", "badge-victory"),
    DEFEAT("DEFEAT", "/assets/images/loss_x.png", "badge-defeat"),
    DRAW("DRAW", "/assets/images/draw_handshake.png", "badge-draw");

    private final String displayName;
    private final String iconPath;
    private final String badgeStyleClass;

    MatchResult(String displayName, String iconPath, String badgeStyleClass) {
        this.displayName = displayName;
        this.iconPath = iconPath;
        this.badgeStyleClass = badgeStyleClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getBadgeStyleClass() {
        return badgeStyleClass;
    }
}
