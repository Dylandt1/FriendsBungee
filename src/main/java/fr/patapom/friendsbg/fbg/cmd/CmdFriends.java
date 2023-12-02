package fr.patapom.friendsbg.fbg.cmd;

import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.patapom.friendsbg.common.players.ProfileManager;
import fr.patapom.friendsbg.common.players.ProfileProvider;
import fr.patapom.friendsbg.fbg.cmd.utils.Help;
import fr.tmmods.tmapi.bungee.data.manager.DBManager;
import fr.tmmods.tmapi.exceptions.ManagerNotFoundException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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

public class CmdFriends extends Command implements TabExecutor
{
    private Map<ProxiedPlayer, ProxiedPlayer> requestFriend = new HashMap<>();

    private final Help H = new Help();

    private final String prefix;
    private final String suffix;
    private final String mainPrefix;
    private final String mainSuffix;
    private final String cmdNotUsable;
    private final String playerNotFound;
    private final String requestsAllow;
    private final String requestsAlreadyEnabled;
    private final String requestsDeny;
    private final String requestsAlreadyDisabled;
    private final String senderRequestsDeny;
    private final String targetRequestsDeny;
    private final String noFriendsInList;
    private final String msgFOnline;
    private final String msgFOffline;
    private final String alreadyFriends;
    private final String alreadyInProgress;
    private final String friendRequestSender;
    private final String friendRequestTarget;
    private final String scrollTargetRequest;
    private final String newFriendSender;
    private final String newFriendTarget;
    private final String yourSelfAsaFriend;
    private final String cantGetOut;
    private final String notFriends;
    private final String deletedFriend;
    private final String errorAdd;
    private final String noRequest;
    private final String refuseFriendSender;
    private final String refuseFriendTarget;

    public CmdFriends()
    {
        super("friend", null, FriendsBG.getInstance().getConfig().getStringList("friends.cmdAlias").toArray(new String[0]));
        Configuration config = FriendsBG.getInstance().getConfig();
        this.prefix = config.getString("friends.prefix").replace("&", "§");
        this.suffix = config.getString("friends.suffix").replace("&", "§");
        this.mainPrefix = config.getString("prefix").replace("&", "§");
        this.mainSuffix = config.getString("suffix").replace("&", "§");
        this.cmdNotUsable = config.getString("friends.cmdNotUsable").replace("&", "§");
        this.playerNotFound = config.getString("friends.playerNotFound").replace("&", "§");
        this.requestsAllow = config.getString("friends.requestsAllow").replace("&", "§");
        this.requestsAlreadyEnabled = config.getString("friends.requestsAlreadyEnabled").replace("&", "§");
        this.requestsDeny = config.getString("friends.requestsDeny").replace("&", "§");
        this.requestsAlreadyDisabled = config.getString("friends.requestsAlreadyDisabled").replace("&", "§");
        this.senderRequestsDeny = config.getString("friends.senderRequestsDeny").replace("&", "§");
        this.targetRequestsDeny = config.getString("friends.targetRequestsDeny").replace("&", "§");
        this.noFriendsInList = config.getString("friends.noFriendsInList").replace("&", "§");
        this.msgFOnline = config.getString("friends.friendsOnline").replace("&", "§");
        this.msgFOffline = config.getString("friends.friendsOffline").replace("&", "§");
        this.alreadyFriends = config.getString("friends.alreadyFriends").replace("&", "§");
        this.alreadyInProgress = config.getString("friends.alreadyInProgress").replace("&", "§");
        this.friendRequestSender = config.getString("friends.friendRequestSender").replace("&", "§");
        this.friendRequestTarget = config.getString("friends.friendRequestTarget").replace("&", "§");
        this.scrollTargetRequest = config.getString("friends.scrollTargetRequest").replace("&", "§");
        this.newFriendSender = config.getString("friends.newFriendSender").replace("&", "§");
        this.newFriendTarget = config.getString("friends.newFriendTarget").replace("&", "§");
        this.yourSelfAsaFriend = config.getString("friends.yourSelfAsaFriend").replace("&", "§");
        this.cantGetOut = config.getString("friends.cantGetOut").replace("&", "§");
        this.notFriends = config.getString("friends.notFriends").replace("&", "§");
        this.deletedFriend = config.getString("friends.deletedFriend").replace("&", "§");
        this.errorAdd = config.getString("friends.errorAddFriend").replace("&", "§");
        this.noRequest = config.getString("friends.noRequest").replace("&", "§");
        this.refuseFriendSender = config.getString("friends.refuseFriendSender").replace("&", "§");
        this.refuseFriendTarget = config.getString("friends.refuseFriendTarget").replace("&", "§");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args)
    {
        if(!(sender instanceof ProxiedPlayer p)) {return null;}

        if(args.length == 1)
        {
            List<String> list = new ArrayList<>();
            list.add("help");
            list.add("enable");
            list.add("disable");
            list.add("accept");
            list.add("refuse");
            list.add("add");
            list.add("remove");
            list.add("list");
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
            }else if(args[0].equalsIgnoreCase("remove")){
                ProfileProvider provider = new ProfileProvider(p.getUniqueId());
                try {
                    ProfileManager profile = provider.getFManager();
                    list.addAll(profile.getFriendsMap().keySet());
                } catch (ManagerNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return list;
        }

        return new ArrayList<>();
    }

    @SuppressWarnings("deprecation")
    public void execute(CommandSender sender, String[] args)
    {
        if (!(sender instanceof ProxiedPlayer p))
        {
            sender.sendMessage(new TextComponent(prefix+" "+suffix+" "+cmdNotUsable));
            return;
        }


        if (args.length == 0)
        {
            H.helpFriends(p);
            return;
        }

        try {
            ProfileProvider provider = new ProfileProvider(p.getUniqueId());
            ProfileManager profile = provider.getFManager();

            if (args.length == 1)
            {
                if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h"))
                {
                    H.helpFriends(p);
                }else if (args[0].equalsIgnoreCase("enable"))
                {
                    if (profile.fAllow()) {sendMessage(p, prefix+suffix+requestsAlreadyEnabled);return;}

                    profile.setFAllow(true);
                    provider.save(profile);
                    sendMessage(p, prefix+suffix+requestsAllow);
                }else if (args[0].equalsIgnoreCase("disable")) {
                    if (!profile.fAllow()) {sendMessage(p, prefix+suffix+requestsAlreadyDisabled);return;}

                    profile.setFAllow(false);
                    provider.save(profile);
                    sendMessage(p, prefix+suffix+requestsDeny);
                }else if (args[0].equalsIgnoreCase("accept"))
                {
                    if (!requestFriend.containsKey(p)) {sendMessage(p, prefix+suffix+noRequest);return;}
                    if(requestFriend.get(p) == null) {sendMessage(p, prefix+suffix+errorAdd);return;}

                    final String targetName = requestFriend.get(p).getName();
                    final UUID targetUUID = requestFriend.get(p).getUniqueId();
                    if (profile.isFriends(targetName)) {sendMessage(p, prefix+suffix+alreadyFriends);return;}

                    ProfileProvider targetProvider = new ProfileProvider(targetUUID);
                    ProfileManager targetManager;
                    try {
                        targetManager = targetProvider.getFManager();
                    } catch (ManagerNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }

                    profile.addFriend(targetUUID);
                    targetManager.addFriend(p.getUniqueId());
                    provider.save(profile);
                    targetProvider.save(targetManager);

                    sendMessage(p, prefix+suffix+newFriendSender.replace("%targetPlayer%", targetName));
                    sendMessage(requestFriend.get(p), prefix+suffix+newFriendTarget.replace("%player%", p.getName()));
                    this.requestFriend.remove(p);
                } else if (args[0].equalsIgnoreCase("refuse"))
                {
                    if (!requestFriend.containsKey(p)) {sendMessage(p, prefix+suffix+noRequest);return;}
                    if (requestFriend.get(p) == null) {sendMessage(p, prefix+suffix+errorAdd);return;}

                    final String targetName = requestFriend.get(p).getName();
                    if (profile.isFriends(targetName)) {sendMessage(p, prefix+suffix+alreadyFriends);return;}

                    sendMessage(p, prefix+suffix+refuseFriendSender.replace("%targetPlayer%", targetName));
                    sendMessage(this.requestFriend.get(p), prefix+suffix+refuseFriendTarget.replace("%player%", p.getName()));
                    requestFriend.remove(p);
                }else if (args[0].equalsIgnoreCase("list"))
                {
                    if (profile.getNbFriends() == 0)
                    {
                        sendMessage(p, prefix+suffix+" "+noFriendsInList);
                    } else {
                        List<String> friendsOnline = new ArrayList<>();
                        List<String> friendsOffline = new ArrayList<>();
                        for(String s : profile.getFriendsMap().keySet())
                        {
                            if(ProxyServer.getInstance().getPlayer(s) == null)
                            {
                                friendsOffline.add(s);
                            }else {
                                friendsOnline.add(s);
                            }
                        }

                        int i = profile.getNbFriends();

                        sendMessage(p, " ");
                        sendMessage(p, "§6#§f-------------------- §bFriends §f(§3"+i+"§f) --------------------§6#");
                        sendMessage(p, " ");

                        if(friendsOnline.isEmpty())
                        {
                            sendMessage(p, msgFOnline.replace("%nbFriends%", String.valueOf(0)));
                        }else {
                            StringBuilder colorPath = new StringBuilder();

                            for(String s : friendsOnline) {
                                colorPath.append("§b").append(s).append("§c, ");
                            }

                            sendMessage(p, msgFOnline.replace("%nbFriends%", String.valueOf(friendsOnline.size())));
                            sendMessage(p, colorPath.substring(0, colorPath.length() - 2));
                            sendMessage(p, " ");
                        }

                        if(friendsOffline.isEmpty())
                        {
                            sendMessage(p, msgFOffline.replace("%nbFriends%", String.valueOf(0)));
                        }else {
                            StringBuilder colorPath = new StringBuilder();

                            for(String s : friendsOffline) {
                                colorPath.append("§7").append(s).append("§c, ");
                            }

                            sendMessage(p, msgFOffline.replace("%nbFriends%", String.valueOf(friendsOffline.size())));
                            sendMessage(p, colorPath.substring(0, colorPath.length() - 2));
                            sendMessage(p, " ");
                        }
                        sendMessage(p, "§6#§f---------------------------------------------------§6#");
                    }
                }else if(args[0].equalsIgnoreCase("opt")) {
                    if(!profile.opt() && !profile.getOpts().contains("F"))
                    {
                        profile.addOpts("F");
                        provider.save(profile);
                        sendMessage(p, mainPrefix+mainSuffix+"§6OPTs §f: §2"+profile.getOpts().size()+"§f/§24");
                    }
                }
            }else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("add"))
                {
                    String targetName = args[1];
                    if (ProxyServer.getInstance().getPlayer(targetName) == null)
                    {
                        sendMessage(p, prefix+suffix+playerNotFound.replace("%targetPlayer%", targetName));
                        return;
                    }

                    UUID targetUUID = ProxyServer.getInstance().getPlayer(targetName).getUniqueId();
                    if (p.getUniqueId().equals(targetUUID)) {sendMessage(p, prefix+suffix+yourSelfAsaFriend);return;}
                    if (profile.isFriends(targetName))
                    {
                        sendMessage(p, prefix+suffix+alreadyFriends.replace("%targetPlayer%", targetName));
                        return;
                    }

                    ProfileProvider targetProvider = new ProfileProvider(targetUUID);
                    ProfileManager targetManager;
                    try {
                        targetManager = targetProvider.getFManager();
                    } catch (ManagerNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }

                    if (!profile.fAllow()) {sendMessage(p, prefix+suffix+senderRequestsDeny);return;}
                    if (!targetManager.fAllow())
                    {
                        sendMessage(p, prefix+suffix+targetRequestsDeny.replace("%targetPlayer%", targetName));
                        return;
                    }

                    if (this.requestFriend.containsValue(p))
                    {
                        sendMessage(p, prefix+suffix+alreadyInProgress.replace("%targetPlayer%", targetName));
                        return;
                    }

                    this.requestFriend.put(ProxyServer.getInstance().getPlayer(targetName), p);

                    TextComponent targetTxt = new TextComponent(prefix+suffix+friendRequestTarget.replace("%player%", p.getName()));
                    targetTxt.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(scrollTargetRequest).create()));
                    targetTxt.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f accept"));

                    sendMessage(p, prefix+suffix+friendRequestSender.replace("%targetPlayer%", targetName));
                    ProxyServer.getInstance().getPlayer(targetName).sendMessage(targetTxt);
                } else if (args[0].equalsIgnoreCase("remove"))
                {
                    String targetName = args[1];
                    if (p.getName().equals(targetName)) {sendMessage(p, prefix+suffix+cantGetOut);return;}
                    if (!profile.isFriends(targetName))
                    {
                        sendMessage(p, prefix+suffix+notFriends.replace("%targetPlayer%", targetName));
                        return;
                    }

                    UUID targerUUID = profile.getFriendsMap().get(targetName);
                    ProfileProvider targetProvider = new ProfileProvider(targerUUID);
                    ProfileManager targetManager;
                    try {
                        targetManager = targetProvider.getFManager();
                    } catch (ManagerNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }

                    profile.removeFriend(targetName);
                    provider.save(profile);

                    targetManager.removeFriend(p.getName());
                    targetProvider.save(targetManager);

                    if(FriendsBG.getInstance().sqlEnable)
                    {
                        try {
                            String prefixTables = DBManager.FBG_DATABASE.getDbAccess().getCredentials().getPrefixTables();
                            String friendsTable = DBManager.FBG_DATABASE.getDbAccess().getCredentials().getFriendsTable();

                            PreparedStatement ps1 = DBManager.FBG_DATABASE.getDbAccess().getConnection().prepareStatement("DELETE FROM "+prefixTables+friendsTable+" WHERE uuid = ? AND friendUUID = ?");
                            ps1.setString(1, p.getUniqueId().toString());
                            ps1.setString(2, targerUUID.toString());
                            ps1.executeUpdate();
                            ps1.close();

                            PreparedStatement ps2 = DBManager.FBG_DATABASE.getDbAccess().getConnection().prepareStatement("DELETE FROM "+prefixTables+friendsTable+" WHERE uuid = ? AND friendUUID = ?");
                            ps2.setString(1, targerUUID.toString());
                            ps2.setString(2, p.getUniqueId().toString());
                            ps2.executeUpdate();
                            ps2.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    sendMessage(p, prefix+suffix+deletedFriend.replace("%targetPlayer%", targetName));
                }
            }else {
                H.helpFriends(p);
            }
        } catch (ManagerNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(ProxiedPlayer p, String s) {p.sendMessage(new TextComponent(s));}
}
