package fr.patapom.friendsbg.fbg.db;

import fr.patapom.friendsbg.fbg.FriendsBG;

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
