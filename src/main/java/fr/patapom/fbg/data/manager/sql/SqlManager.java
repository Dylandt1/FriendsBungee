package fr.patapom.fbg.data.manager.sql;

import fr.patapom.fbg.FriendsBG;
import net.md_5.bungee.config.Configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
