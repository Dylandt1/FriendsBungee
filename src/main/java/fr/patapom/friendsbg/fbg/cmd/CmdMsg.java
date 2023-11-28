package fr.patapom.friendsbg.fbg.cmd;

import fr.patapom.friendsbg.common.players.ProfileManager;
import fr.patapom.friendsbg.common.players.ProfileProvider;
import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.patapom.friendsbg.fbg.cmd.utils.Help;
import fr.tmmods.tmapi.exceptions.ManagerNotFoundException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

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

    private final Configuration config;
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
    private final boolean antiSpam;
    private final int cool_down;
    private final String antiSpamMsg;

    public CmdMsg()
    {
        super("message", null, new String[]{"msg", "tell", "w", "mp"});
        this.config = FriendsBG.getInstance().getConfig();
        this.sPrefix = config.getString("msg.sender.prefix").replace("&", "§");
        this.sdPrefix = config.getString("msg.sender.2ndPrefix").replace("&", "§");
        this.sSuffix = config.getString("msg.sender.suffix").replace("&", "§");
        this.sdSuffix = config.getString("msg.sender.2ndSuffix").replace("&", "§");
        this.tPrefix = config.getString("msg.target.prefix").replace("&", "§");
        this.tSuffix = config.getString("msg.target.suffix").replace("&", "§");
        this.msgColor = config.getString("msg.messageColor").replace("&", "§");
        this.sendToSender = config.getString("msg.sendToSender").replace("&", "§");
        this.tooLong = config.getString("msg.tooLong").replace("&", "§");
        this.playerNotFound = config.getString("msg.playerNotFound").replace("&", "§");
        this.cmdNotUsable = config.getString("msg.cmdNotUsable").replace("&", "§");
        this.msgEnabled = config.getString("msg.msgEnabled").replace("&", "§");
        this.msgDisabled = config.getString("msg.msgDisabled").replace("&", "§");
        this.msgAlreadyEnabled = config.getString("msg.msgAlreadyEnabled").replace("&", "§");
        this.msgAlreadyDisabled = config.getString("msg.msgAlreadyDisabled").replace("&", "§");
        this.msgSenderDisabled = config.getString("msg.msgSenderDisabled").replace("&", "§");
        this.msgTargetDisabled = config.getString("msg.msgTargetDisabled").replace("&", "§");
        this.errorMsg = config.getString("msg.errorMsg").replace("&", "§");
        this.reportCmd = config.getString("msg.reportCmd").replace("&", "§");
        this.antiSpamMsg = config.getString("msg.antiSpam.message").replace("&", "§");
        this.antiSpam = config.getBoolean("msg.antiSpam.use");
        this.cool_down = config.getInt("msg.antiSpam.cool_down")*1000;
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
            for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
            {
                list.add(player.getName());
            }
            return list;
        }
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(!(sender instanceof ProxiedPlayer)) {sendDeniedUsage(sender);return;}

        ProxiedPlayer p = (ProxiedPlayer) sender;
        ProfileProvider provider = new ProfileProvider(p.getUniqueId());
        ProfileManager profile;
        try {
            profile = provider.getFManager();
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
                provider.save(profile);
                sendMessage(p, msgEnabled);
            }else if(args[0].equalsIgnoreCase("disable"))
            {
                if(!profile.msgAllow()) {sendMessage(p, msgAlreadyDisabled);return;}

                profile.setMsgAllow(false);
                provider.save(profile);
                sendMessage(p, msgDisabled);
            }
        }else
        {
            if(!profile.msgAllow()) {sendMessage(p, msgSenderDisabled.replace("%cmd%", "/mp enable"));return;}

            String targetName = args[0];
            if(ProxyServer.getInstance().getPlayer(targetName) == null) {sendMessage(p, playerNotFound);return;}

            ProxiedPlayer targetPl = ProxyServer.getInstance().getPlayer(targetName);
            ProfileProvider targetProvider = new ProfileProvider(targetPl.getUniqueId());
            ProfileManager targetProfile;
            try {
                targetProfile = targetProvider.getFManager();
            } catch (ManagerNotFoundException e) {
                throw new RuntimeException(e);
            }

            if(targetProfile == null) {sendMessage(p, errorMsg);return;}

            if(targetPl == p)
            {
                sendMessage(p, sendToSender);
            }else {
                if(!targetProfile.msgAllow()) {sendMessage(p, msgTargetDisabled.replace("%targetPlayer%", targetPl.getDisplayName()));return;}

                if(antiSpam)
                {
                    if(!FriendsBG.getInstance().cooldown.containsKey(p.getUniqueId()))
                    {
                        FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                    }else if(System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()) > cool_down)
                    {
                        FriendsBG.getInstance().cooldown.remove(p.getUniqueId());
                        FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                    }
                    else {
                        sendMessage(p, antiSpamMsg.replace("%cooldown%", String.valueOf(cool_down - (System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId())) / 1000)));
                        return;
                    }
                }

                StringBuilder msg = new StringBuilder();

                if(args.length > msg.capacity()) {sendMessage(p, tooLong);return;}

                for(int i = 1; i != args.length; i++)
                {
                    msg.append(args[i].replace("&", "§")).append(" ");
                }

                final String part1 = sPrefix+" "+sSuffix+" ";
                final String part2 = sdPrefix.replace("%targetPlayer%", targetName)+" "+sdSuffix+" ";
                p.sendMessage(new TextComponent(part1+part2+msgColor+msg));
                targetPl.sendMessage(new TextComponent(tPrefix.replace("%player%", p.getName())+" "+tSuffix+" "+msgColor+msg));

                FriendsBG.messages.remove(p);
                FriendsBG.messages.remove(targetPl);
                FriendsBG.messages.put(p, targetPl);
                FriendsBG.messages.put(targetPl, p);
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
