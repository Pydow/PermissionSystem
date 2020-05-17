package net.lldv.pydow.permissionsystem.components.data;

import java.util.List;

public class User {

    private final String user;
    private final String group;
    private final long duration;
    private final List<String> permissions;

    public User(String user, String group, long duration, List<String> permissions) {
        this.user = user;
        this.group = group;
        this.duration = duration;
        this.permissions = permissions;
    }

    public String getUser() {
        return user;
    }

    public String getGroup() {
        return group;
    }

    public long getDuration() {
        return duration;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
