package fr.patapom.friendsbg.fbg;

import com.google.common.reflect.TypeToken;
import fr.patapom.friendsbg.common.players.ProfileManager;
import fr.patapom.friendsbg.common.groups.GroupManager;
import fr.patapom.friendsbg.fbg.cmd.*;
import fr.patapom.friendsbg.fbg.listeners.PlayerListener;
import fr.tmmods.tmapi.bungee.TMBungeeAPI;
import fr.tmmods.tmapi.bungee.config.ConfigsManager;
import fr.tmmods.tmapi.bungee.data.manager.DBManager;
import fr.tmmods.tmapi.bungee.data.manager.RedisManager;
import fr.tmmods.tmapi.data.manager.Files;
import fr.tmmods.tmapi.data.manager.Json.SerializationManager;
import fr.tmmods.tmapi.data.manager.UpdateChecker;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;

import java.io.File;
import java.lang.reflect.Type;
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

public class FriendsBG extends Plugin
{
    private final int pluginId = 17971;

    private boolean upToDate;

    private final String console = "[FriendsBungee] -> ";

    private static FriendsBG INSTANCE;
    private static PluginManager pm;

    private Configuration config;
    private SerializationManager serManager;

    public boolean redisEnable;
    public boolean sqlEnable;

    public int antiSpamLevel;

    public static Map<UUID, ProfileManager> fManagers = new HashMap<>();
    public static Map<UUID, GroupManager> parties = new HashMap<>();
    public static Map<ProxiedPlayer, ProxiedPlayer> messages = new HashMap<>();
    public static Map<Integer, String> reports = new HashMap<>();

    public HashMap<UUID, Long> cooldown = new HashMap<>();

    @Override
    public void onLoad()
    {
        getLogger().info(console + " ");
        getLogger().info(console + "Loading in progress...");
        getLogger().info(console + " ");

        // UpdateChecker added by TM-API free software
        getLogger().info(console + "# ----------{ UpdateChecker }---------- #");
        getLogger().info(console + " ");
        getLogger().info(console + "Version : "+this.getDescription().getVersion());
        getLogger().info(console + " ");
        new UpdateChecker(pluginId).getVersion(version -> {
            if(this.getDescription().getVersion().equals(version)) {
                this.upToDate = true;
                getLogger().info(console + "Up to date !");
            }else {
                this.upToDate = false;
                getLogger().info(console + "New update is available : "+version);
            }
            getLogger().info(console + " ");
            getLogger().info(console + "# ---------- --------------- ---------- #");
        });

        //Config Files
        getLogger().info(console + " ");
        getLogger().info(console + "Loading config files...");
        getLogger().info(console + " ");
        this.config = ConfigsManager.getConfig("config", this);
        this.serManager = new SerializationManager();
        this.redisEnable = TMBungeeAPI.redisEnable;
        this.sqlEnable = TMBungeeAPI.sqlEnable;
        this.antiSpamLevel = config.getInt("msg.antiSpam.");
    }

    @Override
    public void onEnable()
    {
        getLogger().info(console + " ");
        getLogger().info(console + "Loading plugin parts...");
        getLogger().info(console + " ");
        INSTANCE = this;
        pm = ProxyServer.getInstance().getPluginManager();

        pm.registerCommand(this, new CmdFriends());
        pm.registerCommand(this, new CmdGroup());
        pm.registerCommand(this, new CmdMsg());
        pm.registerCommand(this, new CmdResend());
        pm.registerCommand(this, new CmdReport());

        pm.registerListener(this, new PlayerListener());

        final File file = new File("./", "reports.json");
        if(file.exists())
        {
            final String json = Files.loadFile(file);
            Type type = new TypeToken<Map<Integer, String>>() {}.getType();
            reports = (Map<Integer, String>) serManager.deserializeByType(json.replace("?", "§"), type);
        }

        getLogger().info(console + "Ready to use !");
    }

    public static FriendsBG getInstance() { return INSTANCE; }
    public int getPluginId() { return pluginId; }
    public Configuration getConfig() {return config;}
    public SerializationManager getSerializationManager() {return serManager;}

    @Override
    public void onDisable()
    {
        getLogger().info(console + " ");
        getLogger().info(console + "Disabling in progress...");
        getLogger().info(console + " ");
        if(sqlEnable)
        {
            DBManager.closeAllConnections();
            getLogger().info(console + "Database connections closed !");
            getLogger().info(console + " ");
        }
        if(redisEnable)
        {
            RedisManager.closeAllConnections();
            getLogger().info(console + "Redis connections closed !");
            getLogger().info(console + " ");
        }

        getLogger().info("Saving data in progress...");
        getLogger().info(console + " ");
        if(!reports.isEmpty())
        {
            // Save reports on Json file
            final File file = new File("./", "reports.json");
            final String json = serManager.serialize(reports);
            Files.save(file, json);
            getLogger().info(console + "Reports saved !");
            getLogger().info(console + " ");
        }

        getLogger().info(console + "Goodbye !");
    }
}
