package fr.patapom.friendsbg.fbg.db;

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

public class DBCredentials
{
    private String host;
    private int port;
    private String dbName;
    public String user;
    public String password;
    public int maxPoolSize;
    public int maxLifeTime;
    public int poolTimeOut;
    public int dataLeak;
    public int timeOut;

    public DBCredentials(String host, int port, String dbName, String user, String password)
    {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
        this.maxPoolSize = 16;
        this.maxLifeTime = 600000;
        this.poolTimeOut = 300000;
        this.dataLeak = 300000;
        this.timeOut = 10000;
    }

    public DBCredentials(String host, int port, String dbName, String user, String password,
                         int maxPoolSize, int maxLifeTime, int poolTimeOut, int dataLeak, int timeOut)
    {
        this.host = host;this.port = port;this.dbName = dbName;this.user = user;this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.maxLifeTime = maxLifeTime;
        this.poolTimeOut = poolTimeOut;
        this.dataLeak = dataLeak;
        this.timeOut = timeOut;
    }

    public String toURI() {return "jdbc:mysql://" + host + ":" + port + "/" + dbName;}
}
