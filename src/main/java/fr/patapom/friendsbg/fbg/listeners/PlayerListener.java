package fr.patapom.friendsbg.fbg.listeners;

import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.patapom.friendsbg.common.players.ProfileManager;
import fr.patapom.friendsbg.common.players.ProfileProvider;
import fr.patapom.friendsbg.common.groups.GroupManager;
import fr.patapom.friendsbg.common.groups.GroupProvider;
import fr.tmmods.tmapi.bungee.players.OfflinePlayer;
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
    private final Configuration msgConfig = FriendsBG.getInstance().getMsgConfig();
    private final int timing = FriendsBG.getInstance().getConfig().getInt("groups.timingTP");

    private final String prefix = msgConfig.getString("prefix").replace("&", "§");
    private final String suffix = msgConfig.getString("suffix").replace("&", "§");
    private final String fPrefix = msgConfig.getString("friends.prefix").replace("&", "§");
    private final String fSuffix = msgConfig.getString("friends.suffix").replace("&", "§");
    private final String friendConnected = msgConfig.getString("friends.friendConnected").replace("&", "§");
    private final String friendDisconnected = msgConfig.getString("friends.friendDisconnected").replace("&", "§");

    @EventHandler (priority = EventPriority.HIGH)
    public void onJoin(PostLoginEvent e)
    {
        final ProxiedPlayer p = e.getPlayer();

        if(FriendsBG.getInstance().getConfig().getBoolean("updates.adminMsg.use"))
        {
            if(!FriendsBG.getInstance().isUpToDate())
            {
                if(p.hasPermission(FriendsBG.getInstance().getMsgConfig().getString("updates.adminMsg.permission")))
                {
                    sendMessage(p, prefix+suffix+"§fNew version available : "+FriendsBG.getInstance().getNewVersion());
                }
            }
        }

        try {
            ProfileProvider profileProvider = new ProfileProvider(p);
            ProfileManager profile = profileProvider.getPManager();

            if(profile.getName() == null || !profile.getName().equalsIgnoreCase(p.getName()))
            {
                profile.setName(p.getName());
                profileProvider.save(profile);

                if(profile.hasFriends())
                {
                    for (UUID plsUUID : profile.getFriendsMap().values()) {
                        ProfileProvider targetProvider = new ProfileProvider(plsUUID);
                        ProfileManager targetProfile;
                        try {
                            targetProfile = targetProvider.getPManager();
                        } catch (ManagerNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }

                        if (targetProfile.getFriendsMap().containsValue(p.getUniqueId()))
                        {
                            if(!targetProfile.getFriendsMap().containsKey(p.getName()))
                            {
                                targetProfile.removeFriend(profile.getLastName());
                                targetProfile.addFriend(p.getName(), p.getUniqueId());
                            }
                        } else {
                            profile.removeFriend(targetProfile.getName());
                        }
                    }
                }
            }

            if(profile.isInGroup())
            {
                GroupProvider gpProvider = new GroupProvider(profile.getGroupId());

                if(!gpProvider.gExist())
                {
                    profile.setGroupId(null);
                    profileProvider.save(profile);
                }
            }

            if(profile.hasFriends())
            {
                for(UUID plsUUID : profile.getFriendsMap().values())
                {
                    ProfileProvider friendProvider = new ProfileProvider(plsUUID);

                    ProfileManager friendProfile = friendProvider.getPManager();

                    if(!friendProfile.isFriends(p.getName()))
                    {
                        profile.removeFriend(friendProfile.getName());
                        profileProvider.save(profile);
                    }

                    if(ProxyServer.getInstance().getPlayer(plsUUID) !=null && friendProfile.isFriends(p.getName()))
                    {
                        ProxiedPlayer onlineFriend = ProxyServer.getInstance().getPlayer(plsUUID);
                        sendMessage(onlineFriend, fPrefix+fSuffix+friendConnected.replace("%player%", p.getDisplayName()));
                    }
                }
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
            ProfileProvider profileProvider = new ProfileProvider(p);
            ProfileManager profile = profileProvider.getPManager();

            // Send disconnected message to all friends
            if(profile.hasFriends())
            {
                for(UUID plsUUID : profile.getFriendsMap().values())
                {
                    ProfileProvider friendProvider = new ProfileProvider(plsUUID);

                    ProfileManager friendProfile = friendProvider.getPManager();

                    if(ProxyServer.getInstance().getPlayer(plsUUID) !=null && friendProfile.isFriends(p.getName()))
                    {
                        ProxiedPlayer onlineFriend = ProxyServer.getInstance().getPlayer(plsUUID);
                        sendMessage(onlineFriend, fPrefix+fSuffix+friendDisconnected.replace("%player%", p.getDisplayName()));
                    }
                }
            }

            if(profile.isInGroup())
            {
                GroupProvider groupProvider = new GroupProvider(profile.getGroupId());

                if(!groupProvider.gExist())
                {
                    profile.setGroupId(null);
                    profileProvider.save(profile);
                }else {
                    GroupManager group;
                    try {
                        group = groupProvider.getGManager();
                    } catch (ManagerNotFoundException ex2) {
                        throw new RuntimeException(ex2);
                    }

                    if(group.isOwner(p.getUniqueId()))
                    {
                        // Eject all group members and send message
                        group.removePlayerInGroup(e.getPlayer());
                        ProxyServer.getInstance().getScheduler().runAsync(FriendsBG.getInstance(), group::onQuit);
                    }else {
                        profile.setGroupId(null);
                        profileProvider.save(profile);
                    }
                }
            }

            if(FriendsBG.getInstance().sqlEnable) {profileProvider.updateDB();return;}

            profileProvider.saveOnJson(profile);
        } catch (ManagerNotFoundException ex1) {
            throw new RuntimeException(ex1);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onTeleport(ServerConnectEvent e)
    {
        ProxiedPlayer p = e.getPlayer();

        try {
            ProfileProvider profileProvider = new ProfileProvider(p);
            ProfileManager profile = profileProvider.getPManager();
            if(profile.isInGroup())
            {
                GroupProvider groupProvider = new GroupProvider(profile.getGroupId());

                if(!groupProvider.gExist())
                {
                    profile.setGroupId(null);
                    profileProvider.save(profile);
                    return;
                }

                GroupManager group;
                try {
                    group = groupProvider.getGManager();
                } catch (ManagerNotFoundException ex2) {
                    throw new RuntimeException(ex2);
                }
                if(group.isOwner(p.getUniqueId()))
                {
                    ProxyServer.getInstance().getScheduler().schedule(FriendsBG.getInstance(), group::onTeleport, timing, TimeUnit.MILLISECONDS);
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
