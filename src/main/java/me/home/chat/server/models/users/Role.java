package me.home.chat.server.models.users;

public class Role {
    private long id;
    private String name;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (hashCode() != obj.hashCode()) return false;
        if (!(obj instanceof Role)) return false;
        Role other = (Role) obj;
        if (this == other) return true;
        if (id != other.id) return false;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return 31 * (1 + Long.hashCode(id) + name.hashCode());
    }
}
