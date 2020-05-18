package net.lldv.pydow.permissionsystem.components.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import cn.nukkit.player.Player;
import net.lldv.pydow.permissionsystem.components.data.Group;

public class PlayerGroupChangeEvent extends PlayerEvent {

    private final Group group;
    private final Group changedGroup;
    private static final HandlerList handlers = new HandlerList();

    public PlayerGroupChangeEvent(Player player, Group group, Group changedGroup) {
        super(player);
        this.group = group;
        this.changedGroup = changedGroup;
    }

    @Override
    public Player getPlayer() {
        return super.getPlayer();
    }

    public Group getGroup() {
        return group;
    }

    public Group getChangedGroup() {
        return changedGroup;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
