package net.lldv.pydow.permissionsystem.components.api;

import cn.nukkit.Server;
import cn.nukkit.player.Player;
import com.mongodb.client.MongoCollection;
import net.lldv.pydow.permissionsystem.components.api.database.MongoDB;
import net.lldv.pydow.permissionsystem.components.data.Group;
import net.lldv.pydow.permissionsystem.components.data.User;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PermissionAPI {

    private static String defaultGroup;
    public static HashMap<String, Group> cachedGroups = new HashMap<>();
    public static HashMap<String, User> cachedPlayer = new HashMap<>();

    public static void createUserData(String user) {
        List<String> list = new ArrayList<>();
        Document document = new Document("user", user)
                .append("group", getDefaultGroup())
                .append("duration", (long) -1)
                .append("permissions", list);
        MongoDB.getUserCollection().insertOne(document);
    }

    public static void createGroup(String group) {
        CompletableFuture.runAsync(() -> {
            List<String> list = new ArrayList<>();
            Document document = new Document("group", group)
                    .append("chatprefix", "")
                    .append("displayprefix", "")
                    .append("permissions", list)
                    .append("parents", list);
            MongoDB.getGroupCollection().insertOne(document);
            cachedGroups.put(group, new Group(group, "", "", list, list));
        });
    }

    public static void removeGroup(String group) {
        CompletableFuture.runAsync(() -> {
            MongoCollection<Document> collection = MongoDB.getGroupCollection();
            collection.deleteOne(new Document("group", group));
        });
    }

    public static boolean userExists(String user) {
        Document document = MongoDB.getUserCollection().find(new Document("user", user)).first();
        return document != null;
    }

    public static boolean groupExists(String group) {
        Document document = MongoDB.getGroupCollection().find(new Document("group", group)).first();
        return document != null;
    }

    public static void setGroupChatPrefix(String group, String prefix) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("group", group);
            Document found = MongoDB.getGroupCollection().find(document).first();
            assert found != null;
            Bson newEntry = new Document("chatprefix", prefix);
            Bson newEntrySet = new Document("$set", newEntry);
            MongoDB.getGroupCollection().updateOne(found, newEntrySet);
            cachedGroups.remove(group);
            cachedGroups.put(group, new Group(group, prefix, document.getString("displayprefix"), document.getList("permissions", String.class), document.getList("parents", String.class)));
        });
    }

    public static void setGroupDisplayPrefix(String group, String prefix) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("group", group);
            Document found = MongoDB.getGroupCollection().find(document).first();
            assert found != null;
            Bson newEntry = new Document("displayprefix", prefix);
            Bson newEntrySet = new Document("$set", newEntry);
            MongoDB.getGroupCollection().updateOne(found, newEntrySet);
            cachedGroups.remove(group);
            cachedGroups.put(group, new Group(group, document.getString("chatprefix"), prefix, document.getList("permissions", String.class), document.getList("parents", String.class)));
        });
    }

    public static void addGroupPermission(String group, String permission) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("group", group);
            Document found = MongoDB.getGroupCollection().find(document).first();
            assert found != null;
            List<String> list = found.getList("permissions", String.class);
            list.add(permission);
            Bson newEntry = new Document("permissions", list);
            Bson newEntrySet = new Document("$set", newEntry);
            MongoDB.getGroupCollection().updateOne(new Document("group", group), newEntrySet);
            cachedGroups.remove(group);
            cachedGroups.put(group, new Group(group, found.getString("chatprefix"), found.getString("displayprefix"), list, found.getList("parents", String.class)));
        });
    }

    public static void removeGroupPermission(String group, String permission) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("group", group);
            Document found = MongoDB.getGroupCollection().find(document).first();
            assert found != null;
            List<String> list = found.getList("permissions", String.class);
            list.remove(permission);
            Bson newEntry = new Document("permissions", new ArrayList<>(list));
            Bson newEntrySet = new Document("$set", newEntry);
            MongoDB.getGroupCollection().updateOne(found, newEntrySet);
            cachedGroups.remove(group);
            cachedGroups.put(group, new Group(group, found.getString("chatprefix"), found.getString("displayprefix"), list, found.getList("parents", String.class)));
        });
    }

    public static void addGroupParent(String group, String parent) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("group", group);
            Document found = MongoDB.getGroupCollection().find(document).first();
            assert found != null;
            List<String> list = found.getList("parents", String.class);
            list.add(parent);
            Bson newEntry = new Document("parents", list);
            Bson newEntrySet = new Document("$set", newEntry);
            MongoDB.getGroupCollection().updateOne(new Document("group", group), newEntrySet);
            cachedGroups.remove(group);
            cachedGroups.put(group, new Group(group, found.getString("chatprefix"), found.getString("displayprefix"), found.getList("permissions", String.class), list));
        });
    }

    public static void removeGroupParent(String group, String parent) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("group", group);
            Document found = MongoDB.getGroupCollection().find(document).first();
            assert found != null;
            List<String> list = found.getList("parents", String.class);
            list.remove(parent);
            Bson newEntry = new Document("parents", list);
            Bson newEntrySet = new Document("$set", newEntry);
            MongoDB.getGroupCollection().updateOne(new Document("group", group), newEntrySet);
            cachedGroups.remove(group);
            cachedGroups.put(group, new Group(group, found.getString("chatprefix"), found.getString("displayprefix"), found.getList("permissions", String.class), list));
        });
    }

    public static void addUserPermission(String user, String permission) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("user", user);
            Document found = MongoDB.getUserCollection().find(document).first();
            assert found != null;
            List<String> list = found.getList("permissions", String.class);
            list.add(permission);
            Bson newEntry = new Document("permissions", list);
            Bson newEntrySet = new Document("$set", newEntry);
            MongoDB.getUserCollection().updateOne(new Document("user", user), newEntrySet);
            cachedPlayer.remove(user);
            cachedPlayer.put(user, new User(user, found.getString("group"), found.getLong("duration"), found.getList("permissions", String.class)));
        });
    }

    public static void removeUserPermission(String user, String permission) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("user", user);
            Document found = MongoDB.getUserCollection().find(document).first();
            assert found != null;
            List<String> list = found.getList("permissions", String.class);
            list.remove(permission);
            Bson newEntry = new Document("permissions", list);
            Bson newEntrySet = new Document("$set", newEntry);
            MongoDB.getUserCollection().updateOne(new Document("user", user), newEntrySet);
            cachedPlayer.remove(user);
            cachedPlayer.put(user, new User(user, found.getString("group"), found.getLong("duration"), found.getList("permissions", String.class)));
        });
    }

    public static boolean groupPermissionExists(String group, String permission) {
        Document document = MongoDB.getGroupCollection().find(new Document("group", group)).first();
        assert document != null;
        return document.getList("permissions", String.class).contains(permission);
    }

    public static boolean groupParentExists(String group, String parent) {
        Document document = MongoDB.getGroupCollection().find(new Document("group", group)).first();
        assert document != null;
        return document.getList("parents", String.class).contains(parent);
    }

    public static boolean userPermissionExists(String user, String permission) {
        Document document = MongoDB.getUserCollection().find(new Document("user", user)).first();
        assert document != null;
        return document.getList("permissions", String.class).contains(permission);
    }

    public static Group getGroup(String group) {
        Document document = MongoDB.getGroupCollection().find(new Document("group", group)).first();
        assert document != null;
        return new Group(group, document.getString("chatprefix"), document.getString("displayprefix"), document.getList("permissions", String.class), document.getList("parents", String.class));
    }

    public static User getUser(String user) {
        Document document = MongoDB.getUserCollection().find(new Document("user", user)).first();
        assert document != null;
        return new User(user, document.getString("group"), document.getLong("duration"), document.getList("permissions", String.class));
    }

    public static void setGroup(String user, String group, int time) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("user", user);
            Document found = MongoDB.getUserCollection().find(document).first();
            assert found != null;
            Bson newEntry = new Document("group", group);
            Bson newEntrySet = new Document("$set", newEntry);
            MongoDB.getUserCollection().updateOne(found, newEntrySet);
            Player player = Server.getInstance().getPlayer(user);
            setGroupUserTime(user, time);
            if (player != null) {
                cachedPlayer.remove(user);
                long end = 0;
                if (time != -1) end = System.currentTimeMillis() + time * 1000L;
                else end = -1;
                cachedPlayer.put(user, new User(user, group, end, found.getList("permissions", String.class)));
            }
        });
    }

    private static void setGroupUserTime(String user, int time) {
        if (time == -1) {
            Document document = new Document("user", user);
            Document found = MongoDB.getUserCollection().find(document).first();
            assert found != null;
            Bson newEntry = new Document("duration", (long) -1);
            Bson newEntrySet = new Document("$set", newEntry);
            MongoDB.getUserCollection().updateOne(found, newEntrySet);
            return;
        }
        long end = System.currentTimeMillis() + time * 1000L;
        Document document = new Document("user", user);
        Document found = MongoDB.getUserCollection().find(document).first();
        assert found != null;
        Bson newEntry = new Document("duration", end);
        Bson newEntrySet = new Document("$set", newEntry);
        MongoDB.getUserCollection().updateOne(found, newEntrySet);
    }

    public static void setDefaultGroup(String group) {
        defaultGroup = group;
    }

    public static String getDefaultGroup() {
        return defaultGroup;
    }
}
