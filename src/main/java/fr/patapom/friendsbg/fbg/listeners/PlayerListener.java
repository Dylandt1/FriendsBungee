package fr.patapom.friendsbg.fbg.listeners;

import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.patapom.friendsbg.common.players.ProfileManager;
import fr.patapom.friendsbg.common.players.ProfileProvider;
import fr.patapom.friendsbg.common.groups.GroupManager;
import fr.patapom.friendsbg.common.groups.GroupProvider;
import fr.tmmods.tmapi.exceptions.ManagerNotFoundException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

public class PlayerListener implements Listener
{
    private final Configuration config = FriendsBG.getInstance().getConfig();
    private final int timing = config.getInt("groups.timingTP");

    private final String fPrefix = config.getString("friends.prefix").replace("&", "§");
    private final String fSuffix = config.getString("friends.suffix").replace("&", "§");
    private final String friendConnected = config.getString("friends.friendConnected").replace("&", "§");
    private final String friendDisconnected = config.getString("friends.friendDisconnected").replace("&", "§");

    @EventHandler (priority = EventPriority.HIGH)
    public void onJoin(PostLoginEvent e)
    {
        final ProxiedPlayer p = e.getPlayer();

        try {
            ProfileProvider fProvider = new ProfileProvider(p.getUniqueId());
            ProfileManager fManager = fProvider.getFManager();

            ProxyServer.getInstance().getScheduler().runAsync(FriendsBG.getInstance(), ()-> {
                if(fManager.hasFriends())
                {
                    for(UUID plsUUID : fManager.getFriendsMap().values())
                    {
                        if(ProxyServer.getInstance().getPlayer(plsUUID) !=null)
                        {
                            ProxiedPlayer onlineFriend = ProxyServer.getInstance().getPlayer(plsUUID);
                            sendMessage(onlineFriend, fPrefix+fSuffix+friendConnected.replace("%player%", p.getDisplayName()));
                        }
                    }
                }
            });

            if(fManager.isInGroup())
            {
                GroupProvider pProvider = new GroupProvider(fManager.getGroupId());

                if(!pProvider.gExist())
                {
                    fManager.setGroupId(null);
                    fProvider.save(fManager);
                    return;
                }

                GroupManager pManager = pProvider.getPManager();
                pManager.addPlayerInGroup(p);
                pProvider.save(pManager);
            }

        }catch (ManagerNotFoundException ex){
            throw new RuntimeException(ex);
        }

    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onQuit(PlayerDisconnectEvent e)
    {
        final ProxiedPlayer p = e.getPlayer();

        try {
            ProfileProvider fProvider = new ProfileProvider(p.getUniqueId());
            ProfileManager fManager = fProvider.getFManager();

            // Send connected message to all friends
            ProxyServer.getInstance().getScheduler().runAsync(FriendsBG.getInstance(), ()-> {
                if(fManager.hasFriends())
                {
                    for(UUID plsUUID : fManager.getFriendsMap().values())
                    {
                        if(ProxyServer.getInstance().getPlayer(plsUUID) !=null)
                        {
                            ProxiedPlayer onlineFriend = ProxyServer.getInstance().getPlayer(plsUUID);
                            sendMessage(onlineFriend, fPrefix+fSuffix+friendDisconnected.replace("%player%", p.getDisplayName()));
                        }
                    }
                }
            });

            if(fManager.isInGroup())
            {
                GroupProvider pProvider = new GroupProvider(fManager.getGroupId());

                if(!pProvider.gExist())
                {
                    fManager.setGroupId(null);
                    fProvider.save(fManager);
                    return;
                }

                GroupManager pManager;
                try {
                    pManager = pProvider.getPManager();
                } catch (ManagerNotFoundException ex2) {
                    throw new RuntimeException(ex2);
                }

                if(pManager.isOwner(p.getUniqueId()))
                {
                    // Eject all group members and send message
                    pManager.removePlayerInGroup(e.getPlayer());
                    ProxyServer.getInstance().getScheduler().runAsync(FriendsBG.getInstance(), pManager::onQuit);
                }else {
                    // Eject player from group and send message
                    pManager.removePlayerInGroup(e.getPlayer());
                    pProvider.save(pManager);
                }
            }

            if(FriendsBG.getInstance().sqlEnable) {fProvider.updateDB();return;}

            fProvider.save(fManager);
        } catch (ManagerNotFoundException ex1) {
            throw new RuntimeException(ex1);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onTeleport(ServerConnectEvent e)
    {
        ProxiedPlayer p = e.getPlayer();

        try {
            ProfileProvider fProvider = new ProfileProvider(p.getUniqueId());
            ProfileManager fManager = fProvider.getFManager();
            if(fManager.isInGroup())
            {
                GroupProvider pProvider = new GroupProvider(fManager.getGroupId());

                if(!pProvider.gExist())
                {
                    fManager.setGroupId(null);
                    fProvider.save(fManager);
                    return;
                }

                GroupManager pManager;
                try {
                    pManager = pProvider.getPManager();
                } catch (ManagerNotFoundException ex2) {
                    throw new RuntimeException(ex2);
                }
                if(pManager.isOwner(p.getUniqueId()))
                {
                    ProxyServer.getInstance().getScheduler().schedule(FriendsBG.getInstance(), pManager::onTeleport, timing, TimeUnit.MILLISECONDS);
                }
            }
        } catch (ManagerNotFoundException ex1) {
            throw new RuntimeException(ex1);
        }
    }

    private void sendMessage(ProxiedPlayer p, String s) {
        p.sendMessage(new TextComponent(s));
    }
}
