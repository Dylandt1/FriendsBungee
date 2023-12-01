package fr.patapom.friendsbg.common.players;

import net.md_5.bungee.api.ProxyServer;

import java.util.*;

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

public class ProfileManager
{
    private UUID uuid;
    private UUID groupId;
    private UUID teamId;
    private String name;
    private String displayName;
    private String rankInTeam;
    private boolean fAllow;
    private boolean msgAllow;
    private boolean gpAllow;
    private boolean teamsAllow;

    private Map<String, UUID> friends;

    private HashSet<String> opts;
    private boolean opt;

    public ProfileManager() {}

    public ProfileManager(UUID playerUUID, String playerName, String displayName, boolean fAllow, boolean msgAllow, boolean gpAllow, boolean teamsAllow, String rankInTeam, UUID groupId, UUID teamId, Map<String, UUID> friendsList, HashSet<String> opts)
    {
        this.uuid = playerUUID;
        this.name = playerName;
        this.displayName = displayName;
        this.fAllow = fAllow;
        this.msgAllow = msgAllow;
        this.gpAllow = gpAllow;
        this.teamsAllow = teamsAllow;
        this.rankInTeam = rankInTeam;
        this.groupId = groupId;
        this.teamId = teamId;
        this.friends = friendsList;

        this.opts = opts;
        this.opt = opts.containsAll(List.of(new String[]{"F", "G", "M", "R"}));
    }

    /**
     * Getters
     */
    public UUID getUUID() {return uuid;}
    public UUID getGroupId() {return groupId;}
    public UUID getTeamId() {return teamId;}
    public String getName() {return name;}
    public String getDisplayName() {return displayName;}
    public String getRankInTeam() {return rankInTeam;}

    public boolean fAllow() {return fAllow;}
    public boolean msgAllow() {return msgAllow;}
    public boolean gpAllow() {return gpAllow;}
    public boolean teamsAllow() {return teamsAllow;}
    public boolean hasFriends() {return !friends.isEmpty();}
    public boolean isInGroup() {return groupId!=null;}
    public boolean isInTeam() {return teamId!=null;}
    public boolean opt() {return opt;}

    public boolean isFriends(String friendName) {return friends.containsKey(friendName);}
    public int getNbFriends() {return friends.size();}

    /**
     * Setters
     */
    public void addFriend(UUID friendUUID) {friends.put(ProxyServer.getInstance().getPlayer(friendUUID).getName(), friendUUID);}
    public void removeFriend(String friendName) {friends.remove(friendName);}

    public void addOpts(String opt) {opts.add(opt);}
    public void clearOpts() {opts.clear();}

    public void setGroupId(UUID groupId) {this.groupId = groupId;}
    public void setTeamId(UUID teamId) {this.teamId = teamId;}
    public void setFAllow(boolean status) {fAllow = status;}
    public void setMsgAllow(boolean status) {this.msgAllow = status;}
    public void setGpAllow(boolean status) {this.gpAllow = status;}
    public void setTeamsAllow(boolean status) {this.teamsAllow = status;}
    public void setRankInTeam(String rank) {this.rankInTeam = rank;}

    public Map<String, UUID> getFriendsMap() {return friends;}

    public HashSet<String> getOpts() {return opts;}
}
