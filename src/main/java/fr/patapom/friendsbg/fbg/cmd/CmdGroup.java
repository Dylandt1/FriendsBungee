package fr.patapom.friendsbg.fbg.cmd;

import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.patapom.friendsbg.common.players.ProfileManager;
import fr.patapom.friendsbg.common.players.ProfileProvider;
import fr.patapom.friendsbg.common.groups.GroupManager;
import fr.patapom.friendsbg.common.groups.GroupProvider;
import fr.patapom.friendsbg.fbg.cmd.utils.Help;
import fr.tmmods.tmapi.exceptions.ManagerNotFoundException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

import java.util.*;

/**
 * This file is part of FriendsBungee, a BungeeCord friends plugin system.
 *
 * FriendsBungee is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FriendsBungee is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

public class CmdGroup extends Command implements TabExecutor
{
    private static Map<UUID, UUID> requestGp = new HashMap<>();

    private final Help H = new Help();

    private final String prefix;
    private final String suffix;
    private final String cmdNotUsable;
    private final String playerNotFound;
    private final String errorAdd;
    private final String noRequest;
    private final String alreadyInGroup;
    private final String groupCreated;
    private final String groupDeletedSender;
    private final String groupDeletedTarget;
    private final String newGroupSender;
    private final String newGroupTarget;
    private final String refuseGroupSender;
    private final String refuseGroupTarget;
    private final String noPlayersInGroup;
    private final String notInGroup;
    private final String ownerGroup;
    private final String requestSender;
    private final String requestTarget;
    private final String scrollTargetRequest;
    private final String groupLimit;
    private final String yourselfCantGetOut;
    private final String targetNotInGroup;
    private final String targetDeleted;
    private final String alreadyOwner;
    private final String justMemberToOwner;
    private final String newOwnerAllInGroup;
    private final String newOwnerTarget;
    private final String quitGroupSender;
    private final String quitGroupPlayers;
    private final String tpEnabled;
    private final String tpDisabled;
    private final String tpAlreadyEnabled;
    private final String tpAlreadyDisabled;
    private final String targetRequestsDeny;
    private final String targetIsInGroup;
    private final String requestsAllow;
    private final String requestsAlreadyEnabled;
    private final String requestsDeny;
    private final String requestsAlreadyDisabled;
    private final String msgGroupList;

    // Private chat constants
    private final String mpPrefix;
    private final String mpSuffix;
    private final String sPrefix;
    private final String sSuffix;
    private final String tPrefix;
    private final String tSuffix;
    private final String msgColor;
    private final String tooLong;

    public CmdGroup()
    {
        super("group", null, FriendsBG.getInstance().getConfig().getStringList("groups.cmdAlias").toArray(new String[0]));
        Configuration msgConfig = FriendsBG.getInstance().getMsgConfig();
        this.prefix = msgConfig.getString("groups.prefix").replace("&", "§");
        this.suffix = msgConfig.getString("groups.suffix").replace("&", "§");
        this.cmdNotUsable = msgConfig.getString("groups.cmdNotUsable").replace("&", "§");
        this.playerNotFound = msgConfig.getString("groups.playerNotFound").replace("&", "§");
        this.errorAdd = msgConfig.getString("groups.errorAdd").replace("&", "§");
        this.noRequest = msgConfig.getString("groups.noRequest").replace("&", "§");
        this.alreadyInGroup = msgConfig.getString("groups.alreadyInGroup").replace("&", "§");
        this.groupCreated = msgConfig.getString("groups.groupCreated").replace("&", "§");
        this.groupDeletedSender = msgConfig.getString("groups.groupDeletedSender").replace("&", "§");
        this.groupDeletedTarget = msgConfig.getString("groups.groupDeletedTarget").replace("&", "§");
        this.newGroupSender = msgConfig.getString("groups.newGroupSender").replace("&", "§");
        this.newGroupTarget = msgConfig.getString("groups.newGroupTarget").replace("&", "§");
        this.refuseGroupSender = msgConfig.getString("groups.refuseGroupSender").replace("&", "§");
        this.refuseGroupTarget = msgConfig.getString("groups.refuseGroupTarget").replace("&", "§");
        this.noPlayersInGroup = msgConfig.getString("groups.noPlayersInGroup").replace("&", "§");
        this.notInGroup = msgConfig.getString("groups.notInGroup").replace("&", "§");
        this.ownerGroup = msgConfig.getString("groups.ownerGroup").replace("&", "§");
        this.requestSender = msgConfig.getString("groups.requestSender").replace("&", "§");
        this.requestTarget = msgConfig.getString("groups.requestTarget").replace("&", "§");
        this.scrollTargetRequest = msgConfig.getString("groups.scrollTargetRequest").replace("&", "§");
        this.groupLimit = msgConfig.getString("groups.groupLimit").replace("&", "§");
        this.yourselfCantGetOut = msgConfig.getString("groups.yourselfCantGetOut").replace("&", "§");
        this.targetNotInGroup = msgConfig.getString("groups.targetNotInGroup").replace("&", "§");
        this.targetDeleted = msgConfig.getString("groups.targetDeleted").replace("&", "§");
        this.alreadyOwner = msgConfig.getString("groups.alreadyOwner").replace("&", "§");
        this.justMemberToOwner = msgConfig.getString("groups.justMemberToOwner").replace("&", "§");
        this.newOwnerAllInGroup = msgConfig.getString("groups.newOwnerAllInGroup").replace("&", "§");
        this.newOwnerTarget = msgConfig.getString("groups.newOwnerTarget").replace("&", "§");
        this.quitGroupSender = msgConfig.getString("groups.quitGroupSender").replace("&", "§");
        this.quitGroupPlayers = msgConfig.getString("groups.quitGroupPlayers").replace("&", "§");
        this.tpEnabled = msgConfig.getString("groups.tpEnabled").replace("&", "§");
        this.tpDisabled = msgConfig.getString("groups.tpDisabled").replace("&", "§");
        this.tpAlreadyEnabled = msgConfig.getString("groups.tpAlreadyEnabled").replace("&", "§");
        this.tpAlreadyDisabled = msgConfig.getString("groups.tpAlreadyDisabled").replace("&", "§");
        this.targetRequestsDeny = msgConfig.getString("groups.targetRequestsDeny").replace("&", "§");
        this.targetIsInGroup = msgConfig.getString("groups.targetIsInGroup").replace("&", "§");
        this.requestsAllow = msgConfig.getString("groups.requestsAllow").replace("&", "§");
        this.requestsAlreadyEnabled = msgConfig.getString("groups.requestsAlreadyEnabled").replace("&", "§");
        this.requestsDeny = msgConfig.getString("groups.requestsDeny").replace("&", "§");
        this.requestsAlreadyDisabled = msgConfig.getString("groups.requestsAlreadyDisabled").replace("&", "§");
        this.msgGroupList = msgConfig.getString("groups.msgGroupList").replace("&", "§");

        // Set private chat constants
        this.mpPrefix = msgConfig.getString("groups.msg.prefix").replace("&", "§");
        this.mpSuffix = msgConfig.getString("groups.msg.suffix").replace("&", "§");
        this.sPrefix = msgConfig.getString("groups.msg.sender.prefix").replace("&", "§");
        this.sSuffix = msgConfig.getString("groups.msg.sender.suffix").replace("&", "§");
        this.tPrefix = msgConfig.getString("groups.msg.target.prefix").replace("&", "§");
        this.tSuffix = msgConfig.getString("groups.msg.target.suffix").replace("&", "§");
        this.msgColor = msgConfig.getString("groups.msg.messageColor").replace("&", "§");
        this.tooLong = msgConfig.getString("groups.msg.tooLong").replace("&", "§");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args)
    {
        if(!(sender instanceof ProxiedPlayer p)) {return null;}

        ProfileProvider profileProvider = new ProfileProvider(p.getUniqueId());
        ProfileManager profile;
        try {
            profile = profileProvider.getPManager();
        } catch (ManagerNotFoundException e) {
            throw new RuntimeException(e);
        }

        GroupProvider groupProvider;

        if(args.length == 1)
        {
            List<String> list = new ArrayList<>();
            list.add("help");
            list.add("enable");
            list.add("disable");

            if(profile.isInGroup())
            {
                groupProvider = new GroupProvider(profile.getGroupId());
                if(groupProvider.gExist())
                {
                    list.add("mp");
                    list.add("list");
                    list.add("quit");

                    GroupManager group;
                    try {
                        group = groupProvider.getGManager();
                    } catch (ManagerNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    if(group.isOwner(p.getUniqueId()))
                    {
                        list.add("delete");
                        list.add("add");
                        list.add("remove");
                        list.add("tp");
                        list.add("owner");
                    }
                }
                return list;
            }

            list.add("create");
            list.add("accept");
            list.add("refuse");

            return list;
        }else if(args.length == 2)
        {
            if(profile.isInGroup())
            {
                List<String> list = new ArrayList<>();
                groupProvider = new GroupProvider(profile.getGroupId());

                if(args[0].equalsIgnoreCase("mp"))
                {
                    list.add("<message>");
                }

                if(!groupProvider.gExist())
                {
                    profile.setGroupId(null);
                    profileProvider.save(profile);
                    return list;
                }

                if(groupProvider.gExist())
                {
                    GroupManager group;
                    try {
                        group = groupProvider.getGManager();
                    } catch (ManagerNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    if(group.isOwner(p.getUniqueId()))
                    {
                        if(args[0].equalsIgnoreCase("add"))
                        {
                            for(ProxiedPlayer pls : ProxyServer.getInstance().getPlayers())
                            {
                                list.add(pls.getName());
                            }
                            return list;
                        }else if(args[0].equalsIgnoreCase("remove"))
                        {
                            if (profile.isInGroup())
                            {
                                for (ProxiedPlayer pls : group.getPlayersInGroup())
                                {
                                    list.add(pls.getName());
                                }
                                return list;
                            }
                        }else if(args[0].equalsIgnoreCase("tp"))
                        {
                            list.add("enable");
                            list.add("disable");
                            return list;
                        }else if(args[0].equalsIgnoreCase("owner"))
                        {
                            if (profile.isInGroup())
                            {
                                for (ProxiedPlayer pls : group.getPlayersInGroup())
                                {
                                    list.add(pls.getName());
                                }
                                return list;
                            }
                        }
                    }
                }
            }
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if (!(sender instanceof ProxiedPlayer p))
        {
            sender.sendMessage(new TextComponent(prefix+suffix+cmdNotUsable));
            return;
        }

        ProfileProvider profileProvider = new ProfileProvider(p.getUniqueId());
        ProfileManager profile;
        try {
            profile = profileProvider.getPManager();
        } catch (ManagerNotFoundException e)
        {
            e.printStackTrace();
            return;
        }

        if (args.length == 0)
        {
            H.helpGroup(p);
        }else if (args.length == 1)
        {
            if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h"))
            {
                H.helpGroup(p);
            }else if(args[0].equalsIgnoreCase("create"))
            {
                if(!profile.isInGroup())
                {
                    GroupProvider groupProvider = new GroupProvider(p);
                    GroupManager group;
                    try {
                        group = groupProvider.getGManager();
                    } catch (ManagerNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    profile.setGroupId(group.getGroupId());
                    profileProvider.save(profile);
                    sendMessage(p, prefix+suffix+groupCreated);
                }else {
                    sendMessage(p, prefix+suffix+alreadyInGroup);
                }
            }else if (args[0].equalsIgnoreCase("enable"))
            {
                if (profile.gpAllow()) {sendMessage(p, prefix+suffix+requestsAlreadyEnabled);return;}

                profile.setGpAllow(true);
                profileProvider.save(profile);
                sendMessage(p, prefix+suffix+requestsAllow);

            }else if (args[0].equalsIgnoreCase("disable"))
            {
                if (!profile.gpAllow()) {sendMessage(p, prefix+suffix+requestsAlreadyDisabled);return;}

                profile.setGpAllow(false);
                profileProvider.save(profile);
                sendMessage(p, prefix+suffix+requestsDeny);

            }else if (args[0].equalsIgnoreCase("accept"))
            {
                if (!requestGp.containsKey(p.getUniqueId())) {
                    sendMessage(p, prefix+suffix+noRequest);
                    return;}

                if (requestGp.get(p.getUniqueId()) == null) {
                    sendMessage(p, prefix+suffix+errorAdd);
                    requestGp.remove(p.getUniqueId());
                    return;}

                if(profile.isInGroup()) {
                    sendMessage(p, prefix+suffix+alreadyInGroup);
                    return;}

                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(requestGp.get(p.getUniqueId()));
                ProfileProvider targetProvider = new ProfileProvider(target.getUniqueId());
                ProfileManager targetProfile;
                try {
                    targetProfile = targetProvider.getPManager();
                } catch (ManagerNotFoundException e) {
                    throw new RuntimeException(e);
                }
                GroupProvider groupProvider = new GroupProvider(targetProfile.getGroupId());
                GroupManager group;
                try {
                    group = groupProvider.getGManager();
                } catch (ManagerNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if(group == null)
                {
                    sendMessage(p, prefix+suffix+errorAdd);
                    requestGp.remove(p.getUniqueId());
                    return;
                }
                sendMessage(p, prefix+suffix+newGroupTarget.replace("%player%", target.getName()));

                sendMessage(target, prefix+suffix+newGroupSender.replace("%targetPlayer%", p.getName()));

                requestGp.remove(p.getUniqueId());
                group.addPlayerInGroup(p);
                groupProvider.save(group);
            }else if(args[0].equalsIgnoreCase("quit"))
            {
                if(profile.isInGroup())
                {
                    GroupProvider groupProvider = new GroupProvider(profile.getGroupId());

                    if(!groupProvider.gExist())
                    {
                        profile.setGroupId(null);
                        profileProvider.save(profile);
                        sendMessage(p, prefix+suffix+notInGroup);
                        return;
                    }

                    GroupManager group;
                    try {
                        group = groupProvider.getGManager();
                    } catch (ManagerNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    if(group.isOwner(p.getUniqueId())) {group.onQuit();return;}

                    group.removePlayerInGroup(p);
                    groupProvider.save(group);
                    sendMessage(p, prefix+suffix+quitGroupSender);
                    for(ProxiedPlayer pls : group.getPlayersInGroup())
                    {
                        sendMessage(pls, prefix+suffix+quitGroupPlayers.replace("%player%", p.getName()));
                    }
                }else {
                    sendMessage(p, prefix+suffix+notInGroup);
                }
            }else if (args[0].equalsIgnoreCase("refuse"))
            {
                if (!requestGp.containsKey(p.getUniqueId()))
                {
                    sendMessage(p, prefix+suffix+noRequest);
                    return;
                }
                if (requestGp.get(p.getUniqueId()) == null)
                {
                    sendMessage(p, prefix+suffix+errorAdd);
                    requestGp.remove(p.getUniqueId());
                    return;
                }

                ProxiedPlayer pl = ProxyServer.getInstance().getPlayer(requestGp.get(p.getUniqueId()));

                sendMessage(p, prefix+suffix+refuseGroupSender.replace("%targetPlayer%", pl.getName()));

                sendMessage(pl, prefix+suffix+refuseGroupTarget.replace("%player%", p.getName()));

                requestGp.remove(p.getUniqueId());
            } else if(args[0].equalsIgnoreCase("list"))
            {
                if(profile.isInGroup())
                {
                    GroupProvider groupProvider = new GroupProvider(profile.getGroupId());

                    if(!groupProvider.gExist())
                    {
                        profile.setGroupId(null);
                        profileProvider.save(profile);
                        sendMessage(p, prefix+suffix+notInGroup);
                        return;
                    }

                    GroupManager group;
                    try {
                        group = groupProvider.getGManager();
                    } catch (ManagerNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    if (group.getPlayersInGroup().size() == 0 )
                    {
                        sendMessage(p, prefix+suffix+noPlayersInGroup);
                    }else {
                        List<String> membersOnline = new ArrayList<>();
                        for (ProxiedPlayer playersInGroup : group.getPlayersInGroup())
                        {
                            membersOnline.add(playersInGroup.getName());
                        }

                        int i = group.getGroupLenght();

                        sendMessage(p, " ");
                        sendMessage(p, "§6#§f-------------------- §2Group §f(§3"+i+"§f) --------------------§6#");
                        sendMessage(p, " ");

                        StringBuilder colorPath = new StringBuilder();

                        for (String s : membersOnline) {
                            colorPath.append("§b").append(s).append("§c, ");
                        }

                        sendMessage(p, msgGroupList.replace("%nbMembers%", String.valueOf(membersOnline.size())));
                        sendMessage(p, colorPath.substring(0, colorPath.length() - 2));
                        sendMessage(p, " ");
                        sendMessage(p, "§6#§f---------------------------------------------------§6#");
                    }
                }else {
                    sendMessage(p, prefix+suffix+notInGroup);
                }
            }else if(args[0].equalsIgnoreCase("delete"))
            {
                if(profile.isInGroup())
                {
                    GroupProvider groupProvider = new GroupProvider(profile.getGroupId());

                    if(!groupProvider.gExist())
                    {
                        profile.setGroupId(null);
                        profileProvider.save(profile);
                        sendMessage(p, prefix+suffix+notInGroup);
                        return;
                    }

                    try {
                        GroupManager group = groupProvider.getGManager();

                        if(group.isOwner(p.getUniqueId()))
                        {
                            sendMessage(p, prefix+suffix+groupDeletedSender);
                            group.removePlayerInGroup(p);

                            for(ProxiedPlayer pls : group.getPlayersInGroup())
                            {
                                sendMessage(pls, prefix+suffix+groupDeletedTarget);
                                group.removePlayerInGroup(pls);
                            }

                            group.delete();
                        }else {
                            sendMessage(p, prefix+suffix+ownerGroup);
                        }
                    } catch (ManagerNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    sendMessage(p, prefix+suffix+notInGroup);
                }
            }else {
                H.helpGroup(p);
            }
        }else if (args.length >= 2)
        {
            if(!profile.isInGroup()) {sendMessage(p, prefix+suffix+notInGroup);return;}

            GroupProvider groupProvider = new GroupProvider(profile.getGroupId());

            if(!groupProvider.gExist())
            {
                profile.setGroupId(null);
                profileProvider.save(profile);
                sendMessage(p, prefix+suffix+notInGroup);
                return;
            }

            GroupManager group;
            try {
                group = groupProvider.getGManager();
            } catch (ManagerNotFoundException e) {
                throw new RuntimeException(e);
            }

            final String targetName = args[1];
            final String notFound = playerNotFound.replace("%targetPlayer%", targetName);

            if(args[0].equalsIgnoreCase("add"))
            {
                if (ProxyServer.getInstance().getPlayer(targetName) == null)
                {
                    sendMessage(p, prefix+suffix+notFound);
                    return;
                }

                ProxiedPlayer targetPl = ProxyServer.getInstance().getPlayer(targetName);

                if(!group.isOwner(p.getUniqueId())) {sendMessage(p, prefix+suffix+ownerGroup);return;}
                if (targetPl == p) {sendMessage(p, prefix+suffix+alreadyInGroup);return;}
                if (group.getPlayersInGroup().size() == group.getGroupLenght())
                {
                    sendMessage(p, prefix+suffix+groupLimit.replace("%groupSize%", String.valueOf(group.getGroupLenght())));
                    return;
                }

                ProfileProvider targetProvider = new ProfileProvider(targetPl.getUniqueId());
                ProfileManager targetProfile;
                try {
                    targetProfile = targetProvider.getPManager();
                } catch (ManagerNotFoundException e) {
                    throw new RuntimeException(e);
                }

                if(!targetProfile.gpAllow()) {sendMessage(p, prefix+suffix+targetRequestsDeny.replace("%targetPlayer%", targetName));return;}
                if(targetProfile.isInGroup()) {sendMessage(p, prefix+suffix+targetIsInGroup.replace("%targetPlayer%", targetName));return;}

                TextComponent targetTxt = new TextComponent(prefix+suffix+requestTarget.replace("%player%", p.getName()));
                targetTxt.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(scrollTargetRequest).create()));
                targetTxt.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gp accept"));

                sendMessage(p, prefix+suffix+requestSender.replace("%targetPlayer%", targetName));
                targetPl.sendMessage(targetTxt);

                requestGp.put(targetPl.getUniqueId(), p.getUniqueId());
                return;
            }else if (args[0].equalsIgnoreCase("remove"))
            {
                if (ProxyServer.getInstance().getPlayer(targetName) == null)
                {
                    sendMessage(p, prefix+suffix+notFound);
                    return;
                }

                ProxiedPlayer targetPl = ProxyServer.getInstance().getPlayer(targetName);

                if(!group.isOwner(p.getUniqueId())) {sendMessage(p, prefix+suffix+ownerGroup);return;}
                if (targetPl == p) {sendMessage(p, prefix+suffix+yourselfCantGetOut);return;}
                if (!group.getPlayersInGroup().contains(targetPl))
                {
                    sendMessage(p, prefix+suffix+targetNotInGroup.replace("%targetPlayer%", targetName));
                    return;
                }

                group.removePlayerInGroup(targetPl);
                sendMessage(p, prefix+suffix+targetDeleted.replace("%targetPlayer%", targetName));
                return;
            }else if (args[0].equalsIgnoreCase("owner"))
            {
                if (ProxyServer.getInstance().getPlayer(targetName) == null)
                {
                    sendMessage(p, prefix+suffix+notFound);
                    return;
                }

                ProxiedPlayer targetPl = ProxyServer.getInstance().getPlayer(targetName);

                if(!group.isOwner(p.getUniqueId())) {sendMessage(p, prefix+suffix+ownerGroup);return;}
                if (targetPl == p) {sendMessage(p, prefix+suffix+alreadyOwner);return;}
                if (!group.getPlayersInGroup().contains(targetPl)) {
                    sendMessage(p, prefix+suffix+justMemberToOwner);return;
                }

                for (ProxiedPlayer players : group.getPlayersInGroup())
                {
                    if (players != targetPl)
                    {
                        sendMessage(p, prefix+suffix+newOwnerAllInGroup.replace("%targetPlayer%", targetName));
                    }
                }
                sendMessage(targetPl, prefix+suffix+newOwnerTarget);
                group.changeOwnerGroup(targetPl);
                return;
            }else if(args[0].equalsIgnoreCase("tp"))
            {
                if(!group.isOwner(p.getUniqueId())) {sendMessage(p, prefix+suffix+ownerGroup);return;}

                if(args[1].equalsIgnoreCase("enable"))
                {
                    if(!group.tp())
                    {
                        group.setTp(true);
                        sendMessage(p, prefix+suffix+tpEnabled);
                        return;
                    }
                    sendMessage(p, prefix+suffix+tpAlreadyEnabled);
                    return;
                }else if(args[1].equalsIgnoreCase("disable"))
                {
                    if(group.tp())
                    {
                        group.setTp(false);
                        sendMessage(p, prefix+suffix+tpDisabled);
                        return;
                    }
                    sendMessage(p, prefix+suffix+tpAlreadyDisabled);
                    return;
                }else {
                    H.helpGroup(p);
                }
                return;
            }if(args[0].equalsIgnoreCase("mp"))
            {
                if(profile.isInGroup())
                {
                    StringBuilder msg = new StringBuilder();

                    if(args.length > msg.capacity()) {sendMessage(p, tooLong);return;}

                    for(int i = 1; i != args.length; i++)
                    {
                        msg.append(args[i].replace("&", "§")).append(" ");
                    }

                    final String part1 = mpPrefix+mpSuffix;
                    final String part2 = sPrefix+sSuffix;

                    sendMessage(p, part1+part2+msgColor+msg);

                    for(ProxiedPlayer pl : group.getPlayersInGroup())
                    {
                        if(!pl.equals(p)) {sendMessage(pl, part1+tPrefix.replace("%player%", p.getName())+tSuffix+msgColor+msg);}
                    }
                }else {
                    profileProvider.save(profile);
                    sendMessage(p, prefix+suffix+notInGroup);
                }
            }else {
                H.helpGroup(p);
            }
        }else {
            H.helpGroup(p);
        }
    }

    private void sendMessage(ProxiedPlayer p, String s) {
        p.sendMessage(new TextComponent(s));
    }
}
