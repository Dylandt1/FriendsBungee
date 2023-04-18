package fr.patapom.fbg.cmd.utils;

import fr.patapom.fbg.FriendsBG;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class Help
{
    /**
     * Vars :
     */

    // General vars
    private final Configuration config;

    // Help /friends
    private final String friendsTop;
    private final String friendsBottom;
    private final String suffixHF;
    private final String enableHF;
    private final String disableHF;
    private final String acceptHF;
    private final String refuseHF;
    private final String addHF;
    private final String removeHF;
    private final String listHF;

    // Help /party
    private final String partyTop;
    private final String partyBottom;
    private final String suffixHP;
    private final String createHP;
    private final String deleteHP;
    private final String acceptHP;
    private final String refuseHP;
    private final String addHP;
    private final String removeHP;
    private final String tpHP;
    private final String listHP;
    private final String ownercmdHP;

    // Help /msg | /r
    private final String msgTop;
    private final String msgBottom;
    private final String suffixHMSG;
    private final String cmdMsgHMSG;
    private final String cmdRHMSG;
    private final String cmdEnableHMSG;
    private final String cmdDisableHMSG;

    public Help()
    {
        this.config = FriendsBG.getInstance().getConfig();
        final String helpmsg = "helpmsg.";

        // Help /friends
        this.friendsTop = config.getString(helpmsg+"helpFriends.top").replace("&", "§");
        this.friendsBottom = config.getString(helpmsg+"helpFriends.bottom").replace("&", "§");
        this.suffixHF = config.getString(helpmsg+"helpFriends.sHelp").replace("&", "§");
        this.enableHF = config.getString(helpmsg+"helpFriends.enable").replace("&", "§");
        this.disableHF = config.getString(helpmsg+"helpFriends.disable").replace("&", "§");
        this.acceptHF = config.getString(helpmsg+"helpFriends.accept").replace("&", "§");
        this.refuseHF = config.getString(helpmsg+"helpFriends.refuse").replace("&", "§");
        this.addHF = config.getString(helpmsg+"helpFriends.add").replace("&", "§");
        this.removeHF = config.getString(helpmsg+"helpFriends.remove").replace("&", "§");
        this.listHF = config.getString(helpmsg+"helpFriends.list").replace("&", "§");

        // Help /party
        this.partyTop = config.getString(helpmsg+"helpParty.top").replace("&", "§");
        this.partyBottom = config.getString(helpmsg+"helpParty.bottom").replace("&", "§");
        this.suffixHP = config.getString(helpmsg+"helpParty.sHelp").replace("&", "§");
        this.createHP = config.getString(helpmsg+"helpParty.create").replace("&", "§");
        this.deleteHP = config.getString(helpmsg+"helpParty.delete").replace("&", "§");
        this.acceptHP = config.getString(helpmsg+"helpParty.accept").replace("&", "§");
        this.refuseHP = config.getString(helpmsg+"helpParty.refuse").replace("&", "§");
        this.addHP = config.getString(helpmsg+"helpParty.add").replace("&", "§");
        this.removeHP = config.getString(helpmsg+"helpParty.remove").replace("&", "§");
        this.tpHP = config.getString(helpmsg+"helpParty.tp").replace("&", "§");
        this.listHP = config.getString(helpmsg+"helpParty.list").replace("&", "§");
        this.ownercmdHP = config.getString(helpmsg+"helpParty.owner").replace("&", "§");

        // Help /msg | /r
        this.msgTop = config.getString(helpmsg+"helpPvMsg.top").replace("&", "§");
        this.msgBottom = config.getString(helpmsg+"helpPvMsg.bottom").replace("&", "§");
        this.suffixHMSG = config.getString(helpmsg+"helpPvMsg.sHelp").replace("&", "§");
        this.cmdMsgHMSG = config.getString(helpmsg+"helpPvMsg.cmdMsg").replace("&", "§");
        this.cmdRHMSG = config.getString(helpmsg+"helpPvMsg.cmdR").replace("&", "§");
        this.cmdEnableHMSG = config.getString(helpmsg+"helpPvMsg.cmdEnable").replace("&", "§");
        this.cmdDisableHMSG = config.getString(helpmsg+"helpPvMsg.cmdDisable").replace("&", "§");
    }

    public void helpFriends(ProxiedPlayer p)
    {
        sendMessage(p, " ");
        sendMessage(p, friendsTop);
        sendMessage(p, " ");
        sendMessage(p, " §6§l? §7§nHelp§f : ");
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bf §fenable "+suffixHF+" "+enableHF);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bf §fdisable "+suffixHF+" "+disableHF);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bf §faccept "+suffixHF+" "+acceptHF);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bf §frefuse "+suffixHF+" "+refuseHF);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bf §fadd §7<§6player§7> "+suffixHF+" "+addHF);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bf §fremove §7<§6player§7> "+suffixHF+" "+removeHF);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bf §flist "+suffixHF+" "+listHF);
        sendMessage(p, " ");
        sendMessage(p, friendsBottom);
    }

    public void helpParty(ProxiedPlayer p)
    {
        sendMessage(p, " ");
        sendMessage(p, partyTop);
        sendMessage(p, " ");
        sendMessage(p, "§6§l? §7§nHelp§f : ");
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bparty §fcreate "+suffixHP+" "+createHP);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bparty §fdelete "+suffixHP+" "+deleteHP);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bparty §faccept "+suffixHP+" "+acceptHP);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bparty §frefuse "+suffixHP+" "+refuseHP);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bparty §fadd §7<§6player§7> "+suffixHP+" "+addHP);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bparty §fremove §7<§6player§7> "+suffixHP+" "+removeHP);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bparty §ftp §7<§aenable §f| §cdisable §7> "+suffixHP+" "+tpHP);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bparty §fowner §7<§6player§7> "+suffixHP+" "+ownercmdHP);
        sendMessage(p, " ");
        sendMessage(p, "§c- §f/§bparty §flist "+suffixHP+" "+listHP);
        sendMessage(p, " ");
        sendMessage(p, partyBottom);
    }

    public void helpMsg(ProxiedPlayer p)
    {
        sendMessage(p, " ");
        sendMessage(p, msgTop);
        sendMessage(p, " ");
        sendMessage(p, " §6§l? §7§nHelp§f : ");
        sendMessage(p, " ");
        sendMessage(p, "§f/§bmsg §7<§6player§7> §7<§fmessage§7> "+suffixHMSG+" "+cmdMsgHMSG);
        sendMessage(p, " ");
        sendMessage(p, "§f/§br §7<§fmessage§7> "+suffixHMSG+" "+cmdRHMSG);
        sendMessage(p, " ");
        sendMessage(p, "§f/§bmsg enable "+suffixHMSG+" "+cmdEnableHMSG);
        sendMessage(p, " ");
        sendMessage(p, "§f/§bmsg disable "+suffixHMSG+" "+cmdDisableHMSG);
        sendMessage(p, " ");
        sendMessage(p, msgBottom);
    }

    private void sendMessage(ProxiedPlayer p, String s) {p.sendMessage(new TextComponent(s));}
}
