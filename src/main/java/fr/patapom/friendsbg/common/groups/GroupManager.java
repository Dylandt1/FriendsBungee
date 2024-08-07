package fr.patapom.friendsbg.common.groups;

import fr.patapom.friendsbg.common.players.ProfileProvider;
import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.patapom.friendsbg.common.players.ProfileManager;
import fr.tmmods.tmapi.exceptions.ManagerNotFoundException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

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

public class GroupManager
{
    private UUID ownerUUID;
    private UUID groupId;
    private boolean autoTp;
    private int groupLength;

    private Map<String, UUID> playersInGroup;

    public GroupManager() {}

    // New party :
    public GroupManager(ProxiedPlayer owner, UUID groupId, int groupLength)
    {
        this.ownerUUID = owner.getUniqueId();
        this.groupId = groupId;
        this.autoTp = true;
        this.groupLength = groupLength;
        this.playersInGroup = new HashMap<>();
        this.playersInGroup.put(owner.getName(), ownerUUID);
    }

    public UUID getOwnerUUID() {return ownerUUID;}
    public UUID getGroupId() {return groupId;}
    public int getGroupLenght() {return groupLength;}
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

    public boolean isOwner(UUID playerUUID) {return ownerUUID.equals(playerUUID);}
    public boolean tp() {return autoTp;}

    public void setTp(boolean autoTp) {this.autoTp = autoTp;}

    public void addPlayerInGroup(ProxiedPlayer player)
    {
        if(!playersInGroup.containsValue(player.getUniqueId()))
        {
            ProfileProvider profileProvider = new ProfileProvider(player.getUniqueId());
            try {
                ProfileManager profile = profileProvider.getPManager();
                profile.setGroupId(groupId);
                profileProvider.save(profile);
            } catch (ManagerNotFoundException e) {
                throw new RuntimeException(e);
            }
            playersInGroup.put(player.getName(), player.getUniqueId());
        }
        playersInGroup.replace(player.getName(), player.getUniqueId());
    }

    public void removePlayerInGroup(ProxiedPlayer player)
    {
        if(playersInGroup.containsValue(player.getUniqueId()))
        {
            ProfileProvider profileProvider = new ProfileProvider(player.getUniqueId());
            try {
                ProfileManager profile = profileProvider.getPManager();
                profile.setGroupId(null);
                profileProvider.save(profile);
            } catch (ManagerNotFoundException e) {
                e.printStackTrace();
            }
            playersInGroup.remove(player.getName());
        }
    }

    public void onTeleport()
    {
        ProxiedPlayer owner = ProxyServer.getInstance().getPlayer(ownerUUID);
        ServerInfo srvInfos = owner.getServer().getInfo();
        if(autoTp)
        {
            for(String s : playersInGroup.keySet())
            {
                ProxiedPlayer players = ProxyServer.getInstance().getPlayer(playersInGroup.get(s));
                if(players != owner)
                {
                    players.connect(srvInfos);
                }
            }
        }
    }

    public void onQuit()
    {
        final Configuration config = FriendsBG.getInstance().getConfig();
        final String prefix = config.getString("groups.prefix").replace("&", "§");
        final String suffix = config.getString("groups.suffix").replace("&", "§");
        final String ownerOffline = config.getString("groups.ownerOffline").replace("&", "§");

        for(ProxiedPlayer pls : getPlayersInGroup())
        {
            if(pls != null)
            {
                ProfileProvider profileProvider = new ProfileProvider(pls.getUniqueId());
                try {
                    ProfileManager profile = profileProvider.getPManager();
                    profile.setGroupId(null);
                    profileProvider.save(profile);
                } catch (ManagerNotFoundException e) {
                    e.printStackTrace();
                }

                if(pls.isConnected())
                {
                    pls.sendMessage(new TextComponent(prefix+" "+suffix+" "+ownerOffline));
                }
            }
        }

        GroupProvider provider = new GroupProvider(groupId);
        provider.delete();
    }

    public void delete()
    {
        for(ProxiedPlayer player : getPlayersInGroup())
        {
            ProfileProvider profileProvider = new ProfileProvider(player.getUniqueId());
            try {
                ProfileManager profile = profileProvider.getPManager();
                profile.setGroupId(null);
                profileProvider.save(profile);
            } catch (ManagerNotFoundException e) {
                e.printStackTrace();
            }
            Configuration config = FriendsBG.getInstance().getConfig();
            String prefix = config.getString("groups.prefix").replace("&", "§");
            String suffix = config.getString("groups.suffix").replace("&", "§");
            String groupDeletedTarget = config.getString("groups.groupDeletedTarget").replace("&", "§");
            sendMessage(player, prefix+suffix+groupDeletedTarget);
        }
        if(FriendsBG.getInstance().redisEnable)
        {
            GroupProvider provider = new GroupProvider(groupId);
            provider.delete();
            return;
        }
        FriendsBG.getInstance().groups.remove(groupId);
    }

    private void sendMessage(ProxiedPlayer p, String s) {
        p.sendMessage(new TextComponent(s));
    }
}
