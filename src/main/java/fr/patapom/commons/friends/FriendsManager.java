package fr.patapom.commons.friends;

import net.md_5.bungee.api.ProxyServer;

import java.util.Map;
import java.util.UUID;

/**
 * This file is part of FriendsBungee (FriendsBG-Free), a bungeecord friends plugin.
 *
 * FriendsBungee (FriendsBG-Free) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FriendsBungee (FriendsBG-Free) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

public class FriendsManager
{
    private UUID uuid;
    private UUID groupId;
    private String name;
    private String displayName;
    private boolean requestsAllow;
    private boolean msgAllow;

    private Map<String, UUID> friends;

    public FriendsManager() {}

    public FriendsManager(UUID playerUUID, String playerName, String displayName, boolean requestsAllow, boolean msgAllow, UUID groupId, Map<String, UUID> friendsList)
    {
        this.uuid = playerUUID;
        this.name = playerName;
        this.displayName = displayName;
        this.requestsAllow = requestsAllow;
        this.msgAllow = msgAllow;
        this.groupId = groupId;
        this.friends = friendsList;
    }

    /**
     * Getters
     */
    public UUID getUUID() {return uuid;}
    public UUID getGroupId() {return groupId;}
    public String getName() {return name;}
    public String getDisplayName() {return displayName;}

    public boolean requestsAllow() {return requestsAllow;}
    public boolean msgAllow() {return msgAllow;}
    public boolean hasFriends() {return !friends.isEmpty();}
    public boolean isInGroup() {return groupId!=null;}

    public boolean isFriends(String friendName) {return friends.containsKey(friendName);}
    public int getNbFriends() {return friends.size();}

    /**
     * Setters
     */
    public void addFriend(UUID friendUUID) {friends.put(ProxyServer.getInstance().getPlayer(friendUUID).getName(), friendUUID);}
    public void removeFriend(String friendName) {friends.remove(friendName);}

    public void setGroupId(UUID groupId) {this.groupId = groupId;}
    public void setRequestsAllow(boolean status) {requestsAllow = status;}
    public void setMsgAllow(boolean msgAllow) {this.msgAllow = msgAllow;}

    public Map<String, UUID> getFriendsMap() {return friends;}
}
