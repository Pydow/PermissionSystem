package net.lldv.pydow.permissionsystem.listeners;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.player.Player;
import net.lldv.pydow.permissionsystem.PermissionSystem;
import net.lldv.pydow.permissionsystem.components.api.PermissionAPI;
import net.lldv.pydow.permissionsystem.components.data.Group;
import net.lldv.pydow.permissionsystem.components.data.User;

import java.util.concurrent.CompletableFuture;

public class EventListener implements Listener {

    @EventHandler
    public void on(PlayerJoinEvent event) {
        CompletableFuture.runAsync(() -> {
            Player player = event.getPlayer();
            player.getEffectivePermissions().clear();
            if (!PermissionAPI.userExists(player.getName())) PermissionAPI.createUserData(player.getName());
            User user = PermissionAPI.getUser(player.getName());
            PermissionAPI.cachedPlayer.put(player.getName(), user);
            Group group = PermissionAPI.cachedGroups.get(user.getGroup());
            if (group == null) PermissionAPI.setGroup(user.getUser(), PermissionAPI.getDefaultGroup(), -1);
            if (group.getPermissions() != null) group.getPermissions().forEach(permission -> player.addAttachment(PermissionSystem.getInstance(), permission, true));
            if (user.getPermissions() != null) user.getPermissions().forEach(permission -> player.addAttachment(PermissionSystem.getInstance(), permission, true));
            player.setNameTag(group.getDisplayPrefix().replace("%p", player.getName()));
            player.setDisplayName(group.getDisplayPrefix().replace("%p", player.getName()));
            if (group.getGroupParents().size() >= 1) {
                group.getGroupParents().forEach(groupList -> {
                    Group parentGroup = PermissionAPI.cachedGroups.get(groupList);
                    if (parentGroup == null) {
                        PermissionAPI.removeGroupParent(group.getGroup(), groupList);
                        return;
                    }
                    parentGroup.getPermissions().forEach(permission -> player.addAttachment(PermissionSystem.getInstance(), permission, true));
                });
            }
        });
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        PermissionAPI.cachedPlayer.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void on(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        User user = PermissionAPI.cachedPlayer.get(player.getName());
        Group group = PermissionAPI.cachedGroups.get(user.getGroup());
        event.setFormat(group.getPrefix().replace("%p", player.getName()).replace("%m", message));
    }
}
