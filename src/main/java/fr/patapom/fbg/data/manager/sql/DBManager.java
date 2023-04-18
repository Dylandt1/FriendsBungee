package fr.patapom.fbg.data.manager.sql;

import fr.patapom.fbg.FriendsBG;
import fr.patapom.tmapi.data.manager.sql.DBAccess;
import fr.patapom.tmapi.data.manager.sql.DBCredentials;

public enum DBManager
{
    // All DB access supported :
    DATABASE_ACCESS(new DBCredentials(FriendsBG.getInstance().getConfig().getString("mysql.host"),
            FriendsBG.getInstance().getConfig().getInt("mysql.port"),
            FriendsBG.getInstance().getConfig().getString("mysql.dbName"),
            FriendsBG.getInstance().getConfig().getString("mysql.user"),
            FriendsBG.getInstance().getConfig().getString("mysql.password"),
            FriendsBG.getInstance().getConfig().getInt("mysql.maxPoolSize"),
            FriendsBG.getInstance().getConfig().getInt("mysql.maxLifeTime"),
            FriendsBG.getInstance().getConfig().getInt("mysql.poolTimeOut"),
            FriendsBG.getInstance().getConfig().getInt("mysql.dataLeak"),
            FriendsBG.getInstance().getConfig().getInt("mysql.timeOut")));

    // Private variables and singletons :
    private final DBAccess dbAccess;
    private static final boolean enabled = FriendsBG.getInstance().getConfig().getBoolean("mysql.use");

    // Launch part :
    DBManager(DBCredentials dbCredentials)
    {
        this.dbAccess = new DBAccess(dbCredentials);
    }

    // Functions part :
    public DBAccess getDBAccess() {return dbAccess;}

    public static void init()
    {
        for(DBManager dbM : values())
        {
            if(!enabled)
            {
                dbM.dbAccess.init();
            }else {
                System.out.println("§cPlease§f, §cconfigure §3SQL §aconnections §cin §7config§f.§7yml §cfile§f, §cfor use this §6TM§f-§bBungeeAPI §c!");
                break;
            }
        }
    }

    public static void stop()
    {
        for(DBManager dbM : values())
        {
            dbM.dbAccess.stop();
        }
    }
}
