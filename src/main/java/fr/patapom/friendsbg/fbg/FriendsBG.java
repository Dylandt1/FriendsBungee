package fr.patapom.friendsbg.fbg;

import com.google.common.reflect.TypeToken;
import fr.patapom.friendsbg.common.players.ProfileManager;
import fr.patapom.friendsbg.common.groups.GroupManager;
import fr.patapom.friendsbg.fbg.cmd.*;
import fr.patapom.friendsbg.fbg.data.manager.DBManager;
import fr.patapom.friendsbg.fbg.data.manager.RedisManager;
import fr.patapom.friendsbg.fbg.listeners.PlayerListener;
import fr.tmmods.tmapi.bungee.config.ConfigsManager;
import fr.tmmods.tmapi.data.manager.Files;
import fr.tmmods.tmapi.data.manager.Json.SerializationManager;
import fr.tmmods.tmapi.data.manager.UpdateChecker;
import fr.tmmods.tmapi.data.manager.sql.SqlManager;
import fr.tmmods.tmapi.data.manager.sql.SqlType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.SQLException;
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

    private static FriendsBG INSTANCE;
    private static PluginManager pm;
    private final String console = "[FriendsBungee] -> ";
    private SqlManager sqlManager;

    private Configuration config;
    private SerializationManager serManager;

    public boolean redisEnable;
    public boolean sqlEnable;
    public int antiSpamLevel;

    public String prefixTables;
    public String profilesTable;
    public String friendsTable;

    public static Map<UUID, ProfileManager> profiles = new HashMap<>();
    public static Map<UUID, GroupManager> groups = new HashMap<>();
    public static Map<ProxiedPlayer, ProxiedPlayer> messages = new HashMap<>();
    public static Map<Integer, String> reports = new HashMap<>();

    public HashMap<UUID, Long> cooldown = new HashMap<>();

    @Override
    public void onLoad()
    {
        log(" ");
        log(console + "Loading in progress...");
        log(" ");

        // UpdateChecker added by TM-API free software
        log(console + "# ----------{ UpdateChecker }---------- #");
        log(" ");
        log(console + "Version : "+this.getDescription().getVersion());
        log(" ");
        new UpdateChecker(pluginId).getVersion(version -> {
            if(this.getDescription().getVersion().equals(version)) {
                this.upToDate = true;
                log(console + "Up to date !");
            }else {
                this.upToDate = false;
                log(console + "New update is available : "+version);
            }
            log(" ");
            log(console + "# ---------- --------------- ---------- #");
        });

        //Config Files
        log(" ");
        log(console + "Loading config files...");
        log(" ");
        this.config = ConfigsManager.getConfig("config", this);
        this.serManager = new SerializationManager();
        this.redisEnable = config.getBoolean("redis.use");
        this.sqlEnable = config.getBoolean("mysql.use");
        this.antiSpamLevel = config.getInt("msg.antiSpam.");
        this.prefixTables = config.getString("mysql.prefixTables");
        this.profilesTable = config.getString("mysql.profilesTable");
        this.friendsTable = config.getString("mysql.friendsTable");
    }

    @Override
    public void onEnable()
    {
        log(" ");
        log(console + "Loading plugin parts...");
        log(" ");
        INSTANCE = this;
        pm = ProxyServer.getInstance().getPluginManager();

        pm.registerCommand(this, new CmdFriends());
        pm.registerCommand(this, new CmdGroup());
        pm.registerCommand(this, new CmdMsg());
        pm.registerCommand(this, new CmdResend());
        pm.registerCommand(this, new CmdReport());

        pm.registerListener(this, new PlayerListener());

        if(sqlEnable)
        {
            getLogger().info(" ");
            getLogger().info(console + "Connecting to databases...");
            getLogger().info(" ");
            DBManager.initAllConnections();
            try {
                this.sqlManager = new SqlManager(DBManager.FBG_DATABASE.getDbAccess().getConnection());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            createTables();
        }

        if(redisEnable)
        {
            getLogger().info(" ");
            getLogger().info(console + "Connecting to redis servers...");
            getLogger().info(" ");
            RedisManager.initAllConnections();
        }

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
        log(" ");
        log(console + "Disabling in progress...");
        log(" ");
        if(sqlEnable)
        {
            DBManager.closeAllConnections();
            log(console + "Database connections closed !");
            log(" ");
        }
        if(redisEnable)
        {
            RedisManager.closeAllConnections();
            log(console + "Redis connections closed !");
            log(" ");
        }

        log("Saving data in progress...");
        log(" ");
        if(!reports.isEmpty())
        {
            // Save reports on Json file
            final File file = new File("./", "reports.json");
            final String json = serManager.serialize(reports);
            Files.save(file, json);
            log(console + "Reports saved !");
            log(" ");
        }

        log(console + "Goodbye !");
    }

    private void createTables()
    {
        Map<String, List<String>> tables = new HashMap<>();

        List<String> listProfilesTable = Arrays.asList(
                "id "+ SqlType.INT.sql()+" NOT NULL AUTO_INCREMENT PRIMARY KEY",
                "uuid "+SqlType.VARCHAR.sql(),
                "name "+SqlType.VARCHAR.sql(),
                "displayName "+SqlType.VARCHAR.sql(),
                "rankInTeam "+SqlType.TINYINT.sql(1),
                "teamId "+SqlType.VARCHAR.sql(),
                "groupId "+SqlType.VARCHAR.sql(),
                "fAllow "+SqlType.BOOLEAN.sql(),
                "msgAllow "+SqlType.BOOLEAN.sql(),
                "gpAllow "+SqlType.BOOLEAN.sql(),
                "teamsAllow "+SqlType.BOOLEAN.sql(),
                "lastJoin "+SqlType.TIMESTAMP.sql(),
                "firstJoin "+SqlType.TIMESTAMP.sql()
        );

        List<String> listFriendsTable = Arrays.asList(
                "uuid "+SqlType.VARCHAR.sql(),
                "friendUUID "+SqlType.VARCHAR.sql(),
                "friendName "+SqlType.VARCHAR.sql(),
                "friendDisplayName "+SqlType.VARCHAR.sql()
        );

        List<String> listTeamsTable = Arrays.asList(
                "id "+ SqlType.INT.sql()+" NOT NULL AUTO_INCREMENT PRIMARY KEY",
                "teamId "+SqlType.VARCHAR.sql(),
                "teamName "+SqlType.VARCHAR.sql(),
                "teamPrefix "+SqlType.VARCHAR.sql(),
                "teamRank "+SqlType.INT.sql(),
                "trophy "+SqlType.INT.sql(),
                "defaultRole "+SqlType.TINYINT.sql(1),
                "leaderUUID "+SqlType.VARCHAR.sql(),
                "deputyUUID "+SqlType.VARCHAR.sql(),
                "prefixLeader "+SqlType.VARCHAR.sql(),
                "prefixDeputy "+SqlType.VARCHAR.sql(),
                "prefixAssistants "+SqlType.VARCHAR.sql(),
                "prefixMembers "+SqlType.VARCHAR.sql(),
                "prefixRecruits "+SqlType.VARCHAR.sql()
        );

        tables.put(profilesTable, listProfilesTable);
        tables.put(friendsTable, listFriendsTable);

        sqlManager.createTables(prefixTables, tables);
    }
}
