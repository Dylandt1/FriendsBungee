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
import java.util.logging.Logger;

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

    private static FriendsBG INSTANCE;
    private static PluginManager pm;
    private final String console = "[FriendsBungee] -> ";

    private Configuration config;
    private SerializationManager serManager;

    public boolean redisEnable;
    public boolean sqlEnable;
    public int antiSpamLevel;

    public static Map<UUID, ProfileManager> profiles = new HashMap<>();
    public static Map<UUID, GroupManager> groups = new HashMap<>();
    public static Map<ProxiedPlayer, ProxiedPlayer> messages = new HashMap<>();
    public static Map<Integer, String> reports = new HashMap<>();

    public HashMap<UUID, Long> cooldown = new HashMap<>();

    @Override
    public void onLoad()
    {
        log(console + " ");
        log(console + "Loading in progress...");
        log(console + " ");

        // UpdateChecker added by TM-API free software
        log(console + "# ----------{ UpdateChecker }---------- #");
        log(console + " ");
        log(console + "Version : "+this.getDescription().getVersion());
        log(console + " ");
        new UpdateChecker(pluginId).getVersion(version -> {
            if(this.getDescription().getVersion().equals(version)) {
                this.upToDate = true;
                log(console + "Up to date !");
            }else {
                this.upToDate = false;
                log(console + "New update is available : "+version);
            }
            log(console + " ");
            log(console + "# ---------- --------------- ---------- #");
        });

        //Config Files
        log(console + " ");
        log(console + "Loading config files...");
        log(console + " ");
        this.config = ConfigsManager.getConfig("config", this);
        this.serManager = new SerializationManager();
        this.redisEnable = TMBungeeAPI.redisEnable;
        this.sqlEnable = TMBungeeAPI.sqlEnable;
        this.antiSpamLevel = config.getInt("msg.antiSpam.");
    }

    @Override
    public void onEnable()
    {
        log(console + " ");
        log(console + "Loading plugin parts...");
        log(console + " ");
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

        log(console + "Ready to use !");
    }

    public static FriendsBG getInstance() { return INSTANCE; }
    public int getPluginId() { return pluginId; }
    public Configuration getConfig() {return config;}
    public SerializationManager getSerializationManager() {return serManager;}
    public void log(String log) {getLogger().info(log);}

    @Override
    public void onDisable()
    {
        log(console + " ");
        log(console + "Disabling in progress...");
        log(console + " ");
        if(sqlEnable)
        {
            DBManager.closeAllConnections();
            log(console + "Database connections closed !");
            log(console + " ");
        }
        if(redisEnable)
        {
            RedisManager.closeAllConnections();
            log(console + "Redis connections closed !");
            log(console + " ");
        }

        log("Saving data in progress...");
        log(console + " ");
        if(!reports.isEmpty())
        {
            // Save reports on Json file
            final File file = new File("./", "reports.json");
            final String json = serManager.serialize(reports);
            Files.save(file, json);
            log(console + "Reports saved !");
            log(console + " ");
        }

        log(console + "Goodbye !");
    }
}
