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
    private final String enableHF;
    private final String disableHF;
    private final String acceptHF;
    private final String refuseHF;
    private final String addHF;
    private final String removeHF;
    private final String listHF;

    // Help /party
    private final String groupTop;
    private final String groupBottom;
    private final String createHG;
    private final String deleteHG;
    private final String acceptHG;
    private final String refuseHG;
    private final String addHG;
    private final String removeHG;
    private final String tpHG;
    private final String listHG;
    private final String ownerHG;
    private final String enableHG;
    private final String disableHG;

    // Help /msg | /r
    private final String msgTop;
    private final String msgBottom;
    private final String mpHMSG;
    private final String rHMSG;
    private final String enableHMSG;
    private final String disableHMSG;

    // Help /report
    private final String reportAdm;
    private final String reportTop;
    private final String reportBottom;
    private final String reportHRPT;
    private final String removeHRPT;
    private final String listHRPT;

    public Help()
    {
        this.helpMsg = "helpMsg.";
        Configuration msgConfig = FriendsBG.getInstance().getMsgConfig();

        // Help /friends
        this.friendsTop = msgConfig.getString(helpMsg+"helpFriends.top").replace("&", "§");
        this.friendsBottom = msgConfig.getString(helpMsg+"helpFriends.bottom").replace("&", "§");
        this.enableHF = msgConfig.getString(helpMsg+"helpFriends.enable").replace("&", "§");
        this.disableHF = msgConfig.getString(helpMsg+"helpFriends.disable").replace("&", "§");
        this.acceptHF = msgConfig.getString(helpMsg+"helpFriends.accept").replace("&", "§");
        this.refuseHF = msgConfig.getString(helpMsg+"helpFriends.refuse").replace("&", "§");
        this.addHF = msgConfig.getString(helpMsg+"helpFriends.add").replace("&", "§");
        this.removeHF = msgConfig.getString(helpMsg+"helpFriends.remove").replace("&", "§");
        this.listHF = msgConfig.getString(helpMsg+"helpFriends.list").replace("&", "§");

        // Help /group
        this.groupTop = msgConfig.getString(helpMsg+"helpGroup.top").replace("&", "§");
        this.groupBottom = msgConfig.getString(helpMsg+"helpGroup.bottom").replace("&", "§");
        this.createHG = msgConfig.getString(helpMsg+"helpGroup.create").replace("&", "§");
        this.deleteHG = msgConfig.getString(helpMsg+"helpGroup.delete").replace("&", "§");
        this.acceptHG = msgConfig.getString(helpMsg+"helpGroup.accept").replace("&", "§");
        this.refuseHG = msgConfig.getString(helpMsg+"helpGroup.refuse").replace("&", "§");
        this.addHG = msgConfig.getString(helpMsg+"helpGroup.add").replace("&", "§");
        this.removeHG = msgConfig.getString(helpMsg+"helpGroup.remove").replace("&", "§");
        this.tpHG = msgConfig.getString(helpMsg+"helpGroup.tp").replace("&", "§");
        this.listHG = msgConfig.getString(helpMsg+"helpGroup.list").replace("&", "§");
        this.ownerHG = msgConfig.getString(helpMsg+"helpGroup.owner").replace("&", "§");
        this.enableHG = msgConfig.getString(helpMsg+"helpGroup.enable").replace("&", "§");
        this.disableHG = msgConfig.getString(helpMsg+"helpGroup.disable").replace("&", "§");

        // Help /msg | /r
        this.msgTop = msgConfig.getString(helpMsg+"helpPvMsg.top").replace("&", "§");
        this.msgBottom = msgConfig.getString(helpMsg+"helpPvMsg.bottom").replace("&", "§");
        this.mpHMSG = msgConfig.getString(helpMsg+"helpPvMsg.mp").replace("&", "§");
        this.rHMSG = msgConfig.getString(helpMsg+"helpPvMsg.r").replace("&", "§");
        this.enableHMSG = msgConfig.getString(helpMsg+"helpPvMsg.enable").replace("&", "§");
        this.disableHMSG = msgConfig.getString(helpMsg+"helpPvMsg.disable").replace("&", "§");

        // Help /report
        this.reportAdm = FriendsBG.getInstance().getConfig().getString("msg.report.admPerm");
        this.reportTop = msgConfig.getString(helpMsg+"helpReport.top").replace("&", "§");
        this.reportBottom = msgConfig.getString(helpMsg+"helpReport.bottom").replace("&", "§");
        this.reportHRPT = msgConfig.getString(helpMsg+"helpReport.report").replace("&", "§");
        this.listHRPT = msgConfig.getString(helpMsg+"helpReport.list").replace("&", "§");
        this.removeHRPT = msgConfig.getString(helpMsg+"helpReport.remove").replace("&", "§");
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

    public void helpGroup(ProxiedPlayer p)
    {
        sendMessage(p, " ");
        sendMessage(p, groupTop);
        sendMessage(p, "§6§l? §7§nHelp§f : ");
        sendMessage(p, " ");
        sendMessage(p, enableHG);
        sendMessage(p, disableHG);
        sendMessage(p, createHG);
        sendMessage(p, deleteHG);
        sendMessage(p, acceptHG);
        sendMessage(p, refuseHG);
        sendMessage(p, addHG);
        sendMessage(p, removeHG);
        sendMessage(p, tpHG);
        sendMessage(p, ownerHG);
        sendMessage(p, listHG);
        sendMessage(p, groupBottom);
    }

    public void helpMsg(ProxiedPlayer p)
    {
        sendMessage(p, " ");
        sendMessage(p, msgTop);
        sendMessage(p, " §6§l? §7§nHelp§f : ");
        sendMessage(p, " ");
        sendMessage(p, mpHMSG);
        sendMessage(p, rHMSG);
        sendMessage(p, enableHMSG);
        sendMessage(p, disableHMSG);
        sendMessage(p, msgBottom);
    }

    public void helpReport(ProxiedPlayer p)
    {
        sendMessage(p, " ");
        sendMessage(p, reportTop);
        sendMessage(p, " §6§l? §7§nHelp§f : ");
        sendMessage(p, " ");
        sendMessage(p, reportHRPT);
        if(p.hasPermission(reportAdm))
        {
            sendMessage(p, listHRPT);
            sendMessage(p, removeHRPT);
        }
        sendMessage(p, reportBottom);
    }

    private void sendMessage(ProxiedPlayer p, String s) {p.sendMessage(new TextComponent(s));}
}
