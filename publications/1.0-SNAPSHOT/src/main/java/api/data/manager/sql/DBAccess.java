package api.data.manager.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.patapom.fbg.FriendsBG;
import net.md_5.bungee.config.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

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

public class DBAccess
{
    // Private variables and singletons :
    private static final Configuration cfg = FriendsBG.getInstance().getConfig();
    private static final String prefixTables = cfg.getString("mysql.prefixTables");
    private static final String tableName = cfg.getString("mysql.tableName");
    private DBCredentials credentials;
    private HikariDataSource dataSource;

    // Launch part :
    public DBAccess(DBCredentials credentials) {this.credentials = credentials;}
    public void init() {setupHikariCP();}
    public void stop()
    {
        if(dataSource != null)
        {
            dataSource.close();
        }
    }

    // Getter part :
    public DBCredentials getCredentials() {return credentials;}
    public HikariDataSource getDataSource() {return dataSource;}
    public static String getPrefixTables() {return prefixTables;}
    public static String getTableName() {return tableName;}

    public Connection getConnection() throws SQLException
    {
        if(dataSource == null)setupHikariCP();
        return dataSource.getConnection();
    }

    // Setup HikariCP
    private void setupHikariCP()
    {
        final HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(cfg.getInt("mysql.maxPoolSize"));
        config.setJdbcUrl(credentials.toURI());
        config.setUsername(credentials.getUser());
        config.setPassword(credentials.getPassword());
        config.setMaxLifetime(cfg.getInt("mysql.maxLifeTime"));
        config.setIdleTimeout(cfg.getInt("mysql.poolTimeOut"));
        config.setLeakDetectionThreshold(cfg.getInt("mysql.dataLeak"));
        config.setConnectionTimeout(cfg.getInt("mysql.timeOut"));

        dataSource = new HikariDataSource(config);
    }
}
