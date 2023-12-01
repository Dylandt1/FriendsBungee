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
import java.util.UUID;

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
        this.prefix = config.getString("msg.report.prefix").replace("&", "§");
        this.suffix = config.getString("msg.report.suffix").replace("&", "§");
        this.admMessage = config.getString("msg.report.admMessage").replace("&", "§");
        this.reportConfirmation = config.getString("msg.report.reportConfirmation").replace("&", "§");
        this.cmdNotUsable = config.getString("msg.cmdNotUsable").replace("&", "§");
        this.tooLong = config.getString("msg.tooLong").replace("&", "§");
        this.reportDeleted = config.getString("msg.report.reportDeleted").replace("&", "§");
        this.cmdNotFound = config.getString("cmdNotFound").replace("&", "§");
        this.admPerm = config.getString("msg.report.admPerm");
        this.keyForm = config.getString("msg.report.keyForm").replace("&", "§");
        this.allReportsDeleted = config.getString("msg.report.allReportsDeleted").replace("&", "§");
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
