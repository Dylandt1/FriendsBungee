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
    private final String alreadySend;
    private final String reportDeleted;

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
        this.alreadySend = config.getString("msg.report.alreadySend").replace("&", "§");
        this.reportDeleted = config.getString("msg.report.reportDeleted").replace("&", "§");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args)
    {
        if(!(sender instanceof ProxiedPlayer p)) {return null;}

        List<String> list = new ArrayList<>();

        if(args.length == 1)
        {
            list.addAll(config.getStringList("helpMsg.helpPvMsg.reportReasons"));

            for(ProxiedPlayer pls : ProxyServer.getInstance().getPlayers())
            {
                list.add(pls.getName());
            }

            list.add("list");
            list.add("remove");
        }else if(args.length == 2)
        {
            for(ProxiedPlayer pls : ProxyServer.getInstance().getPlayers())
            {
                if(FriendsBG.reports.containsKey(pls.getUniqueId())) list.add(pls.getName());
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
                for(UUID key : FriendsBG.reports.keySet())
                {
                    sendMessage(p, " ");
                    sendMessage(p, FriendsBG.reports.get(key));
                }
            }
        }else if(args.length == 2)
        {
            if(args[0].equalsIgnoreCase("remove"))
            {
                if(ProxyServer.getInstance().getPlayer(args[1]) != null)
                {
                    ProxiedPlayer pl = ProxyServer.getInstance().getPlayer(args[1]);
                    if(FriendsBG.reports.containsKey(pl.getUniqueId()))
                    {
                        FriendsBG.reports.remove(pl.getUniqueId());
                        sendMessage(p, prefix+suffix+reportDeleted);
                    }
                }
            }
        }else if(args.length > 2)
        {
            if(FriendsBG.reports.containsKey(p.getUniqueId()))
            {
                sendMessage(p, prefix+suffix+alreadySend);
                return;
            }

            if(config.getStringList("helpMsg.helpPvMsg.reportReasons").contains(args[0])
                    || ProxyServer.getInstance().getPlayer(args[0]) != null)
            {
                String reason = args[0];
                StringBuilder report = new StringBuilder();

                if(args.length > report.capacity()) {sendMessage(p, prefix+suffix+tooLong);return;}

                report.append("§f[§6").append(p.getName()).append("§7] §f: §7(§c")
                        .append(reason).append("§7) §f§l-> §f");

                for(int i = 1; i != args.length; i++)
                {
                    report.append(args[i].replace("&", "§")).append(" ");
                }

                FriendsBG.reports.put(p.getUniqueId(), report.toString());
                sendMessage(p, prefix+suffix+reportConfirmation);
                sendMessage(p, prefix+suffix+"nb reports in list cache : "+FriendsBG.reports.size());

                for(ProxiedPlayer pls : ProxyServer.getInstance().getPlayers())
                {
                    if(pls.hasPermission("frb.report.admin")) {sendMessage(pls, prefix+suffix+admMessage.replace("%sender%", p.getName()));}
                }
            }else {
                H.helpMsg(p);
            }
        }else {
            H.helpMsg(p);
        }
    }

    private void sendMessage(ProxiedPlayer p, String s) {p.sendMessage(new TextComponent(s));}

    private void sendDeniedUsage(CommandSender sender) {sender.sendMessage(new TextComponent(cmdNotUsable));}
}
