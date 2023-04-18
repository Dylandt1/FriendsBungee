package fr.patapom.fbg.cmd;

import fr.patapom.fbg.FriendsBG;
import fr.patapom.commons.friends.FriendsManager;
import fr.patapom.commons.friends.FriendsProvider;
import fr.patapom.commons.party.PartyManager;
import fr.patapom.commons.party.PartyProvider;
import fr.patapom.fbg.cmd.utils.Help;
import fr.patapom.tmapi.exceptions.ManagerNotFoundException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

import java.util.*;

/**
 * This file is part of FriendsBungee (FriendsBG-Free), a bungeecord friends plugin.
 *
 * FriendsBungee (FriendsBG-Free) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FriendsBungee (FriendsBG-Free) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

public class CmdParty extends Command implements TabExecutor
{
    private static Map<UUID, UUID> requestGp = new HashMap<>();

    private final Help H = new Help();

    private final Configuration config;
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

    public CmdParty()
    {
        super("group", null, new String[] { "party", "partie", "groupe", "gp", "g", "p" });
        this.config = FriendsBG.getInstance().getConfig();
        this.prefix = config.getString("groups.prefix").replace("&", "§");
        this.suffix = config.getString("groups.suffix").replace("&", "§");
        this.cmdNotUsable = config.getString("groups.cmdNotUsable").replace("&", "§");
        this.playerNotFound = config.getString("groups.playerNotFound").replace("&", "§");
        this.errorAdd = config.getString("groups.errorAdd").replace("&", "§");
        this.noRequest = config.getString("groups.noRequest").replace("&", "§");
        this.alreadyInGroup = config.getString("groups.alreadyInGroup").replace("&", "§");
        this.groupCreated = config.getString("groups.groupCreated").replace("&", "§");
        this.groupDeletedSender = config.getString("groups.groupDeletedSender").replace("&", "§");
        this.groupDeletedTarget = config.getString("groups.groupDeletedTargets").replace("&", "§");
        this.newGroupSender = config.getString("groups.newGroupSender").replace("&", "§");
        this.newGroupTarget = config.getString("groups.newGroupTarget").replace("&", "§");
        this.refuseGroupSender = config.getString("groups.refuseGroupSender").replace("&", "§");
        this.refuseGroupTarget = config.getString("groups.refuseGroupTarget").replace("&", "§");
        this.noPlayersInGroup = config.getString("groups.noPlayersInGroup").replace("&", "§");
        this.notInGroup = config.getString("groups.notInGroup").replace("&", "§");
        this.ownerGroup = config.getString("groups.ownerGroup").replace("&", "§");
        this.requestSender = config.getString("groups.requestSender").replace("&", "§");
        this.requestTarget = config.getString("groups.requestTarget").replace("&", "§");
        this.scrollTargetRequest = config.getString("groups.scrollTargetRequest").replace("&", "§");
        this.groupLimit = config.getString("groups.groupLimit").replace("&", "§");
        this.yourselfCantGetOut = config.getString("groups.yourselfCantGetOut").replace("&", "§");
        this.targetNotInGroup = config.getString("groups.targetNotInGroup").replace("&", "§");
        this.targetDeleted = config.getString("groups.targetDeleted").replace("&", "§");
        this.alreadyOwner = config.getString("groups.alreadyOwner").replace("&", "§");
        this.justMemberToOwner = config.getString("groups.justMemberToOwner").replace("&", "§");
        this.newOwnerAllInGroup = config.getString("groups.newOwnerAllInGroup").replace("&", "§");
        this.newOwnerTarget = config.getString("groups.newOwnerTarget").replace("&", "§");
        this.quitGroupSender = config.getString("groups.quitGroupSender").replace("&", "§");
        this.quitGroupPlayers = config.getString("groups.quitGroupPlayers").replace("&", "§");
        this.tpEnabled = config.getString("groups.tpEnabled").replace("&", "§");
        this.tpDisabled = config.getString("groups.tpDisabled").replace("&", "§");
        this.tpAlreadyEnabled = config.getString("groups.tpAlreadyEnabled").replace("&", "§");
        this.tpAlreadyDisabled = config.getString("groups.tpAlreadyDisabled").replace("&", "§");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args)
    {
        if(!(sender instanceof ProxiedPlayer)) {return null;}

        ProxiedPlayer p = (ProxiedPlayer) sender;
        FriendsProvider fProvider = new FriendsProvider(p.getUniqueId());
        FriendsManager fManager;
        try {
            fManager = fProvider.getFManager();
        } catch (ManagerNotFoundException e) {
            throw new RuntimeException(e);
        }
        if(args.length == 1)
        {
            List<String> list = new ArrayList<>();
            list.add("help");
            list.add("create");
            list.add("delete");
            list.add("accept");
            list.add("refuse");
            list.add("quit");
            list.add("add");
            list.add("remove");
            list.add("tp");
            list.add("list");
            list.add("owner");
            return list;
        }else if(args.length == 2)
        {
            List<String> list = new ArrayList<>();
            if(args[0].equalsIgnoreCase("add"))
            {
                for(ProxiedPlayer pls : ProxyServer.getInstance().getPlayers())
                {
                    list.add(pls.getName());
                }
            }else if(args[0].equalsIgnoreCase("remove"))
            {
                if (fManager.isInGroup())
                {
                    PartyProvider pProvider = new PartyProvider(fManager.getGroupId());

                    if(!pProvider.pExist())
                    {
                        fManager.setGroupId(null);
                        fProvider.save(fManager);
                        return null;
                    }

                    PartyManager party;
                    try {
                        party = pProvider.getPManager();
                    } catch (ManagerNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    for (ProxiedPlayer pls : party.getPlayersInGroup())
                    {
                        list.add(pls.getName());
                    }
                }
            }else if(args[0].equalsIgnoreCase("tp"))
            {
                list.add("enable");
                list.add("disable");
            }
            return list;
        }
        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if (!(sender instanceof ProxiedPlayer))
        {
            sender.sendMessage(new TextComponent(prefix+" "+suffix+" "+cmdNotUsable));
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer)sender;

        FriendsProvider provider = new FriendsProvider(p.getUniqueId());
        FriendsManager fManager;
        try {
            fManager = provider.getFManager();
        } catch (ManagerNotFoundException e)
        {
            e.printStackTrace();
            return;
        }

        if (args.length == 0)
        {
            H.helpParty(p);
        }else if (args.length == 1)
        {
            if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h"))
            {
                H.helpParty(p);
            }else if(args[0].equalsIgnoreCase("create"))
            {
                if(!fManager.isInGroup())
                {
                    PartyProvider pProvider = new PartyProvider(p);
                    PartyManager party;
                    try {
                        party = pProvider.getPManager();
                    } catch (ManagerNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    fManager.setGroupId(party.getGroupId());
                    provider.save(fManager);
                    sendMessage(p, prefix+" "+suffix+" "+groupCreated);
                }else {
                    sendMessage(p, prefix+" "+suffix+" "+alreadyInGroup);
                }
            }
            else if (args[0].equalsIgnoreCase("accept"))
            {
                if (!requestGp.containsKey(p.getUniqueId()))
                {
                    sendMessage(p, prefix+" "+suffix+" "+noRequest);
                    return;
                }
                if (requestGp.get(p.getUniqueId()) == null)
                {
                    sendMessage(p, prefix+" "+suffix+" "+errorAdd);
                    requestGp.remove(p.getUniqueId());
                    return;
                }
                if(fManager.isInGroup())
                {
                    sendMessage(p, prefix+" "+suffix+" "+alreadyInGroup);
                    return;
                }

                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(requestGp.get(p.getUniqueId()));
                FriendsProvider targetProvider = new FriendsProvider(target.getUniqueId());
                FriendsManager targetManager;
                try {
                    targetManager = targetProvider.getFManager();
                } catch (ManagerNotFoundException e) {
                    throw new RuntimeException(e);
                }
                PartyProvider partyProvider = new PartyProvider(targetManager.getGroupId());
                PartyManager party;
                try {
                    party = partyProvider.getPManager();
                } catch (ManagerNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if(party == null)
                {
                    sendMessage(p, prefix+" "+suffix+" "+errorAdd);
                    requestGp.remove(p.getUniqueId());
                    return;
                }
                sendMessage(p, prefix+" "+suffix+" "+newGroupTarget.replace("%player%", target.getName()));

                sendMessage(target, prefix+" "+suffix+" "+newGroupSender.replace("%targetPlayer%", p.getName()));

                requestGp.remove(p.getUniqueId());
                party.addPlayerInGroup(p);
                partyProvider.save(party);
            }else if(args[0].equalsIgnoreCase("quit"))
            {
                if(fManager.isInGroup())
                {
                    PartyProvider pProvider = new PartyProvider(fManager.getGroupId());

                    if(!pProvider.pExist())
                    {
                        fManager.setGroupId(null);
                        provider.save(fManager);
                        sendMessage(p, prefix+" "+suffix+" "+notInGroup);
                        return;
                    }

                    PartyManager party;
                    try {
                        party = pProvider.getPManager();
                    } catch (ManagerNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    if(party.isOwner(p.getUniqueId())) {party.onQuit();return;}

                    party.removePlayerInGroup(p);
                    pProvider.save(party);
                    sendMessage(p, prefix+" "+suffix+" "+quitGroupSender);
                    for(ProxiedPlayer pls : party.getPlayersInGroup())
                    {
                        sendMessage(pls, prefix+" "+suffix+" "+quitGroupPlayers.replace("%player%", p.getName()));
                    }
                }else {
                    sendMessage(p, prefix+" "+suffix+" "+notInGroup);
                }
            }else if (args[0].equalsIgnoreCase("refuse"))
            {
                if (!requestGp.containsKey(p.getUniqueId()))
                {
                    sendMessage(p, prefix+" "+suffix+" "+noRequest);
                    return;
                }
                if (requestGp.get(p.getUniqueId()) == null)
                {
                    sendMessage(p, prefix+" "+suffix+" "+errorAdd);
                    requestGp.remove(p.getUniqueId());
                    return;
                }

                ProxiedPlayer pl = ProxyServer.getInstance().getPlayer(requestGp.get(p.getUniqueId()));

                sendMessage(p, prefix+" "+suffix+" "+refuseGroupSender.replace("%targetPlayer%", pl.getName()));

                sendMessage(pl, prefix+" "+suffix+" "+refuseGroupTarget.replace("%player%", p.getName()));

                requestGp.remove(p.getUniqueId());
            } else if(args[0].equalsIgnoreCase("list"))
            {
                if(fManager.isInGroup())
                {
                    PartyProvider pProvider = new PartyProvider(fManager.getGroupId());

                    if(!pProvider.pExist())
                    {
                        fManager.setGroupId(null);
                        provider.save(fManager);
                        sendMessage(p, prefix+" "+suffix+" "+notInGroup);
                        return;
                    }

                    PartyManager party;
                    try {
                        party = pProvider.getPManager();
                    } catch (ManagerNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    if (party.getPlayersInGroup().size() == 0 )
                    {
                        sendMessage(p, prefix+" "+suffix+" "+noPlayersInGroup);
                    }else {
                        List<String> membersOnline = new ArrayList<>();
                        List<String> membersOffline = new ArrayList<>();
                        for (ProxiedPlayer playersInGroup : party.getPlayersInGroup())
                        {
                            if (ProxyServer.getInstance().getPlayer(playersInGroup.getUniqueId()) == null) {
                                membersOffline.add(playersInGroup.getName());
                                continue;
                            }
                            membersOnline.add(playersInGroup.getName());
                        }

                        int i = party.getGroupLenght();

                        sendMessage(p, " ");
                        if (i < 10)
                        {
                            sendMessage(p, "§6#§f--------------------- §2Groupe §f(§3"+i+"§f) --------------------§6#");
                        } else if (i < 100) {
                            sendMessage(p, "§6#§f--------------------- §2Groupe §f(§3"+i+"§f) -------------------§6#");
                        } else {
                            sendMessage(p, "§6#§f-------------------- §2Groupe §f(§3"+i+"§f) -------------------§6#");
                        }
                        sendMessage(p, " ");
                        if (membersOnline.isEmpty())
                        {
                            sendMessage(p, "§6Membres §aen ligne §f: §30");
                            sendMessage(p, " ");
                        } else {
                            StringBuilder colorPath = new StringBuilder();
                            for (int x = 0; x < membersOnline.size(); x++) {
                                colorPath.append("§b").append(membersOnline.get(x)).append("§c, ");
                                sendMessage(p, "§6Membres §aen ligne §f: §3" + membersOnline.size());
                                sendMessage(p, " ");
                                sendMessage(p, colorPath.substring(0, colorPath.length() - 2));
                                sendMessage(p, " ");
                            }
                        }
                        if (membersOffline.isEmpty())
                        {
                            sendMessage(p, "§6Membres §chors-ligne §f: §30");
                            sendMessage(p, " ");
                        } else {
                            StringBuilder colorPath = new StringBuilder();
                            for (int x = 0; x < membersOffline.size(); x++) {
                                colorPath.append("§7").append(membersOffline.get(x)).append("§c, ");
                                sendMessage(p, "§6Membres §chors-ligne §f: §3" + membersOffline.size());
                                sendMessage(p, " ");
                                sendMessage(p, colorPath.substring(0, colorPath.length() - 2));
                                sendMessage(p, " ");
                            }
                        }
                        sendMessage(p, "§6#§f---------------------------------------------------§6#");
                    }
                }else {
                    sendMessage(p, prefix+" "+suffix+" "+notInGroup);
                }
            }else if(args[0].equalsIgnoreCase("delete"))
            {
                if(fManager.isInGroup())
                {
                    PartyProvider pProvider = new PartyProvider(fManager.getGroupId());

                    if(!pProvider.pExist())
                    {
                        fManager.setGroupId(null);
                        provider.save(fManager);
                        sendMessage(p, prefix+" "+suffix+" "+notInGroup);
                        return;
                    }

                    try {
                        PartyManager party = pProvider.getPManager();

                        if(party.isOwner(p.getUniqueId()))
                        {
                            sendMessage(p, prefix+" "+suffix+" "+groupDeletedSender);
                            party.removePlayerInGroup(p);
                            party.delete();
                        }else {
                            sendMessage(p, prefix+" "+suffix+" "+ownerGroup);
                        }
                    } catch (ManagerNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    sendMessage(p, prefix+" "+suffix+" "+notInGroup);
                }
            }else {
                H.helpParty(p);
            }
        }else if (args.length == 2)
        {
            String targetName = args[1];

            if(!fManager.isInGroup()) {sendMessage(p, prefix+" "+suffix+" "+notInGroup);return;}

            if (ProxyServer.getInstance().getPlayer(targetName) == null)
            {
                sendMessage(p, prefix+" "+suffix+" "+playerNotFound.replace("%targetPlayer%", targetName));
                return;
            }

            PartyProvider partyProvider = new PartyProvider(p);

            if(!partyProvider.pExist())
            {
                fManager.setGroupId(null);
                provider.save(fManager);
                sendMessage(p, prefix+" "+suffix+" "+notInGroup);
                return;
            }

            PartyManager party;
            try {
                party = partyProvider.getPManager();
            } catch (ManagerNotFoundException e) {
                throw new RuntimeException(e);
            }

            ProxiedPlayer targetPl = ProxyServer.getInstance().getPlayer(targetName);

            if(args[0].equalsIgnoreCase("add"))
            {
                if(!party.isOwner(p.getUniqueId())) {sendMessage(p, prefix+" "+suffix+" "+ownerGroup);return;}
                if (targetPl == p) {sendMessage(p, prefix+" "+suffix+" "+alreadyInGroup);return;}
                if (party.getPlayersInGroup().size() == party.getGroupLenght())
                {
                    sendMessage(p, prefix+" "+suffix+" "+groupLimit.replace("%groupSize%", String.valueOf(party.getGroupLenght())));
                    return;
                }

                TextComponent targetTxt = new TextComponent(prefix+" "+suffix+" "+requestTarget.replace("%player%", p.getName()));
                targetTxt.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(scrollTargetRequest).create()));
                targetTxt.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p accept"));

                sendMessage(p, prefix+" "+suffix+" "+requestSender.replace("%targetPlayer%", targetName));
                targetPl.sendMessage(targetTxt);

                requestGp.put(targetPl.getUniqueId(), p.getUniqueId());
            }else if (args[0].equalsIgnoreCase("remove"))
            {
                if(!party.isOwner(p.getUniqueId())) {sendMessage(p, prefix+" "+suffix+" "+ownerGroup);return;}
                if (targetPl == p) {sendMessage(p, prefix+" "+suffix+" "+yourselfCantGetOut);return;}
                if (!party.getPlayersInGroup().contains(targetPl))
                {
                    sendMessage(p, prefix+" "+suffix+" "+targetNotInGroup.replace("%targetPlayer%", targetName));
                    return;
                }

                party.removePlayerInGroup(targetPl);
                sendMessage(p, prefix+" "+suffix+" "+targetDeleted.replace("%targetPlayer%", targetName));
            }else if (args[0].equalsIgnoreCase("owner"))
            {
                if(!party.isOwner(p.getUniqueId())) {sendMessage(p, prefix+" "+suffix+" "+ownerGroup);return;}
                if (targetPl == p) {sendMessage(p, prefix+" "+suffix+" "+alreadyOwner);return;}
                if (!party.getPlayersInGroup().contains(targetPl)) {
                    sendMessage(p, prefix+" "+suffix+" "+justMemberToOwner);return;
                }

                for (ProxiedPlayer players : party.getPlayersInGroup())
                {
                    if (players != targetPl)
                    {
                        sendMessage(p, prefix+" "+suffix+" "+newOwnerAllInGroup.replace("%targetPlayer%", targetName));
                    }
                }
                sendMessage(targetPl, prefix+" "+suffix+" "+newOwnerTarget);
                party.changeOwnerGroup(targetPl);
            }else if(args[0].equalsIgnoreCase("tp"))
            {
                if(!party.isOwner(p.getUniqueId())) {sendMessage(p, prefix+" "+suffix+" "+ownerGroup);return;}

                if(args[1].equalsIgnoreCase("enable"))
                {
                    if(!party.tp())
                    {
                        party.setTp(true);
                        sendMessage(p, prefix+" "+suffix+" "+tpEnabled);
                        return;
                    }
                    sendMessage(p, prefix+" "+suffix+" "+tpAlreadyEnabled);
                }else if(args[0].equalsIgnoreCase("disable"))
                {
                    if(party.tp())
                    {
                        party.setTp(false);
                        sendMessage(p, prefix+" "+suffix+" "+tpDisabled);
                        return;
                    }
                    sendMessage(p, prefix+" "+suffix+" "+tpAlreadyDisabled);
                }else {
                    H.helpParty(p);
                }
            }else {
                H.helpParty(p);
            }
        }
    }

    private void sendMessage(ProxiedPlayer p, String s) {
        p.sendMessage(new TextComponent(s));
    }
}
