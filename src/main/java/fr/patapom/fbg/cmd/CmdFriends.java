package fr.patapom.fbg.cmd;

import fr.patapom.fbg.FriendsBG;
import fr.patapom.commons.friends.FriendsManager;
import fr.patapom.commons.friends.FriendsProvider;
import fr.patapom.fbg.utils.exceptions.FManagerNotFoundException;
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

public class CmdFriends extends Command implements TabExecutor
{
    private Map<ProxiedPlayer, ProxiedPlayer> requestFriend = new HashMap<>();
    private final Configuration config;

    private final String prefix;
    private final String suffix;
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

    private final String suffixH;
    private final String enable;
    private final String disable;
    private final String accept;
    private final String refuse;
    private final String add;
    private final String remove;
    private final String list;

    public CmdFriends()
    {
        super("friend", null, new String[] { "friends", "f" });
        this.config = FriendsBG.getInstance().getConfig();
        this.prefix = config.getString("friends.prefix").replace("&", "§");
        this.suffix = config.getString("friends.suffix").replace("&", "§");
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
        this.suffixH = config.getString("friends.sHelp").replace("&", "§");
        this.enable = config.getString("friends.enable").replace("&", "§");
        this.disable = config.getString("friends.disable").replace("&", "§");
        this.accept = config.getString("friends.accept").replace("&", "§");
        this.refuse = config.getString("friends.refuse").replace("&", "§");
        this.add = config.getString("friends.add").replace("&", "§");
        this.remove = config.getString("friends.remove").replace("&", "§");
        this.list = config.getString("friends.list").replace("&", "§");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args)
    {
        if(sender instanceof ProxiedPlayer)
        {
            ProxiedPlayer p = (ProxiedPlayer) sender;
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
                    for(ProxiedPlayer pls : p.getServer().getInfo().getPlayers())
                    {
                        list.add(pls.getName());
                    }
                }else if(args[0].equalsIgnoreCase("remove")){
                    FriendsProvider provider = new FriendsProvider(p.getUniqueId());
                    try {
                        FriendsManager fManager = provider.getFManager();
                        list.addAll(fManager.getFriendsMap().keySet());
                    } catch (FManagerNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return list;
            }
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("deprecation")
    public void execute(CommandSender sender, String[] args)
    {
        if (!(sender instanceof ProxiedPlayer))
        {
            sender.sendMessage(new TextComponent(prefix+" "+suffix+" "+cmdNotUsable));
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer)sender;

        if (args.length == 0)
        {
            helpFriend(p);
            return;
        }

        try {
            FriendsProvider provider = new FriendsProvider(p.getUniqueId());
            FriendsManager fManager = provider.getFManager();

            if (args.length == 1)
            {
                if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h"))
                {
                    helpFriend(p);
                    return;
                }else if (args[0].equalsIgnoreCase("enable"))
                {
                    if (!fManager.isAllow()) {
                        fManager.setAllow(true);
                        provider.save(fManager);
                        sendMessage(p, prefix+" "+suffix+" "+requestsAllow);
                    } else {
                        sendMessage(p, prefix+" "+suffix+" "+requestsAlreadyEnabled);
                    }
                }else if (args[0].equalsIgnoreCase("disable")) {
                    if (fManager.isAllow()) {
                        fManager.setAllow(false);
                        provider.save(fManager);
                        sendMessage(p, prefix+" "+suffix+" "+requestsDeny);
                    } else {
                        sendMessage(p, prefix+" "+suffix+" "+requestsAlreadyDisabled);
                    }
                }else if (args[0].equalsIgnoreCase("accept"))
                {
                    if (!requestFriend.containsKey(p))
                    {
                        sendMessage(p, prefix+" "+suffix+" "+noRequest);
                        return;
                    }
                    if(requestFriend.get(p) == null)
                    {
                        sendMessage(p, prefix+" "+suffix+" "+errorAdd);
                        return;
                    }
                    final String targetName = requestFriend.get(p).getName();
                    final UUID targetUUID = requestFriend.get(p).getUniqueId();
                    if (fManager.isFriends(targetName))
                    {
                        sendMessage(p, prefix+" "+suffix+" "+alreadyFriends);
                        return;
                    }

                    FriendsProvider targetProvider = new FriendsProvider(targetUUID);
                    FriendsManager targetManager;
                    try {
                        targetManager = targetProvider.getFManager();
                    } catch (FManagerNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }

                    fManager.addFriend(targetUUID);
                    targetManager.addFriend(p.getUniqueId());
                    provider.save(fManager);
                    targetProvider.save(targetManager);

                    sendMessage(p, prefix+" "+suffix+" "+newFriendSender.replace("%targetPlayer%", targetName));
                    sendMessage(requestFriend.get(p), prefix+" "+suffix+" "+newFriendTarget.replace("%player%", p.getName()));
                    this.requestFriend.remove(p);

                } else if (args[0].equalsIgnoreCase("refuse"))
                {
                    if (!requestFriend.containsKey(p))
                    {
                        sendMessage(p, prefix+" "+suffix+" "+noRequest);
                        return;
                    }
                    if (requestFriend.get(p) == null)
                    {
                        sendMessage(p, prefix+" "+suffix+" "+errorAdd);
                        return;
                    }
                    final String targetName = requestFriend.get(p).getName();
                    if (fManager.isFriends(targetName))
                    {
                        sendMessage(p, prefix+" "+suffix+" "+alreadyFriends);
                        return;
                    }

                    sendMessage(p, prefix+" "+suffix+" "+refuseFriendSender.replace("%targetPlayer%", targetName));
                    sendMessage(this.requestFriend.get(p), prefix+" "+suffix+" "+refuseFriendTarget.replace("%player%", p.getName()));
                    requestFriend.remove(p);
                }else if (args[0].equalsIgnoreCase("list"))
                {
                    if (fManager.getNbFriends() == 0)
                    {
                        sendMessage(p, prefix+" "+suffix+" "+noFriendsInList);
                    } else {
                        List<String> friendsOnline = new ArrayList<>();
                        List<String> friendsOffline = new ArrayList<>();
                        for(String s : fManager.getFriendsMap().keySet())
                        {
                            if(ProxyServer.getInstance().getPlayer(s) == null)
                            {
                                friendsOffline.add(s);
                            }else {
                                friendsOnline.add(s);
                            }
                        }

                        int i = fManager.getNbFriends();

                        sendMessage(p, " ");
                        if (i < 10)
                        {
                            sendMessage(p, "§6#§f-------------------- §bFriends §f(§3"+i+"§f) --------------------§6#");
                        } else if (i < 100) {
                            sendMessage(p, "§6#§f-------------------- §bFriends §f(§3"+i+"§f) -------------------§6#");
                        } else {
                            sendMessage(p, "§6#§f------------------- §bFriends §f(§3"+i+"§f) -------------------§6#");
                        }
                        sendMessage(p, " ");
                        if (friendsOnline.isEmpty())
                        {
                            sendMessage(p, msgFOnline+" §30");
                            sendMessage(p, " ");
                        } else {
                            StringBuilder colorPath = new StringBuilder();
                            for (int x = 0; x < friendsOnline.size(); x++)
                            {
                                colorPath.append("§b").append(friendsOnline.get(x)).append("§c, ");
                                sendMessage(p, msgFOnline+" §3" + friendsOnline.size());
                                sendMessage(p, " ");
                                sendMessage(p, colorPath.substring(0, colorPath.length() - 2));
                                sendMessage(p, " ");
                            }
                        }
                        if (friendsOffline.isEmpty())
                        {
                            sendMessage(p, msgFOffline+" §30");
                            sendMessage(p, " ");
                        } else {
                            StringBuilder colorPath = new StringBuilder();
                            for (int x = 0; x < friendsOffline.size(); x++)
                            {
                                colorPath.append("§7").append(friendsOffline.get(x)).append("§c, ");
                                sendMessage(p, msgFOffline+" §3" + friendsOffline.size());
                                sendMessage(p, " ");
                                sendMessage(p, colorPath.substring(0, colorPath.length() - 2));
                                sendMessage(p, " ");
                            }
                        }
                        sendMessage(p, "§6#§f---------------------------------------------------§6#");
                    }
                }else {
                    if (args[0].equalsIgnoreCase("add")) {
                        helpFriend(p);
                        return;
                    }
                    if (args[0].equalsIgnoreCase("remove")) {
                        helpFriend(p);
                        return;
                    }
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("add")) {
                    String targetName = args[1];
                    if (ProxyServer.getInstance().getPlayer(targetName) != null)
                    {
                        UUID targetUUID = ProxyServer.getInstance().getPlayer(targetName).getUniqueId();
                        if (p.getUniqueId().equals(targetUUID))
                        {
                            sendMessage(p, prefix+" "+suffix+" "+yourSelfAsaFriend);
                            return;
                        }
                        if (fManager.isFriends(targetName))
                        {
                            sendMessage(p, prefix+" "+suffix+" "+alreadyFriends.replace("%targetPlayer%", targetName));
                            return;
                        }
                        FriendsProvider targetProvider = new FriendsProvider(targetUUID);
                        FriendsManager targetManager;
                        try {
                            targetManager = targetProvider.getFManager();
                        } catch (FManagerNotFoundException e) {
                            e.printStackTrace();
                            return;
                        }
                        if (targetManager.isAllow()) {
                            if (fManager.isAllow()) {
                                if (this.requestFriend.containsValue(p))
                                {
                                    sendMessage(p, prefix+" "+suffix+" "+alreadyInProgress.replace("%targetPlayer%", targetName));
                                    return;
                                }
                                this.requestFriend.put(ProxyServer.getInstance().getPlayer(targetName), p);

                                TextComponent targetTxt = new TextComponent(prefix+" "+suffix+" "+friendRequestTarget.replace("%player%", p.getName()));
                                targetTxt.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(scrollTargetRequest).create()));
                                targetTxt.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f accept"));

                                sendMessage(p, prefix+" "+suffix+" "+friendRequestSender.replace("%targetPlayer%", targetName));
                                ProxyServer.getInstance().getPlayer(targetName).sendMessage(targetTxt);

                            } else {
                                sendMessage(p, prefix+" "+suffix+" "+senderRequestsDeny);
                            }
                        } else {
                            sendMessage(p, prefix+" "+suffix+" "+targetRequestsDeny.replace("%targetPlayer%", targetName));
                        }
                    }else {
                        sendMessage(p, prefix+" "+suffix+" "+playerNotFound.replace("%targetPlayer%", targetName));
                    }
                } else if (args[0].equalsIgnoreCase("remove"))
                {
                    String targetName = args[1];
                    if (p.getName().equals(targetName))
                    {
                        sendMessage(p, prefix+" "+suffix+" "+cantGetOut);
                        return;
                    }
                    if (!fManager.isFriends(targetName))
                    {
                        sendMessage(p, prefix+" "+suffix+" "+notFriends.replace("%targetPlayer%", targetName));
                        return;
                    }
                    UUID targerUUID = fManager.getFriendsMap().get(targetName);
                    FriendsProvider targetProvider = new FriendsProvider(targerUUID);
                    FriendsManager targetManager;
                    try {
                        targetManager = targetProvider.getFManager();
                    } catch (FManagerNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }
                    fManager.removeFriend(targetName);
                    targetManager.removeFriend(p.getName());
                    provider.save(fManager);
                    targetProvider.save(targetManager);
                    sendMessage(p, prefix+" "+suffix+" "+deletedFriend.replace("%targetPlayer%", targetName));
                }
            }
        } catch (FManagerNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void helpFriend(ProxiedPlayer p)
    {
        sendMessage(p, " ");
        sendMessage(p, "§6#§f-------------------- [§3Friends§f-§6BG§f] -------------------§6#");
        sendMessage(p, " ");
        sendMessage(p, " §6§l? §7§nHelp§f : ");
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bf §fenable "+suffixH+" "+enable);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bf §fdisable "+suffixH+" "+disable);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bf §faccept "+suffixH+" "+accept);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bf §frefuse "+suffixH+" "+refuse);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bf §fadd §7<§6player§7> "+suffixH+" "+add);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bf §fremove §7<§6player§7> "+suffixH+" "+remove);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bf §flist "+suffixH+" "+list);
        sendMessage(p, " ");
        sendMessage(p, "§6#§f----------------------- §2FREE §f-----------------------§6#");
    }

    private void sendMessage(ProxiedPlayer p, String s) {p.sendMessage(new TextComponent(s));}
}
