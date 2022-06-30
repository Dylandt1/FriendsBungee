package fr.patapom.fbg;

import api.config.ConfigsManager;
import api.data.manager.json.SerializationManager;
import api.data.manager.redis.RedisAccess;
import api.data.manager.sql.DBManager;
import api.data.manager.sql.SqlManager;
//import api.utils.PluginUpdateChecker;
import fr.patapom.fbg.cmd.*;
import fr.patapom.fbg.listeners.PlayerListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

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
    private static FriendsBG INSTANCE;
    private Configuration config;
    private SerializationManager serManager;
    //private String newVersion;

    private String prefix;
    private String suffix;
    public boolean redisEnable;
    public boolean sqlEnable;

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

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CmdFriends());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CmdParty());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CmdMsg());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CmdResend());

        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener());

        if(sqlEnable)
        {
            DBManager.init();
            SqlManager.createTable();
        }
        if(redisEnable)
        {
            RedisAccess.init();
        }
    }

    public static FriendsBG getInstance() { return INSTANCE; }
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
        System.out.println(prefix+" "+suffix+" "+"§lGoodbye §c!");
    }
}
