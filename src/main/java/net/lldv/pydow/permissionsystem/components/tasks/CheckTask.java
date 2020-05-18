package net.lldv.pydow.permissionsystem.components.tasks;

import cn.nukkit.Server;
import cn.nukkit.player.Player;
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
            }
        }
    }
}
