package fr.patapom.friendsbg.fbg.cmd;

import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.patapom.friendsbg.fbg.cmd.utils.Help;
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

public class CmdReport extends Command implements TabExecutor
{
    private final Help H = new Help();

    private final Configuration config;
    private final String prefix;
    private final String suffix;
    private final String admMessage;
    private final String reportConfirmation;
    private final String cmdNotUsable;
    private final String tooLong;
    private final String reportDeleted;
    private final String cmdNotFound;
    private final String admPerm;
    private final String keyForm;
    private final String allReportsDeleted;

    public CmdReport()
    {
        super("report", null, FriendsBG.getInstance().getConfig().getStringList("msg.cmdAlias.report").toArray(new String[0]));
        this.config = FriendsBG.getInstance().getConfig();
        Configuration msgConfig = FriendsBG.getInstance().getMsgConfig();
        this.prefix = msgConfig.getString("msg.report.prefix").replace("&", "§");
        this.suffix = msgConfig.getString("msg.report.suffix").replace("&", "§");
        this.admMessage = msgConfig.getString("msg.report.admMessage").replace("&", "§");
        this.reportConfirmation = msgConfig.getString("msg.report.reportConfirmation").replace("&", "§");
        this.cmdNotUsable = msgConfig.getString("msg.cmdNotUsable").replace("&", "§");
        this.tooLong = msgConfig.getString("msg.tooLong").replace("&", "§");
        this.reportDeleted = msgConfig.getString("msg.report.reportDeleted").replace("&", "§");
        this.cmdNotFound = msgConfig.getString("cmdNotFound").replace("&", "§");
        this.admPerm = msgConfig.getString("msg.report.admPerm");
        this.keyForm = msgConfig.getString("msg.report.keyForm").replace("&", "§");
        this.allReportsDeleted = msgConfig.getString("msg.report.allReportsDeleted").replace("&", "§");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args)
    {
        if(!(sender instanceof ProxiedPlayer p)) {return null;}

        List<String> list = new ArrayList<>();

        if(args.length == 1)
        {
            list.addAll(config.getStringList("msg.report.reasons"));

            for(ProxiedPlayer pls : ProxyServer.getInstance().getPlayers())
            {
                list.add(pls.getName());
            }

            if(p.hasPermission(admPerm))
            {
                list.add("list");
                list.add("remove");
            }

        }else if(args.length == 2)
        {
            list.add("<message>");

            if(p.hasPermission(admPerm))
            {
                if(args[0].equalsIgnoreCase("remove"))
                {
                    list.add("all");
                    for(int key : FriendsBG.reports.keySet())
                    {
                        list.add(String.valueOf(key));
                    }
                }
            }
        }
        return list;
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(!(sender instanceof ProxiedPlayer p)) {sendDeniedUsage(sender);return;}

        if(args.length == 1)
        {
            if(args[0].equalsIgnoreCase("list"))
            {
                if(p.hasPermission(admPerm))
                {
                    for(int key : FriendsBG.reports.keySet())
                    {
                        sendMessage(p, " ");
                        sendMessage(p, keyForm.replace("%key%", String.valueOf(key))+FriendsBG.reports.get(key));
                    }
                }else {

                    StringBuilder sb = new StringBuilder();
                    for(String arg : args) {sb.append(arg).append(" ");}
                    sendMessage(p, cmdNotFound.replace("%cmd%", this.getName()).replace("%args%", sb));
                }
            }
        }else if(args.length == 2)
        {
            if(args[0].equalsIgnoreCase("remove"))
            {
                if(p.hasPermission(admPerm))
                {
                    if(FriendsBG.reports.containsKey(Integer.parseInt(args[1])))
                    {
                        FriendsBG.reports.remove(Integer.parseInt(args[1]));
                        sendMessage(p, prefix+suffix+reportDeleted.replace("%report%", args[1].toString()));
                    }else if(args[1].equalsIgnoreCase("all"))
                    {
                        FriendsBG.reports.clear();
                        sendMessage(p, prefix+suffix+allReportsDeleted);
                    }
                }else {
                    StringBuilder sb = new StringBuilder();
                    for(String arg : args) {sb.append(arg).append(" ");}
                    sendMessage(p, cmdNotFound.replace("%cmd%", this.getName()).replace("%args%", sb));
                }
            }else if(config.getStringList("msg.report.reasons").contains(args[0])
                    || ProxyServer.getInstance().getPlayer(args[0]) != null)
            {
                int id = FriendsBG.reports.size()+1;

                String reason = args[0];
                StringBuilder report = new StringBuilder();

                if(args.length > report.capacity()) {sendMessage(p, prefix+suffix+tooLong);return;}

                report.append("§f[§6").append(p.getName()).append("§7] §f: §7(§c")
                        .append(reason).append("§7) §f§l-> §f");

                for(int i = 1; i != args.length; i++)
                {
                    report.append(args[i].replace("&", "§")).append(" ");
                }

                FriendsBG.reports.put(id, report.toString());
                sendMessage(p, prefix+suffix+reportConfirmation);

                for(ProxiedPlayer pl : ProxyServer.getInstance().getPlayers())
                {
                    if(pl.hasPermission(admPerm)) {sendMessage(pl, prefix+suffix+admMessage.replace("%sender%", p.getName()));}
                }
            }else {
                StringBuilder sb = new StringBuilder();
                for(String arg : args) {sb.append(arg).append(" ");}
                sendMessage(p, cmdNotFound.replace("%cmd%", this.getName()).replace("%args%", sb));
            }
        }else {
            H.helpReport(p);
        }
    }

    private void sendMessage(ProxiedPlayer p, String s) {p.sendMessage(new TextComponent(s));}

    private void sendDeniedUsage(CommandSender sender) {sender.sendMessage(new TextComponent(cmdNotUsable));}
}
