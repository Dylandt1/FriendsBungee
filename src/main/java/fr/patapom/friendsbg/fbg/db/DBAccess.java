package fr.patapom.friendsbg.fbg.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

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

public class DBAccess
{
    // Private variables and singletons :
    private final DBCredentials credentials;
    private HikariDataSource dataSource;

    // Launch part :
    public DBAccess(DBCredentials credentials) {this.credentials = credentials;}

    public void init() {setupHikariCP();}
    public void stop() {if(dataSource != null) {dataSource.close();}}

    // Getter part :
    public DBCredentials getCredentials() {return credentials;}
    public HikariDataSource getDataSource() {return dataSource;}

    public Connection getConnection() throws SQLException
    {
        if(dataSource == null)setupHikariCP();
        return dataSource.getConnection();
    }

    // Setup HikariCP
    private void setupHikariCP()
    {
        final HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(credentials.maxPoolSize);
        config.setJdbcUrl(credentials.toURI());
        config.setUsername(credentials.user);
        config.setPassword(credentials.password);
        config.setMaxLifetime(credentials.maxLifeTime);
        config.setIdleTimeout(credentials.poolTimeOut);
        config.setLeakDetectionThreshold(credentials.dataLeak);
        config.setConnectionTimeout(credentials.timeOut);

        dataSource = new HikariDataSource(config);
    }
}
