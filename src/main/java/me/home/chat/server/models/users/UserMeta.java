package me.home.chat.server.models.users;

import java.time.LocalDateTime;

public class UserMeta {
    private long id;
    private LocalDateTime lastVisit;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(LocalDateTime lastVisit) {
        this.lastVisit = lastVisit;
    }
}
