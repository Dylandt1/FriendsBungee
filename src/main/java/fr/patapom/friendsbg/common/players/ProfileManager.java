package fr.patapom.friendsbg.common.players;

import fr.patapom.friendsbg.fbg.FriendsBG;
import net.md_5.bungee.api.ProxyServer;

import java.sql.Timestamp;
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
    private String lastName;
    private String displayName;
    private String rankInTeam;

    private boolean fAllow;
    private boolean msgAllow;
    private boolean gpAllow;
    private boolean teamsAllow;

    private Timestamp lastJoin;
    private Timestamp firstJoin;

    private Map<String, UUID> friends;

    public ProfileManager() {}

    public ProfileManager(UUID playerUUID, String playerName, String displayName, boolean fAllow, boolean msgAllow, boolean gpAllow, boolean teamsAllow, String rankInTeam, UUID groupId, UUID teamId, Map<String, UUID> friendsList, Timestamp lastJoin, Timestamp firstJoin)
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
        this.lastJoin = lastJoin;
        this.firstJoin = firstJoin;
    }

    /**
     * Getters
     */
    public UUID getUUID() {return uuid;}
    public UUID getGroupId() {return groupId;}
    public UUID getTeamId() {return teamId;}

    public String getName() {return name;}
    public String getLastName() {return lastName;}
    public String getDisplayName() {return displayName;}
    public String getRankInTeam() {return rankInTeam;}

    public boolean fAllow() {return fAllow;}
    public boolean msgAllow() {return msgAllow;}
    public boolean gpAllow() {return gpAllow;}
    public boolean teamsAllow() {return teamsAllow;}
    public boolean hasFriends() {return !friends.isEmpty();}
    public boolean isInGroup() {return groupId!=null;}
    public boolean isInTeam() {return teamId!=null;}

    public boolean isFriends(String friendName) {return friends.containsKey(friendName);}

    public int getNbFriends() {return friends.size();}

    public Timestamp getLastJoin() {return lastJoin;}
    public Timestamp getFirstJoin() {return firstJoin;}

    /**
     * Setters
     */
    public void addFriend(String playerName, UUID friendUUID) {friends.put(playerName, friendUUID);}
    public void removeFriend(String friendName) {friends.remove(friendName);}

    public void setName(String name) {this.name = name;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setGroupId(UUID groupId) {this.groupId = groupId;}
    public void setTeamId(UUID teamId) {this.teamId = teamId;}
    public void setFAllow(boolean status) {fAllow = status;}
    public void setMsgAllow(boolean status) {this.msgAllow = status;}
    public void setGpAllow(boolean status) {this.gpAllow = status;}
    public void setTeamsAllow(boolean status) {this.teamsAllow = status;}
    public void setRankInTeam(String rank) {this.rankInTeam = rank;}

    public void setLastJoin(Timestamp lastJoin) {this.lastJoin = lastJoin;}
    public void setFirstJoin(Timestamp firstJoin) {this.firstJoin = firstJoin;}

    public Map<String, UUID> getFriendsMap() {return friends;}
}
