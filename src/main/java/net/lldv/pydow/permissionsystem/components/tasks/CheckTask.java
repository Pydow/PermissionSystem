package net.lldv.pydow.permissionsystem.components.tasks;

import cn.nukkit.Server;
import cn.nukkit.player.Player;
import net.lldv.pydow.permissionsystem.PermissionSystem;
import net.lldv.pydow.permissionsystem.components.api.PermissionAPI;
import net.lldv.pydow.permissionsystem.components.data.Group;
import net.lldv.pydow.permissionsystem.components.data.User;

public class CheckTask extends Thread {

    @Override
    public void run() {
        if (!Server.getInstance().getOnlinePlayers().isEmpty()) {
            for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                User user = PermissionAPI.cachedPlayer.get(player.getName());
                Group group = PermissionAPI.cachedGroups.get(user.getGroup());
                if (user.getDuration() != -1) {
                    if (user.getDuration() < System.currentTimeMillis()) PermissionAPI.setGroup(user.getUser(), PermissionAPI.getDefaultGroup(), -1);
                }
                if (group == null) PermissionAPI.setGroup(user.getUser(), PermissionAPI.getDefaultGroup(), -1);
                player.getEffectivePermissions().clear();
                if (group.getPermissions() != null) group.getPermissions().forEach(permission -> player.addAttachment(PermissionSystem.getInstance(), permission, true));
                if (user.getPermissions() != null) user.getPermissions().forEach(permission -> player.addAttachment(PermissionSystem.getInstance(), permission, true));
                /*group.getPermissions().forEach(permissions -> {
                    if (player.hasPermission(permissions) && !group.getPermissions().contains(permissions)) {
                        player.addAttachment(PermissionSystem.getInstance(), permissions, false);
                    }
                });
                user.getPermissions().forEach(permissions -> {
                    if (player.hasPermission(permissions) && !user.getPermissions().contains(permissions)) {
                        player.addAttachment(PermissionSystem.getInstance(), permissions, false);
                    }
                });*/
                //Todo: Die Permissions entfernen, die nicht mehr in der aktuellen Gruppe vorhanden sind, aber dem Spieler aus einer anderen Gruppe noch gesetzt sin.
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
            }
        }
    }
}
