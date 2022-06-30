package api.data.manager.sql;

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

public class DBCredentials
{
    private String host;
    private int port;
    private String dbName;
    private String user;
    private String password;

    public DBCredentials(String host, int port, String dbName, String user, String password)
    {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
    }

    public String toURI()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append("jdbc:mysql://")
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(dbName);

        return sb.toString();
    }

    public String getUser() {return user;}
    public String getPassword() {return password;}
}
