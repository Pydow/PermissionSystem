package net.lldv.pydow.permissionsystem.components.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import cn.nukkit.player.Player;

public class SystemInteractEvent extends PlayerEvent {

    private final String executor;
    private final String command;
    private static final HandlerList handlers = new HandlerList();

    public SystemInteractEvent(Player player, String executor, String command) {
        super(player);
        this.executor = executor;
        this.command = command;
    }

    @Deprecated
    @Override
    public Player getPlayer() {
        return super.getPlayer();
    }

    public String getExecutor() {
        return executor;
    }

    public String getCommand() {
        return command;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
