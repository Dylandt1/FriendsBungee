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
import java.util.List;

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

public class CmdResend extends Command implements TabExecutor
{
    private final Help H = new Help();

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
    private final String msgSenderDisabled;
    private final String msgTargetDisabled;
    private final String errorMsg;
    private final String reportCmd;

    public CmdResend()
    {
        super("resend", null, FriendsBG.getInstance().getConfig().getStringList("msg.cmdAlias.resend").toArray(new String[0]));
        Configuration msgConfig = FriendsBG.getInstance().getMsgConfig();
        this.playerOffline = msgConfig.getString("msg.playerOffline").replace("&", "§");
        this.cmdNotUsable = msgConfig.getString("msg.cmdNotUsable").replace("&", "§");
        this.noMessage = msgConfig.getString("msg.noMessage").replace("&", "§");
        this.sPrefix = msgConfig.getString("msg.sender.prefix").replace("&", "§");
        this.sdPrefix = msgConfig.getString("msg.sender.2ndPrefix").replace("&", "§");
        this.sSuffix = msgConfig.getString("msg.sender.suffix").replace("&", "§");
        this.sdSuffix = msgConfig.getString("msg.sender.2ndSuffix").replace("&", "§");
        this.tPrefix = msgConfig.getString("msg.target.prefix").replace("&", "§");
        this.tSuffix = msgConfig.getString("msg.target.suffix").replace("&", "§");
        this.msgColor = msgConfig.getString("msg.messageColor").replace("&", "§");
        this.tooLong = msgConfig.getString("msg.tooLong").replace("&", "§");
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
            list.add("<message>");
            return list;
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
        }else {
            if(!profile.msgAllow()) {sendMessage(p, msgSenderDisabled.replace("%cmd%", "/message enable"));return;}
            if(!FriendsBG.getInstance().messages.containsKey(p)) {sendMessage(p, noMessage);return;}
            if(!FriendsBG.getInstance().messages.get(p).isConnected())
            {
                sendMessage(p, playerOffline.replace("%targetPlayer%", FriendsBG.getInstance().messages.get(p).getName()));
                return;
            }

            ProxiedPlayer targetPl = FriendsBG.getInstance().messages.get(p);
            ProfileProvider targetProvider = new ProfileProvider(targetPl.getUniqueId());
            ProfileManager targetProfile;
            try {
                targetProfile = targetProvider.getPManager();
            } catch (ManagerNotFoundException e) {
                throw new RuntimeException(e);
            }

            if(targetProfile == null) {sendMessage(p, errorMsg);return;}
            if(!targetProfile.msgAllow()) {sendMessage(p, msgTargetDisabled.replace("%targetPlayer%", targetPl.getName()));return;}

            StringBuilder msg = new StringBuilder();

            if(args.length > msg.capacity()) {sendMessage(p, tooLong);return;}

            for(int i = 0; i != args.length; i++)
            {
                msg.append(args[i].replace("&", "§")).append(" ");
            }

            final String part1 = sPrefix+sSuffix;
            final String part2 = sdPrefix.replace("%targetPlayer%", targetPl.getName())+sdSuffix;
            p.sendMessage(new TextComponent(part1+part2+msgColor+msg));
            targetPl.sendMessage(new TextComponent(tPrefix.replace("%player%", p.getName())+tSuffix+msgColor+msg));

            FriendsBG.getInstance().messages.remove(p);
            FriendsBG.getInstance().messages.remove(targetPl);
            FriendsBG.getInstance().messages.put(p, targetPl);
            FriendsBG.getInstance().messages.put(targetPl, p);
        }
    }

    private void sendMessage(ProxiedPlayer p, String s) {p.sendMessage(new TextComponent(s));}

    private void sendDeniedUsage(CommandSender sender) {sender.sendMessage(new TextComponent(cmdNotUsable));}
}
