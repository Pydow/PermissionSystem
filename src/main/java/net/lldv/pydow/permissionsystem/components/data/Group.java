package net.lldv.pydow.permissionsystem.components.data;

import java.util.List;

public class Group {

    private final String group;
    private final String chatPrefix;
    private final String displayPrefix;
    private final List<String> permissions;
    private final List<String> groupParents;

    public Group(String group, String chatPrefix, String displayPrefix, List<String> permissions, List<String> groupParents) {
        this.group = group;
        this.chatPrefix = chatPrefix;
        this.displayPrefix = displayPrefix;
        this.permissions = permissions;
        this.groupParents = groupParents;
    }

    public String getGroup() {
        return group;
    }

    public String getPrefix() {
        return chatPrefix;
    }

    public String getDisplayPrefix() {
        return displayPrefix;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public List<String> getGroupParents() {
        return groupParents;
    }
}
