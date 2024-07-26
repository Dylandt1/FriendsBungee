package fr.patapom.friendsbg.fbg.cmd;

import fr.patapom.friendsbg.common.players.ProfileManager;
import fr.patapom.friendsbg.common.players.ProfileProvider;
import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.patapom.friendsbg.fbg.cmd.utils.Help;
import fr.tmmods.tmapi.exceptions.ManagerNotFoundException;
import net.bytebuddy.build.Plugin;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

public class CmdMsg extends Command implements TabExecutor
{
    private final Help H = new Help();

    private final String sPrefix;
    private final String sdPrefix;
    private final String sSuffix;
    private final String sdSuffix;
    private final String tPrefix;
    private final String tSuffix;
    private final String msgColor;
    private final String sendToSender;
    private final String tooLong;
    private final String playerNotFound;
    private final String cmdNotUsable;
    private final String msgEnabled;
    private final String msgDisabled;
    private final String msgAlreadyEnabled;
    private final String msgAlreadyDisabled;
    private final String msgSenderDisabled;
    private final String msgTargetDisabled;
    private final String errorMsg;
    private final String reportCmd;

    public CmdMsg()
    {
        super("message", null, FriendsBG.getInstance().getConfig().getStringList("msg.cmdAlias.send").toArray(new String[0]));
        Configuration msgConfig = FriendsBG.getInstance().getMsgConfig();
        this.sPrefix = msgConfig.getString("msg.sender.prefix").replace("&", "§");
        this.sdPrefix = msgConfig.getString("msg.sender.2ndPrefix").replace("&", "§");
        this.sSuffix = msgConfig.getString("msg.sender.suffix").replace("&", "§");
        this.sdSuffix = msgConfig.getString("msg.sender.2ndSuffix").replace("&", "§");
        this.tPrefix = msgConfig.getString("msg.target.prefix").replace("&", "§");
        this.tSuffix = msgConfig.getString("msg.target.suffix").replace("&", "§");
        this.msgColor = msgConfig.getString("msg.messageColor").replace("&", "§");
        this.sendToSender = msgConfig.getString("msg.sendToSender").replace("&", "§");
        this.tooLong = msgConfig.getString("msg.tooLong").replace("&", "§");
        this.playerNotFound = msgConfig.getString("msg.playerNotFound").replace("&", "§");
        this.cmdNotUsable = msgConfig.getString("msg.cmdNotUsable").replace("&", "§");
        this.msgEnabled = msgConfig.getString("msg.msgEnabled").replace("&", "§");
        this.msgDisabled = msgConfig.getString("msg.msgDisabled").replace("&", "§");
        this.msgAlreadyEnabled = msgConfig.getString("msg.msgAlreadyEnabled").replace("&", "§");
        this.msgAlreadyDisabled = msgConfig.getString("msg.msgAlreadyDisabled").replace("&", "§");
        this.msgSenderDisabled = msgConfig.getString("msg.msgSenderDisabled").replace("&", "§");
        this.msgTargetDisabled = msgConfig.getString("msg.msgTargetDisabled").replace("&", "§");
        this.errorMsg = msgConfig.getString("msg.errorMsg").replace("&", "§");
        this.reportCmd = msgConfig.getString("msg.reportCmd").replace("&", "§");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args)
    {
        if(!(sender instanceof ProxiedPlayer)) {return null;}

        List<String> list = new ArrayList<>();

        if(args.length == 1)
        {
            list.add("enable");
            list.add("disable");
            for(ProxiedPlayer pls : ProxyServer.getInstance().getPlayers())
            {
                list.add(pls.getName());
            }

            return list;
        }else if(args.length == 2)
        {
            if(!args[0].equalsIgnoreCase("") && !args[0].equalsIgnoreCase("enable") ||
                    !args[0].equalsIgnoreCase("") && !args[0].equalsIgnoreCase("enable"))
            {
                list.add("<message>");
                return list;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(!(sender instanceof ProxiedPlayer p)) {sendDeniedUsage(sender);return;}

        ProfileProvider profileProvider = new ProfileProvider(p.getUniqueId());
        ProfileManager profile;
        try {
            profile = profileProvider.getPManager();
        } catch (ManagerNotFoundException e) {
            throw new RuntimeException(e);
        }

        if(profile == null) {sendMessage(p, errorMsg.replace("%cmd%", reportCmd));return;}

        if(args.length == 0)
        {
            H.helpMsg(p);
        }else if(args.length == 1)
        {
            if(args[0].equalsIgnoreCase("enable"))
            {
                if(profile.msgAllow()) {sendMessage(p, msgAlreadyEnabled);return;}

                profile.setMsgAllow(true);
                profileProvider.save(profile);
                sendMessage(p, msgEnabled);
            }else if(args[0].equalsIgnoreCase("disable"))
            {
                if(!profile.msgAllow()) {sendMessage(p, msgAlreadyDisabled);return;}

                profile.setMsgAllow(false);
                profileProvider.save(profile);
                sendMessage(p, msgDisabled);
            }else {
                H.helpMsg(p);
            }
        }else
        {
            if(!profile.msgAllow()) {sendMessage(p, msgSenderDisabled.replace("%cmd%", "/message enable"));return;}

            String targetName = args[0];
            if(ProxyServer.getInstance().getPlayer(targetName) == null) {sendMessage(p, playerNotFound.replace("%targetPlayer%", targetName));return;}

            ProxiedPlayer targetPl = ProxyServer.getInstance().getPlayer(targetName);
            ProfileProvider targetProvider = new ProfileProvider(targetPl.getUniqueId());
            ProfileManager targetProfile;
            try {
                targetProfile = targetProvider.getPManager();
            } catch (ManagerNotFoundException e) {
                throw new RuntimeException(e);
            }

            if(targetProfile == null) {sendMessage(p, errorMsg);return;}

            if(targetPl == p)
            {
                sendMessage(p, sendToSender);
            }else {
                if(!targetProfile.msgAllow()) {sendMessage(p, msgTargetDisabled.replace("%targetPlayer%", targetPl.getDisplayName()));return;}

                StringBuilder msg = new StringBuilder();

                if(args.length > msg.capacity()) {sendMessage(p, tooLong);return;}

                for(int i = 1; i != args.length; i++)
                {
                    msg.append(args[i].replace("&", "§")).append(" ");
                }

                final String part1 = sPrefix+sSuffix;
                final String part2 = sdPrefix.replace("%targetPlayer%", targetName)+sdSuffix;
                sendMessage(p, part1+part2+msgColor+msg);
                sendMessage(targetPl, tPrefix.replace("%player%", p.getName())+tSuffix+msgColor+msg);

                FriendsBG.getInstance().messages.remove(p);
                FriendsBG.getInstance().messages.remove(targetPl);
                FriendsBG.getInstance().messages.put(p, targetPl);
                FriendsBG.getInstance().messages.put(targetPl, p);
            }
        }
    }

    private void sendMessage(ProxiedPlayer p, String s) {
        p.sendMessage(new TextComponent(s));
    }

    private void sendDeniedUsage(CommandSender sender) {
        sender.sendMessage(new TextComponent(cmdNotUsable));
    }
}
