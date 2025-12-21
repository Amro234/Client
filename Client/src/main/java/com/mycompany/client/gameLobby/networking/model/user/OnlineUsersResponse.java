package com.mycompany.client.gameLobby.networking.model.user;

import java.util.List;

public class OnlineUsersResponse {
    private final List<OnlineUser> users;
    private final int count;

    public OnlineUsersResponse(List<OnlineUser> users, int count) {
        this.users = users;
        this.count = count;
    }

    public List<OnlineUser> getUsers() {
        return users;
    }

    public int getCount() {
        return count;
    }
}
