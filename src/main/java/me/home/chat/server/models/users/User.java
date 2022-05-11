package me.home.chat.server.models.users;

public class User {
    private String username;
    private String password;
    private Role role;
    private UserMeta info;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserMeta getInfo() {
        return info;
    }

    public void setInfo(UserMeta info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (hashCode() != obj.hashCode()) return false;
        if (!(obj instanceof User)) return false;
        User other = (User) obj;
        if (this == other) return true;
        if (!username.equals(other.username)) return false;
        if (!password.equals(other.password)) return false;
        if (role.getId() != other.role.getId()) return false;
        return info.getId() == other.info.getId();
    }

    @Override
    public int hashCode() {
        return 31 * (1 + username.hashCode() + password.hashCode() + Long.hashCode(role.getId()) + Long.hashCode(info.getId()));
    }
}
