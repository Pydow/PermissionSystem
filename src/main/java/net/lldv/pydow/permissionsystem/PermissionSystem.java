package net.lldv.pydow.permissionsystem;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.registry.CommandRegistry;
import net.lldv.pydow.permissionsystem.commands.PermissionCommand;
import net.lldv.pydow.permissionsystem.components.api.database.MongoDB;
import net.lldv.pydow.permissionsystem.components.api.PermissionAPI;
import net.lldv.pydow.permissionsystem.components.tasks.CheckTask;
import net.lldv.pydow.permissionsystem.components.tools.Language;
import net.lldv.pydow.permissionsystem.listeners.EventListener;

public class PermissionSystem extends PluginBase {

    private static PermissionSystem instance;

    @Override
    public void onLoad() {
        instance = this;
        registerCommands();
    }

    @Override
    public void onEnable() {
        Language.init();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        MongoDB.connect(this);
        PermissionAPI.setDefaultGroup(getConfig().getString("DefaultGroup"));
        getServer().getScheduler().scheduleDelayedRepeatingTask(this, new CheckTask(), 200, 200, true);
    }

    private void registerCommands() {
        CommandRegistry registry = getServer().getCommandRegistry();
        registry.register(this, new PermissionCommand(this));
    }

    @Override
    public void onDisable() {
        MongoDB.getMongoClient().close();
    }

    public static PermissionSystem getInstance() {
        return instance;
    }
}
