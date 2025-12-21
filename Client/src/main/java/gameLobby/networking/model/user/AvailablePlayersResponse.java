package gameLobby.networking.model.user;

import java.util.List;

public class AvailablePlayersResponse {
    private final List<OnlineUser> players;

    public AvailablePlayersResponse(List<OnlineUser> players) {
        this.players = players;
    }

    public List<OnlineUser> getPlayers() {
        return players;
    }
}
