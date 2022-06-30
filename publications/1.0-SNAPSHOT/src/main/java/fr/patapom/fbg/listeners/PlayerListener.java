package fr.patapom.fbg.listeners;

import fr.patapom.fbg.FriendsBG;
import fr.patapom.commons.friends.FriendsManager;
import fr.patapom.commons.friends.FriendsProvider;
import fr.patapom.commons.party.PartyManager;
import fr.patapom.commons.party.PartyProvider;
import fr.patapom.fbg.utils.exceptions.FManagerNotFoundException;
import fr.patapom.fbg.utils.exceptions.PManagerNotFoundException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

public class PlayerListener implements Listener
{
    private final Configuration config = FriendsBG.getInstance().getConfig();
    private final int timing = config.getInt("groups.timingTP");

    @EventHandler
    public void onJoin(PostLoginEvent e)
    {
        ProxyServer.getInstance().getScheduler().runAsync(FriendsBG.getInstance(), ()-> {
            final ProxiedPlayer p = e.getPlayer();
            try {
                FriendsProvider provider = new FriendsProvider(p.getUniqueId());
                FriendsManager fManager = provider.getFManager();
                fManager.setInGroup(false);
                fManager.setGroupId(null);
                provider.save(fManager);
            } catch (FManagerNotFoundException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e)
    {
        UUID uuid = e.getPlayer().getUniqueId();
        FriendsProvider provider = new FriendsProvider(uuid);
        FriendsManager fManager;
        try {
            fManager = provider.getFManager();
        } catch (FManagerNotFoundException ex1) {
            throw new RuntimeException(ex1);
        }
        if(fManager.isInGroup() && fManager.getGroupId() != null)
        {
            PartyProvider pProvider = new PartyProvider(fManager.getGroupId());
            PartyManager party;
            try {
                party = pProvider.getPManager();
            } catch (PManagerNotFoundException ex2) {
                throw new RuntimeException(ex2);
            }
            if(party.isOwner(uuid))
            {
                party.removePlayerInGroup(e.getPlayer());
                ProxyServer.getInstance().getScheduler().runAsync(FriendsBG.getInstance(), party::onQuit);
            }
            fManager.setInGroup(false);
            fManager.setGroupId(null);
            provider.save(fManager);
        }
        if(config.getBoolean("mysql.use"))
        {
            provider.updateDB();
        }
    }

    @EventHandler
    public void onTeleport(ServerConnectEvent e)
    {
        ProxiedPlayer p = e.getPlayer();
        FriendsProvider provider = new FriendsProvider(p.getUniqueId());
        FriendsManager fManager;
        try {
            fManager = provider.getFManager();
        } catch (FManagerNotFoundException ex1) {
            throw new RuntimeException(ex1);
        }
        if(fManager.isInGroup() && fManager.getGroupId() != null)
        {
            PartyProvider pProvider = new PartyProvider(fManager.getGroupId());
            PartyManager party;
            try {
                party = pProvider.getPManager();
            } catch (PManagerNotFoundException ex2) {
                throw new RuntimeException(ex2);
            }
            if(party.isOwner(p.getUniqueId()))
            {
                ProxyServer.getInstance().getScheduler().schedule(FriendsBG.getInstance(), party::onTeleport, timing, TimeUnit.MILLISECONDS);
            }
        }
    }
}
