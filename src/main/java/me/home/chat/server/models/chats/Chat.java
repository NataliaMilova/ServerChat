package me.home.chat.server.models.chats;

import me.home.chat.server.models.users.Role;
import me.home.chat.server.models.users.User;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private long id;
    private String name;
    private List<User> users;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        if (this.users == null) users = new ArrayList<>();
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (hashCode() != obj.hashCode()) return false;
        if (!(obj instanceof Chat)) return false;
        Chat other = (Chat) obj;
        if (this == other) return true;
        if (id != other.id) return false;
        if (!name.equals(other.name)) return false;
        return users.equals(other.users);
    }

    @Override
    public int hashCode() {
        return 31 * (1 + Long.hashCode(id) + name.hashCode() + users.hashCode());
    }
}
