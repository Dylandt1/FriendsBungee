package fr.patapom.friendsbg.fbg;

import fr.patapom.friendsbg.fbg.data.manager.DBManager;
import fr.patapom.friendsbg.fbg.data.manager.RedisManager;
import fr.patapom.friendsbg.fbg.json.SerializationManager;
import fr.patapom.friendsbg.common.players.ProfileManager;
//import fr.patapom.tmapi.data.manager.UpdateChecker;
import fr.patapom.friendsbg.common.groups.GroupManager;
import fr.patapom.friendsbg.fbg.listeners.PlayerListener;
import fr.patapom.friendsbg.fbg.cmd.CmdFriends;
import fr.patapom.friendsbg.fbg.cmd.CmdMsg;
import fr.patapom.friendsbg.fbg.cmd.CmdGroup;
import fr.patapom.friendsbg.fbg.cmd.CmdResend;
import fr.tmmods.tmapi.bungee.config.ConfigsManager;
import fr.tmmods.tmapi.data.manager.UpdateChecker;
import fr.tmmods.tmapi.data.manager.redis.RedisAccess;
import fr.tmmods.tmapi.data.manager.sql.SqlManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
//import org.bstats.bungeecord.Metrics;
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

    private final String console = "[FriendsBungee] -> ";

    private static FriendsBG INSTANCE;
    private static PluginManager pm;

    private Configuration config;
    private SerializationManager serManager;

    public SqlManager sqlProfilesManager;
    public SqlManager sqlFListManager;

    private String prefix;
    private String suffix;

    public boolean redisEnable;
    public boolean sqlEnable;

    public static Map<UUID, ProfileManager> fManagers = new HashMap<>();
    public static Map<UUID, GroupManager> parties = new HashMap<>();
    public static Map<ProxiedPlayer, ProxiedPlayer> messages = new HashMap<>();

    @Override
    public void onLoad()
    {
        getLogger().info(console + "Loading in progress...");

        // UpdateChecker added by TM-API free software
        getLogger().info(console + "Checking for update");
        new UpdateChecker(pluginId).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info(console + "Up to date !");
            } else {
                getLogger().info(console + "New update is available !");
            }
        });

        //Config Files
        getLogger().info(console + "Loading config files...");
        this.config = ConfigsManager.getConfig("config", this);
        this.serManager = new SerializationManager();
        this.prefix = config.getString("prefix").replace("&", "§");
        this.suffix = config.getString("suffix").replace("&", "§");
        this.redisEnable = config.getBoolean("redis.use");
        this.sqlEnable = config.getBoolean("mysql.use");
    }

    @Override
    public void onEnable()
    {
        getLogger().info(console + "Loading plugin parts...");
        INSTANCE = this;
        pm = ProxyServer.getInstance().getPluginManager();

        //metrics = new Metrics(INSTANCE, pluginId);

        // Optional: Add custom charts
        //metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));

        pm.registerCommand(this, new CmdFriends());
        pm.registerCommand(this, new CmdGroup());
        pm.registerCommand(this, new CmdMsg());
        pm.registerCommand(this, new CmdResend());

        pm.registerListener(this, new PlayerListener());

        if(sqlEnable)
        {
            getLogger().info(console + "Connecting to databases...");
            DBManager.initAllConnections();
            try {
                sqlProfilesManager = new SqlManager(DBManager.FBG_DATABASE.getDbAccess().getConnection(), config.getString("mysql.prefixTables"), config.getString("mysql.tableName"));
                sqlFListManager = new SqlManager(DBManager.FBG_DATABASE.getDbAccess().getConnection(), config.getString("mysql.prefixTables"), config.getString("mysql.friendsTable"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(redisEnable)
        {
            getLogger().info(console + "Connecting to redis servers...");
            RedisManager.initAllConnections();
        }

        getLogger().info(console + "Ready to use !");
    }

    public static FriendsBG getInstance() { return INSTANCE; }

    //public static Metrics getMetrics() { return metrics; }
    public Configuration getConfig() {return config;}
    public SerializationManager getSerializationManager() {return serManager;}

    @Override
    public void onDisable()
    {
        getLogger().info(console + "Disabling in progress...");
        if(sqlEnable)
        {
            DBManager.closeAllConnections();
            getLogger().info(console + "Database connections closed !");
        }
        if(redisEnable)
        {
            RedisManager.closeAllConnections();
            getLogger().info(console + "Redis connections closed !");
        }
        getLogger().info(console + "Goodbye !");
    }
}
