package fr.patapom.friendsbg.fbg.utils;

import fr.patapom.friendsbg.fbg.FriendsBG;
import net.md_5.bungee.config.Configuration;

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

public class Constants
{
    /**
     * This class is under development and is juste use for test !!!
     */

    private Configuration config = FriendsBG.getInstance().getConfig();

    /**
     * General constant vars
     */

    public String pluginConsole = "§f[§bFriends§6Bungee§f]";
    public String friendsConsole = "§f[§bFriends§f]";
    public String groupConsole = "§f[§2Group§f]";

    /**
     * Constant vars /friends command
     */

    // Completion variables
    public String cmdFriendsComp1 = "help";
    public String cmdFriendsComp2 = "enable";
    public String cmdFriendsComp3 = "disable";
    public String cmdFriendsComp4 = "accept";
    public String cmdFriendsComp5 = "refuse";
    public String cmdFriendsComp6 = "add";
    public String cmdFriendsComp7 = "remove";
    public String cmdFriendsComp8 = "list";
}
