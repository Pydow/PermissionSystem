package net.lldv.pydow.permissionsystem.components.api.database;

import cn.nukkit.utils.Config;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.lldv.pydow.permissionsystem.PermissionSystem;
import net.lldv.pydow.permissionsystem.components.api.PermissionAPI;
import org.bson.Document;

import java.util.concurrent.CompletableFuture;

public class MongoDB {

    private static Config config = PermissionSystem.getInstance().getConfig();

    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    private static MongoCollection<Document> groupCollection, userCollection;

    public static void connect(PermissionSystem server) {
        CompletableFuture.runAsync(() -> {
            MongoClientURI uri = new MongoClientURI(config.getString("MongoDB.Uri"));
            mongoClient = new MongoClient(uri);
            mongoDatabase = mongoClient.getDatabase(config.getString("MongoDB.Database"));
            groupCollection = mongoDatabase.getCollection("groups");
            userCollection = mongoDatabase.getCollection("users");
            server.getLogger().info("[MongoClient] Connection opened.");
            if (!PermissionAPI.groupExists(PermissionAPI.getDefaultGroup())) PermissionAPI.createGroup(PermissionAPI.getDefaultGroup());
            for (Document document : groupCollection.find()) {
                PermissionAPI.cachedGroups.put(document.getString("group"), PermissionAPI.getGroup(document.getString("group")));
            }
        });
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public static MongoCollection<Document> getGroupCollection() {
        return groupCollection;
    }

    public static MongoCollection<Document> getUserCollection() {
        return userCollection;
    }

    public static MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }
}
