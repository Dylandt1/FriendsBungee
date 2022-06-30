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

import fr.patapom.fbg.FriendsBG;

public enum DBManager
{
    // All DB access supported :
    DATABASE_ACCESS(new DBCredentials(FriendsBG.getInstance().getConfig().getString("mysql.host"),
            FriendsBG.getInstance().getConfig().getInt("mysql.port"),
            FriendsBG.getInstance().getConfig().getString("mysql.dbName"),
            FriendsBG.getInstance().getConfig().getString("mysql.user"),
            FriendsBG.getInstance().getConfig().getString("mysql.password")));

    // Private variables and singletons :
    private DBAccess dbAccess;
    private static boolean enabled = FriendsBG.getInstance().getConfig().getBoolean("use");

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
            dbM.dbAccess.init();
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
