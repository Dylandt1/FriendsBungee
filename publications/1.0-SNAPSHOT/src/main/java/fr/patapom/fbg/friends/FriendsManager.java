package fr.patapom.commons.friends;

import net.md_5.bungee.api.ProxyServer;

import java.util.Map;
import java.util.UUID;

/**
 * This file is part of FriendsBungee (FriendsBG-Free), a bungeecord friends plugin.
 *
 * Copyright (C) <2022>  <Dylan André>
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

    private boolean isAllow;
    private boolean isInGroup;
    private UUID groupId;

    private Map<String, UUID> friends;

    public FriendsManager() {}

    public FriendsManager(UUID playerUUID, boolean isAllow, Map<String, UUID> friendsList)
    {
        this.uuid = playerUUID;
        this.isAllow = isAllow;
        this.isInGroup = false;
        this.groupId = null;
        this.friends = friendsList;
    }

    public boolean isFriends(String friendName) {return friends.containsKey(friendName);}
    public boolean isAllow() {return isAllow;}
    public int getNbFriends() {return friends.size();}
    public boolean isInGroup() {return isInGroup;}
    public UUID getGroupId() {return groupId;}

    public void setAllow(boolean status) {isAllow = status;}
    public void addFriend(UUID friendUUID) {friends.put(ProxyServer.getInstance().getPlayer(friendUUID).getName(), friendUUID);}
    public void removeFriend(String friendName) {friends.remove(friendName);}
    public void setInGroup(boolean isInGroup) {this.isInGroup = isInGroup;}
    public void setGroupId(UUID groupId) {this.groupId = groupId;}

    public Map<String, UUID> getFriendsMap() {return friends;}
}
