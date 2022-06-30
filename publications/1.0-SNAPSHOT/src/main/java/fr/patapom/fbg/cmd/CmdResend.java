package fr.patapom.fbg.cmd;

import fr.patapom.fbg.FriendsBG;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is part of FriendsBungee (FriendsBG-Free), a bungeecord friends plugin.
 *
 * Copyright (C) <2022>  <Dylan André>
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

public class CmdResend extends Command implements TabExecutor
{
    private final Configuration config;
    private final String playerOffline;
    private final String cmdNotUsable;
    private final String noMessage;
    private final String sPrefix;
    private final String sdPrefix;
    private final String sSuffix;
    private final String sdSuffix;
    private final String tPrefix;
    private final String tSuffix;
    private final String msgColor;
    private final String tooLong;
    private final String suffixH;
    private final String cmdMsg;
    private final String cmdR;

    public CmdResend()
    {
        super("resend", null, "r");
        this.config = FriendsBG.getInstance().getConfig();
        this.playerOffline = config.getString("msg.playerOffline").replace("&", "§");
        this.cmdNotUsable = config.getString("msg.cmdNotUsable").replace("&", "§");
        this.noMessage = config.getString("msg.noMessage").replace("&", "§");
        this.sPrefix = config.getString("msg.sender.prefix").replace("&", "§");
        this.sdPrefix = config.getString("msg.sender.2ndPrefix").replace("&", "§");
        this.sSuffix = config.getString("msg.sender.suffix").replace("&", "§");
        this.sdSuffix = config.getString("msg.sender.2ndSuffix").replace("&", "§");
        this.tPrefix = config.getString("msg.target.prefix").replace("&", "§");
        this.tSuffix = config.getString("msg.target.suffix").replace("&", "§");
        this.msgColor = config.getString("msg.messageColor").replace("&", "§");
        this.tooLong = config.getString("msg.tooLong").replace("&", "§");
        this.suffixH = config.getString("msg.sHelp").replace("&", "§");
        this.cmdMsg = config.getString("msg.cmdMsg").replace("&", "§");
        this.cmdR = config.getString("msg.cmdR").replace("&", "§");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args)
    {
        if(sender instanceof ProxiedPlayer)
        {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            List<String> list = new ArrayList<>();
            if(args.length == 1)
            {

                for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
                {
                    list.add(player.getName());
                }
                return list;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(sender instanceof ProxiedPlayer)
        {
            ProxiedPlayer p = (ProxiedPlayer) sender;

            if(args.length == 0)
            {
                helpMsg(p);
            }else {
                if(FriendsBG.messages.containsKey(p))
                {
                    if(FriendsBG.messages.get(p).isConnected())
                    {
                        ProxiedPlayer targetPl = FriendsBG.messages.get(p);
                        StringBuilder msg = new StringBuilder();

                        if(args.length > msg.capacity())
                        {
                            sendMessage(p, tooLong);
                        }else {
                            for(int i = 0; i != args.length; i++)
                            {
                                msg.append(args[i].replace("&", "§")).append(" ");
                            }

                            final String part1 = sPrefix+" "+sSuffix+" ";
                            final String part2 = sdPrefix.replace("%targetPlayer%", targetPl.getName())+" "+sdSuffix+" ";
                            p.sendMessage(new TextComponent(part1+part2+msgColor+msg));
                            targetPl.sendMessage(new TextComponent(
                                    tPrefix.replace("%player%", p.getName())+" "+tSuffix+" "+msgColor+msg));

                            FriendsBG.messages.remove(p);
                            FriendsBG.messages.remove(targetPl);
                            FriendsBG.messages.put(p, targetPl);
                            FriendsBG.messages.put(targetPl, p);
                        }
                    }else {
                        sendMessage(p, playerOffline);
                    }
                }else {
                    sendMessage(p, noMessage);
                }
            }
        }else {
            sendDeniedUsage(sender);
        }
    }

    private void helpMsg(ProxiedPlayer p)
    {
        sendMessage(p, " ");
        sendMessage(p, "§6#§f-------------------- [§3Friends§f-§6BG§f] -------------------§6#");
        sendMessage(p, " ");
        sendMessage(p, " §6§l? §7§nHelp§f : ");
        sendMessage(p, " ");
        sendMessage(p, "§f/§bmsg §7<§6player§7> §7<§fmessage§7> "+suffixH+" "+cmdMsg);
        sendMessage(p, " ");
        sendMessage(p, "§f/§br §7<§fmessage§7> "+suffixH+" "+cmdR);
        sendMessage(p, " ");
        sendMessage(p, "§6#§f----------------------- §2FREE §f-----------------------§6#");
    }

    private void sendMessage(ProxiedPlayer p, String s) {
        p.sendMessage(new TextComponent(s));
    }

    private void sendDeniedUsage(CommandSender sender) {
        sender.sendMessage(new TextComponent(cmdNotUsable));
    }
}
