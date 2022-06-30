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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlManager
{
    private static final String prefixTables = DBAccess.getPrefixTables();
    private final static String friendsTable = "friends";
    private final static String tableAllow = "fbg_allow";

    // Functions part :
    public static void createTable()
    {
        update("CREATE TABLE IF NOT EXISTS "+prefixTables+friendsTable+" (" +
                "player_uuid VARCHAR(255), " +
                "friend_uuid VARCHAR(255), " +
                "friend_name VARCHAR(255))");
        update("CREATE TABLE IF NOT EXISTS "+tableAllow+" (" +
                "uuid VARCHAR(255), " +
                "isAllow INT(11))");
    }

    private static void update(String qry)
    {
        Connection connection = null;
        PreparedStatement ps;
        try
        {
            connection = DBManager.DATABASE_ACCESS.getDBAccess().getConnection();
            ps = connection.prepareStatement(qry);
            ps.executeUpdate();
            ps.close();
            connection.close();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void query(String query)
    {
        Connection connection;
        PreparedStatement ps;
        try
        {
            connection = DBManager.DATABASE_ACCESS.getDBAccess().getConnection();
            ps = connection.prepareStatement(query);
            ps.executeQuery();
            ps.close();
            connection.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static void execute(String query)
    {
        Connection connection;
        PreparedStatement ps;
        try
        {
            connection = DBManager.DATABASE_ACCESS.getDBAccess().getConnection();
            ps = connection.prepareStatement(query);
            ps.execute();
            ps.close();
            connection.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
}