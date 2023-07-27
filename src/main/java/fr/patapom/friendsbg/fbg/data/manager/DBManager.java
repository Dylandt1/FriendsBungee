package fr.patapom.friendsbg.fbg.data.manager;

import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.tmmods.tmapi.data.manager.sql.DBAccess;
import fr.tmmods.tmapi.data.manager.sql.DBCredentials;

public enum DBManager
{
    FBG_DATABASE(new DBCredentials(FriendsBG.getInstance().getConfig().getString("mysql.host"),
            FriendsBG.getInstance().getConfig().getInt("mysql.port"),
            FriendsBG.getInstance().getConfig().getString("mysql.dbName"),
            FriendsBG.getInstance().getConfig().getString("mysql.user"),
            FriendsBG.getInstance().getConfig().getString("mysql.password"),
            FriendsBG.getInstance().getConfig().getInt("mysql.maxPoolSize"),
            FriendsBG.getInstance().getConfig().getInt("mysql.maxLifeTime"),
            FriendsBG.getInstance().getConfig().getInt("mysql.poolTimeout"),
            FriendsBG.getInstance().getConfig().getInt("mysql.dataleak"),
            FriendsBG.getInstance().getConfig().getInt("mysql.timeout")));

    private final DBAccess dbAccess;

    DBManager(DBCredentials credentials) {this.dbAccess = new DBAccess(credentials);}

    public DBAccess getDbAccess() {return dbAccess;}

    public static void initAllConnections() {for(DBManager dbM : values()) {dbM.dbAccess.init();}}

    public static void closeAllConnections() {for(DBManager dbM : values()) {dbM.dbAccess.stop();}}
}
