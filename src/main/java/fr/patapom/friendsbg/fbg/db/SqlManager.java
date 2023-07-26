package fr.patapom.friendsbg.fbg.db;

import fr.patapom.friendsbg.fbg.FriendsBG;
import net.md_5.bungee.config.Configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

public class SqlManager
{
    private static final Configuration cfg = FriendsBG.getInstance().getConfig();
    private static final String prefixTables = cfg.getString("mysql.prefixTables");
    private static final String tableName = cfg.getString("mysql.tableName");
    private static final String fTable = cfg.getString("mysql.friendsTable");

    // Functions part :
    public void createTables()
    {
        update("CREATE TABLE IF NOT EXISTS "+prefixTables+tableName+" (" +
                "id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "uuid VARCHAR(255), " +
                "name VARCHAR(255), " +
                "displayName VARCHAR(255), " +
                "requestsAllow TINYINT(1), " +
                "msgAllow TINYINT(1), " +
                "groupId VARCHAR(255))");
        update("CREATE TABLE IF NOT EXISTS "+prefixTables+fTable+" (" +
                "uuid VARCHAR(255), " +
                "friendUUID VARCHAR(255), " +
                "friendName VARCHAR(255))");
    }

    public static String getPrefixTables() {return prefixTables;}
    public static String getTableName() {return tableName;}
    public static String getFTable() {return tableName;}

    /**
     *
     */

    private static void update(String qry)
    {
        try
        {
            Connection connection = DBManager.DATABASE_ACCESS.getDBAccess().getConnection();
            PreparedStatement ps = connection.prepareStatement(qry);
            ps.executeUpdate();
            ps.close();
            connection.close();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     *
     */

    private static void query(String query)
    {
        try
        {
            Connection connection = DBManager.DATABASE_ACCESS.getDBAccess().getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.executeQuery();
            ps.close();
            connection.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     *
     */

    private static void execute(String query)
    {
        try {
            Connection connection = DBManager.DATABASE_ACCESS.getDBAccess().getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.execute();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
