package fr.patapom.fbg;

import fr.patapom.commons.friends.FriendsManager;
import fr.patapom.fbg.data.manager.redis.RedisAccess;
//import fr.patapom.tmapi.data.manager.UpdateChecker;
import fr.patapom.commons.party.PartyManager;
import fr.patapom.fbg.cmd.*;
import fr.patapom.fbg.data.manager.sql.DBManager;
import fr.patapom.fbg.data.manager.sql.SqlManager;
import fr.patapom.fbg.listeners.PlayerListener;
import fr.patapom.tmapi.bungee.config.ConfigsManager;
import fr.patapom.tmapi.data.manager.Json.SerializationManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
//import org.bstats.bungeecord.Metrics;
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

public class FriendsBG extends Plugin
{
    private final int pluginId = 17971;
    //private static Metrics metrics;

    private static FriendsBG INSTANCE;
    private static PluginManager pm;

    private Configuration config;
    private SerializationManager serManager;

    private String prefix;
    private String suffix;

    public boolean redisEnable;
    public boolean sqlEnable;

    public static Map<UUID, FriendsManager> fManagers = new HashMap<>();
    public static Map<UUID, PartyManager> parties = new HashMap<>();
    public static Map<ProxiedPlayer, ProxiedPlayer> messages = new HashMap<>();

    @Override
    public void onLoad()
    {
        //Config Files
        this.config = ConfigsManager.loadConfig("config", this);
        this.serManager = new SerializationManager();
        this.prefix = config.getString("prefix").replace("&", "§");
        this.suffix = config.getString("suffix").replace("&", "§");
        this.redisEnable = config.getBoolean("redis.use");
        this.sqlEnable = config.getBoolean("mysql.use");

        /**
         * if(config.getBoolean("updates.enable"))
         * {
         *      PluginUpdateChecker updateChecker = new PluginUpdateChecker()
         *      if(updateChecker.isAvailable())
         *      {
         *          this.newVersion = updateChecker.getVersionAvailable();
         *          System.out.println(prefix+" "+suffix+" "+"§aNew version available §f: §b"+newVersion);
         *      }
         * }
         */
    }

    @Override
    public void onEnable()
    {
        INSTANCE = this;
        pm = ProxyServer.getInstance().getPluginManager();

        //metrics = new Metrics(INSTANCE, pluginId);

        // Optional: Add custom charts
        //metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));

        pm.registerCommand(this, new CmdFriends());
        pm.registerCommand(this, new CmdParty());
        pm.registerCommand(this, new CmdMsg());
        pm.registerCommand(this, new CmdResend());

        pm.registerListener(this, new PlayerListener());

        if(sqlEnable)
        {
            DBManager.init();
            new SqlManager().createTables();
        }
        if(redisEnable)
        {
            RedisAccess.init();
        }
    }

    public static FriendsBG getInstance() { return INSTANCE; }

    //public static Metrics getMetrics() { return metrics; }
    public Configuration getConfig() {return config;}
    public SerializationManager getSerializationManager() {return serManager;}

    @Override
    public void onDisable()
    {
        if(sqlEnable)
        {
            DBManager.stop();
        }
        if(redisEnable && RedisAccess.getInstance() != null)
        {
            RedisAccess.getInstance().getRedisCli().shutdown();
        }
        System.out.println("[FriendsBungee] -> Goodbye !");
    }
}
