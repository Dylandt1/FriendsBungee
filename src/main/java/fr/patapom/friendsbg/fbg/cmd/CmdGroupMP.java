package fr.patapom.friendsbg.fbg.cmd;

import fr.patapom.friendsbg.common.groups.GroupManager;
import fr.patapom.friendsbg.common.groups.GroupProvider;
import fr.patapom.friendsbg.common.players.ProfileManager;
import fr.patapom.friendsbg.common.players.ProfileProvider;
import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.patapom.friendsbg.fbg.cmd.utils.Help;
import fr.tmmods.tmapi.exceptions.ManagerNotFoundException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class CmdGroupMP extends Command implements TabExecutor
{
    private final Help H = new Help();

    private final String prefix;
    private final String suffix;
    private final String cmdNotUsable;
    private final String notInGroup;

    // Private chat constants
    private final String mpPrefix;
    private final String mpSuffix;
    private final String sPrefix;
    private final String sSuffix;
    private final String tPrefix;
    private final String tSuffix;
    private final String msgColor;
    private final String tooLong;

    public CmdGroupMP()
    {
        super("groupmp", null, FriendsBG.getInstance().getConfig().getStringList("groups.msg.cmdAlias").toArray(new String[0]));

        Configuration msgConfig = FriendsBG.getInstance().getMsgConfig();
        this.prefix = msgConfig.getString("groups.prefix").replace("&", "§");
        this.suffix = msgConfig.getString("groups.suffix").replace("&", "§");
        this.cmdNotUsable = msgConfig.getString("groups.msg.cmdNotUsable").replace("&", "§");
        this.notInGroup = msgConfig.getString("groups.notInGroup").replace("&", "§");

        // Set private chat constants
        this.mpPrefix = msgConfig.getString("groups.msg.prefix").replace("&", "§");
        this.mpSuffix = msgConfig.getString("groups.msg.suffix").replace("&", "§");
        this.sPrefix = msgConfig.getString("groups.msg.sender.prefix").replace("&", "§");
        this.sSuffix = msgConfig.getString("groups.msg.sender.suffix").replace("&", "§");
        this.tPrefix = msgConfig.getString("groups.msg.target.prefix").replace("&", "§");
        this.tSuffix = msgConfig.getString("groups.msg.target.suffix").replace("&", "§");
        this.msgColor = msgConfig.getString("groups.msg.messageColor").replace("&", "§");
        this.tooLong = msgConfig.getString("groups.msg.tooLong").replace("&", "§");
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
        } catch (ManagerNotFoundException e)
        {
            e.printStackTrace();
            return;
        }

        if(args.length == 0)
        {
            H.helpGroupMp(p);
        }else
        {
            if(profile.isInGroup())
            {
                GroupProvider groupProvider = new GroupProvider(profile.getGroupId());

                if(!groupProvider.gExist())
                {
                    profile.setGroupId(null);
                    profileProvider.save(profile);
                    sendMessage(p, prefix+suffix+notInGroup);
                    return;
                }

                GroupManager group;
                try {
                    group = groupProvider.getGManager();
                } catch (ManagerNotFoundException e) {
                    throw new RuntimeException(e);
                }

                StringBuilder msg = new StringBuilder();

                if(args.length > msg.capacity()) {sendMessage(p, tooLong);return;}

                for(int i = 0; i != args.length; i++)
                {
                    msg.append(args[i].replace("&", "§")).append(" ");
                }

                final String part1 = mpPrefix+mpSuffix;
                final String part2 = sPrefix+sSuffix;

                sendMessage(p, part1+part2+msgColor+msg);

                for(ProxiedPlayer pl : group.getPlayersInGroup())
                {
                    if(!pl.equals(p)) {sendMessage(pl, part1+tPrefix.replace("%player%", p.getName())+tSuffix+msgColor+msg);}
                }
            }else {
                profileProvider.save(profile);
                sendMessage(p, prefix+suffix+notInGroup);
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
