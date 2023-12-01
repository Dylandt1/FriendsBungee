package fr.patapom.friendsbg.fbg.cmd.utils;

import fr.patapom.friendsbg.fbg.FriendsBG;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.List;
import java.util.Map;

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

public class Help
{
    /**
     * Vars :
     */
    private final String helpMsg;

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
    private final String enableHP;
    private final String disableHP;

    // Help /msg | /r
    private final String msgTop;
    private final String msgBottom;
    private final String suffixHMSG;
    private final String cmdMsgHMSG;
    private final String cmdRHMSG;
    private final String cmdEnableHMSG;
    private final String cmdDisableHMSG;
    private final String cmdReportHMSG;

    private final Configuration config;

    public Help()
    {
        this.helpMsg = "helpMsg.";
        this.config = FriendsBG.getInstance().getConfig();

        // Help /friends
        this.friendsTop = config.getString(helpMsg+"helpFriends.top").replace("&", "§");
        this.friendsBottom = config.getString(helpMsg+"helpFriends.bottom").replace("&", "§");
        this.suffixHF = config.getString(helpMsg+"helpFriends.sHelp").replace("&", "§");
        this.enableHF = config.getString(helpMsg+"helpFriends.enable").replace("&", "§");
        this.disableHF = config.getString(helpMsg+"helpFriends.disable").replace("&", "§");
        this.acceptHF = config.getString(helpMsg+"helpFriends.accept").replace("&", "§");
        this.refuseHF = config.getString(helpMsg+"helpFriends.refuse").replace("&", "§");
        this.addHF = config.getString(helpMsg+"helpFriends.add").replace("&", "§");
        this.removeHF = config.getString(helpMsg+"helpFriends.remove").replace("&", "§");
        this.listHF = config.getString(helpMsg+"helpFriends.list").replace("&", "§");

        // Help /party
        this.partyTop = config.getString(helpMsg+"helpParty.top").replace("&", "§");
        this.partyBottom = config.getString(helpMsg+"helpParty.bottom").replace("&", "§");
        this.suffixHP = config.getString(helpMsg+"helpParty.sHelp").replace("&", "§");
        this.createHP = config.getString(helpMsg+"helpParty.create").replace("&", "§");
        this.deleteHP = config.getString(helpMsg+"helpParty.delete").replace("&", "§");
        this.acceptHP = config.getString(helpMsg+"helpParty.accept").replace("&", "§");
        this.refuseHP = config.getString(helpMsg+"helpParty.refuse").replace("&", "§");
        this.addHP = config.getString(helpMsg+"helpParty.add").replace("&", "§");
        this.removeHP = config.getString(helpMsg+"helpParty.remove").replace("&", "§");
        this.tpHP = config.getString(helpMsg+"helpParty.tp").replace("&", "§");
        this.listHP = config.getString(helpMsg+"helpParty.list").replace("&", "§");
        this.ownercmdHP = config.getString(helpMsg+"helpParty.owner").replace("&", "§");
        this.enableHP = config.getString(helpMsg+"helpParty.enable").replace("&", "§");
        this.disableHP = config.getString(helpMsg+"helpParty.disable").replace("&", "§");

        // Help /msg | /r
        this.msgTop = config.getString(helpMsg+"helpPvMsg.top").replace("&", "§");
        this.msgBottom = config.getString(helpMsg+"helpPvMsg.bottom").replace("&", "§");
        this.suffixHMSG = config.getString(helpMsg+"helpPvMsg.sHelp").replace("&", "§");
        this.cmdMsgHMSG = config.getString(helpMsg+"helpPvMsg.cmdMsg").replace("&", "§");
        this.cmdRHMSG = config.getString(helpMsg+"helpPvMsg.cmdR").replace("&", "§");
        this.cmdEnableHMSG = config.getString(helpMsg+"helpPvMsg.cmdEnable").replace("&", "§");
        this.cmdDisableHMSG = config.getString(helpMsg+"helpPvMsg.cmdDisable").replace("&", "§");
        this.cmdReportHMSG = config.getString(helpMsg+"helpPvMsg.cmdReport").replace("&", "§");
    }

    public void helpFriends(ProxiedPlayer p)
    {
        sendMessage(p, " ");
        sendMessage(p, friendsTop);
        sendMessage(p, " §6§l? §7§nHelp§f : ");
        sendMessage(p, " ");
        sendMessage(p, enableHF);
        sendMessage(p, disableHF);
        sendMessage(p, acceptHF);
        sendMessage(p, refuseHF);
        sendMessage(p, addHF);
        sendMessage(p, removeHF);
        sendMessage(p, listHF);
        sendMessage(p, friendsBottom);
    }

    public void helpParty(ProxiedPlayer p)
    {
        sendMessage(p, " ");
        sendMessage(p, partyTop);
        sendMessage(p, "§6§l? §7§nHelp§f : ");
        sendMessage(p, " ");
        sendMessage(p, enableHP);
        sendMessage(p, disableHP);
        sendMessage(p, createHP);
        sendMessage(p, deleteHP);
        sendMessage(p, acceptHP);
        sendMessage(p, refuseHP);
        sendMessage(p, addHP);
        sendMessage(p, removeHP);
        sendMessage(p, tpHP);
        sendMessage(p, ownercmdHP);
        sendMessage(p, listHP);
        sendMessage(p, partyBottom);
    }

    public void helpMsg(ProxiedPlayer p)
    {
        sendMessage(p, " ");
        sendMessage(p, msgTop);
        sendMessage(p, " §6§l? §7§nHelp§f : ");
        sendMessage(p, " ");
        sendMessage(p, cmdMsgHMSG);
        sendMessage(p, cmdRHMSG);
        sendMessage(p, cmdEnableHMSG);
        sendMessage(p, cmdDisableHMSG);
        sendMessage(p, cmdReportHMSG);
        sendMessage(p, msgBottom);
    }

    private void sendMessage(ProxiedPlayer p, String s) {p.sendMessage(new TextComponent(s));}
}
