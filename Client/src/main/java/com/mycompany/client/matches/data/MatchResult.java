package com.mycompany.client.matches.data;

/**
 * Enum representing the possible results of a match.
 */
public enum MatchResult {
    VICTORY("VICTORY", "/assets/images/vic_cup.png", "badge-victory", "#F1F5F9"),
    DEFEAT("DEFEAT", "/assets/images/loss_x.png", "badge-defeat", "#FEF2F2"),
    DRAW("DRAW", "/assets/images/draw_handshake.png", "badge-draw", "#F1F5F9");

    private final String displayName;
    private final String iconPath;
    private final String badgeStyleClass;
    private final String backgroundColor;

    MatchResult(String displayName, String iconPath, String badgeStyleClass, String backgroundColor) {
        this.displayName = displayName;
        this.iconPath = iconPath;
        this.badgeStyleClass = badgeStyleClass;
        this.backgroundColor = backgroundColor;
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

    public String getBackgroundColor() {
        return backgroundColor;
    }
}
