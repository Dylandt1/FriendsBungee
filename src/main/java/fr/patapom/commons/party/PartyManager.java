package fr.patapom.commons.party;

import fr.patapom.commons.friends.FriendsProvider;
import fr.patapom.fbg.FriendsBG;
import fr.patapom.commons.friends.FriendsManager;
import fr.patapom.fbg.utils.exceptions.FManagerNotFoundException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.io.File;
import java.util.*;

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

public class PartyManager
{
    private UUID ownerUUID;
    private UUID groupId;
    private boolean tp;
    private int partyLength;

    private Map<String, UUID> playersInGroup;

    // New party :
    public PartyManager(ProxiedPlayer owner, UUID groupId, int partyLength)
    {
        this.ownerUUID = owner.getUniqueId();
        this.groupId = groupId;
        this.tp = true;
        this.partyLength = partyLength;
        this.playersInGroup = new HashMap<>();
        this.playersInGroup.put(owner.getName(), ownerUUID);
    }

    public UUID getOwnerUUID() {return ownerUUID;}
    public UUID getGroupId() {return groupId;}
    public boolean isOwner(UUID playerUUID) {return ownerUUID.equals(playerUUID);}
    public int getGroupLenght() {return partyLength;}
    public void changeOwnerGroup(ProxiedPlayer newOwner) {this.ownerUUID = newOwner.getUniqueId();}
    public List<ProxiedPlayer> getPlayersInGroup()
    {
        List<ProxiedPlayer> pls = new ArrayList<>();
        for(UUID uuid : playersInGroup.values())
        {
            pls.add(ProxyServer.getInstance().getPlayer(uuid));
        }
        return pls;
    }

    public void addPlayerInGroup(ProxiedPlayer player)
    {
        FriendsProvider provider = new FriendsProvider(player.getUniqueId());
        try {
            FriendsManager fManager = provider.getFManager();
            fManager.setInGroup(true);
            fManager.setGroupId(groupId);
            provider.save(fManager);
        } catch (FManagerNotFoundException e) {
            e.printStackTrace();
        }
        playersInGroup.put(player.getName(), player.getUniqueId());
    }

    public void removePlayerInGroup(ProxiedPlayer player)
    {
        FriendsProvider provider = new FriendsProvider(player.getUniqueId());
        try {
            FriendsManager fManager = provider.getFManager();
            fManager.setInGroup(false);
            fManager.setGroupId(null);
            provider.save(fManager);
        } catch (FManagerNotFoundException e) {
            e.printStackTrace();
        }
        playersInGroup.remove(player.getName());
    }

    public void onTeleport()
    {
        ProxiedPlayer owner = ProxyServer.getInstance().getPlayer(ownerUUID);
        ServerInfo srvInfos = owner.getServer().getInfo();
        for(String s : playersInGroup.keySet())
        {
            ProxiedPlayer players = ProxyServer.getInstance().getPlayer(playersInGroup.get(s));
            if(players != owner)
            {
                players.connect(srvInfos);
            }
        }
    }

    public void onQuit()
    {
        final Configuration config = FriendsBG.getInstance().getConfig();
        final String prefix = config.getString("groups.prefix").replace("&", "§");
        final String suffix = config.getString("groups.suffix").replace("&", "§");
        final String ownerOffline = config.getString("groups.ownerOffline").replace("&", "§");
        final File saveDirectory = new File(FriendsBG.getInstance().getDataFolder(), "/managers/fManagers/");
        for(int i = 0; i<getPlayersInGroup().size(); i++)
        {
            ProxiedPlayer player = getPlayersInGroup().get(i);
            FriendsProvider provider = new FriendsProvider(player.getUniqueId());
            try {
                FriendsManager fManager = provider.getFManager();
                fManager.setInGroup(false);
                fManager.setGroupId(null);
                provider.save(fManager);
            } catch (FManagerNotFoundException e) {
                e.printStackTrace();
            }
            player.sendMessage(new TextComponent(prefix+" "+suffix+" "+ownerOffline));
            if(i >= getPlayersInGroup().size())break;
        }
        final File file = new File(saveDirectory, groupId.toString()+".json");
        if(file.exists())
        {
            file.deleteOnExit();
        }
    }

    public void delete()
    {
        final File saveDirectory = new File(FriendsBG.getInstance().getDataFolder(), "/managers/fManagers/");
        final File file = new File(saveDirectory, groupId.toString()+".json");
        if(file.exists())
        {
            file.deleteOnExit();
        }
    }
}
