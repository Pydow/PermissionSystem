package net.lldv.pydow.permissionsystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.plugin.PluginManager;
import net.lldv.pydow.permissionsystem.PermissionSystem;
import net.lldv.pydow.permissionsystem.components.api.PermissionAPI;
import net.lldv.pydow.permissionsystem.components.event.SystemInteractEvent;
import net.lldv.pydow.permissionsystem.components.tools.Command;
import net.lldv.pydow.permissionsystem.components.tools.Language;

public class PermissionCommand extends PluginCommand<PermissionSystem> {

    PluginManager manager = getPlugin().getServer().getPluginManager();

    public PermissionCommand(PermissionSystem owner) {
        super(owner, Command.create("permission", "Verwalte die Permissions",
                new String[]{"pydow.permissionsystem.admin"},
                new String[]{"perms"}));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender.hasPermission(getPermissions().get(0))) {
            if (args.length == 2 || args.length >= 4) {
                if (args[0].equals("user")) {
                    String user = args[1];
                    if (!PermissionAPI.userExists(user)) {
                        sender.sendMessage(Language.getAndReplace("user-not-found", user));
                        return true;
                    }
                    if (args[2].equals("group")) {
                        if (args[3].equals("set")) {
                            String group = args[4];
                            if (PermissionAPI.groupExists(group)) {
                                PermissionAPI.setGroup(user, group, -1);
                                manager.callEvent(new SystemInteractEvent(null, sender.getName(), "user " + user + " group set " + group));
                                sender.sendMessage(Language.getAndReplace("user-group-set", user, group));
                            } else sender.sendMessage(Language.getAndReplace("group-not-found", group));
                        } else if (args[3].equals("settemp")) {
                            String group = args[4];
                            if (PermissionAPI.groupExists(group)) {
                                String timeString = args[5];
                                if (timeString.equals("hours") || timeString.equals("days")) {
                                    try {
                                        int time = Integer.parseInt(args[6]);
                                        int seconds = 0;
                                        if (timeString.equalsIgnoreCase("days")) seconds = time * 86400;
                                        if (timeString.equalsIgnoreCase("hours")) seconds = time * 3600;
                                        PermissionAPI.setGroup(user, group, seconds);
                                        manager.callEvent(new SystemInteractEvent(null, sender.getName(), "user " + user + " group settemp " + group + " " + timeString + " " + time));
                                    } catch (NumberFormatException e) {
                                        sender.sendMessage(Language.getAndReplace("invalid-time"));
                                    }
                                } else sender.sendMessage(Language.getAndReplace("need-time-format"));
                            } else sender.sendMessage(Language.getAndReplace("group-not-found", group));
                        } else sendUsage(sender);
                    } else if (args[2].equals("permission")) {
                        String permission = args[4];
                        if (args[3].equals("add")) {
                            if (!PermissionAPI.userPermissionExists(user, permission)) {
                                PermissionAPI.addUserPermission(user, permission);
                                manager.callEvent(new SystemInteractEvent(null, sender.getName(), "user " + user + " permission add " + permission));
                                sender.sendMessage(Language.getAndReplace("permission-user-set", user, permission));
                            } else sender.sendMessage(Language.getAndReplace("permission-already-exists"));
                        } else if (args[3].equals("remove")) {
                            if (PermissionAPI.userPermissionExists(user, permission)) {
                                PermissionAPI.removeUserPermission(user, permission);
                                manager.callEvent(new SystemInteractEvent(null, sender.getName(), "user " + user + " permission remove " + permission));
                                sender.sendMessage(Language.getAndReplace("permission-user-removed", user, permission));
                            } else sender.sendMessage(Language.getAndReplace("permission-not-found"));
                        } else sendUsage(sender);
                    } else sendUsage(sender);
                } else if (args[0].equals("group")) {
                    String group = args[1];
                    if (!PermissionAPI.groupExists(group)) {
                        sender.sendMessage(Language.getAndReplace("group-not-found", group));
                        return true;
                    }
                    if (args[2].equals("permission")) {
                        String permission = args[4];
                        if (args[3].equals("add")) {
                            if (!PermissionAPI.groupPermissionExists(group, permission)) {
                                PermissionAPI.addGroupPermission(group, permission);
                                manager.callEvent(new SystemInteractEvent(null, sender.getName(), "group " + group + " permission add " + permission));
                                sender.sendMessage(Language.getAndReplace("permission-group-set", group, permission));
                            } else sender.sendMessage(Language.getAndReplace("permission-already-exists"));
                        } else if (args[3].equals("remove")) {
                            if (PermissionAPI.groupPermissionExists(group, permission)) {
                                PermissionAPI.removeGroupPermission(group, permission);
                                manager.callEvent(new SystemInteractEvent(null, sender.getName(), "group " + group + " permission remove " + permission));
                                sender.sendMessage(Language.getAndReplace("permission-group-removed", group, permission));
                            } else sender.sendMessage(Language.getAndReplace("permission-not-found"));
                        } else sendUsage(sender);
                    } else if (args[2].equals("setchatprefix")) {
                        String prefix = "";
                        for (int i = 3; i < args.length; ++i) prefix = prefix + args[i] + " ";
                        PermissionAPI.setGroupChatPrefix(group, prefix);
                        manager.callEvent(new SystemInteractEvent(null, sender.getName(), "group " + group + " setchatprefix " + prefix));
                        sender.sendMessage(Language.getAndReplace("chatprefix-set", group));
                    } else if (args[2].equals("setdisplayprefix")) {
                        String prefix = "";
                        for (int i = 3; i < args.length; ++i) prefix = prefix + args[i] + " ";
                        PermissionAPI.setGroupDisplayPrefix(group, prefix);
                        manager.callEvent(new SystemInteractEvent(null, sender.getName(), "group " + group + " setdisplayprefix " + prefix));
                        sender.sendMessage(Language.getAndReplace("displayprefix-set", group));
                    } else if (args[2].equals("parent")) {
                        String parent = args[4];
                        if (!PermissionAPI.groupExists(parent)) {
                            sender.sendMessage(Language.getAndReplace("group-not-found"));
                            return true;
                        }
                        if (args[3].equals("add")) {
                            if (!PermissionAPI.groupParentExists(group, parent)) {
                                PermissionAPI.addGroupParent(group, parent);
                                manager.callEvent(new SystemInteractEvent(null, sender.getName(), "group " + group + " parent add " + parent));
                                sender.sendMessage(Language.getAndReplace("parent-group-set", group, parent));
                            } else sender.sendMessage(Language.getAndReplace("parent-already-exists"));
                        } else if (args[3].equals("remove")) {
                            if (PermissionAPI.groupParentExists(group, parent)) {
                                PermissionAPI.removeGroupParent(group, parent);
                                manager.callEvent(new SystemInteractEvent(null, sender.getName(), "group " + group + " parent remove " + parent));
                                sender.sendMessage(Language.getAndReplace("parent-group-removed", group, parent));
                            } else sender.sendMessage(Language.getAndReplace("parent-not-found"));
                        }
                    } else sendUsage(sender);
                } else if (args[0].equals("creategroup")) {
                    String group = args[1];
                    if (!PermissionAPI.groupExists(group)) {
                        PermissionAPI.createGroup(group);
                        manager.callEvent(new SystemInteractEvent(null, sender.getName(), "creategroup " + group));
                        sender.sendMessage(Language.getAndReplace("group-created", group));
                        sender.sendMessage(Language.getAndReplace("group-created-info"));
                    } else sender.sendMessage(Language.getAndReplace("group-already-exists"));
                } else if (args[0].equals("removegroup")) {
                    String group = args[1];
                    if (PermissionAPI.groupExists(group)) {
                        PermissionAPI.removeGroup(group);
                        manager.callEvent(new SystemInteractEvent(null, sender.getName(), "removegroup " + group));
                        sender.sendMessage(Language.getAndReplace("group-removed", group));
                    } else sender.sendMessage(Language.getAndReplace("group-not-found", group));
                } else sendUsage(sender);
            } else sendUsage(sender);
        } else sender.sendMessage(Language.getAndReplace("no-permission"));
        return true;
    }

    private void sendUsage(CommandSender sender) {
        String Prefix = Language.prefix;
        sender.sendMessage(Prefix + "/perms creategroup <Gruppe>");
        sender.sendMessage(Prefix + "/perms removegroup <Gruppe>");
        sender.sendMessage(Prefix + "/perms group <Gruppe> permission add <Permission>");
        sender.sendMessage(Prefix + "/perms group <Gruppe> permission remove <Permission>");
        sender.sendMessage(Prefix + "/perms group <Gruppe> setchatprefix <Prefix>");
        sender.sendMessage(Prefix + "/perms group <Gruppe> setdisplayprefix <Prefix>");
        sender.sendMessage(Prefix + "/perms group <Gruppe> parent add <Gruppe>");
        sender.sendMessage(Prefix + "/perms group <Gruppe> parent remove <Gruppe>");
        sender.sendMessage(Prefix + "/perms user <Spieler> group set <Gruppe>");
        sender.sendMessage(Prefix + "/perms user <Spieler> group settemp <Group> <days|hours> <Zeit>");
        sender.sendMessage(Prefix + "/perms user <Spieler> permission add <Permission>");
        sender.sendMessage(Prefix + "/perms user <Spieler> permission remove <Permission>");
    }
}
