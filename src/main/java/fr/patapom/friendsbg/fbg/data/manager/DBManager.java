package fr.patapom.friendsbg.fbg.data.manager;

import fr.tmmods.tmapi.bungee.TMBungeeAPI;
import fr.tmmods.tmapi.data.manager.sql.DBAccess;
import fr.tmmods.tmapi.data.manager.sql.DBCredentials;

/**
 * This file is part of TM-API, a Spigot/BungeeCord API.
 *
 * TM-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TM-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

public enum DBManager
{
    FBG_DATABASE(new DBCredentials(TMBungeeAPI.getInstance().getConfig().getString("mysql.host"),
            TMBungeeAPI.getInstance().getConfig().getInt("mysql.port"),
            TMBungeeAPI.getInstance().getConfig().getString("mysql.dbName"),
            TMBungeeAPI.getInstance().getConfig().getString("mysql.user"),
            TMBungeeAPI.getInstance().getConfig().getString("mysql.password"),
            TMBungeeAPI.getInstance().getConfig().getInt("mysql.maxPoolSize"),
            TMBungeeAPI.getInstance().getConfig().getInt("mysql.maxLifeTime"),
            TMBungeeAPI.getInstance().getConfig().getInt("mysql.poolTimeout"),
            TMBungeeAPI.getInstance().getConfig().getInt("mysql.dataleak"),
            TMBungeeAPI.getInstance().getConfig().getInt("mysql.timeout")));

    private final DBAccess dbAccess;

    DBManager(DBCredentials credentials) {this.dbAccess = new DBAccess(credentials);}

    public DBAccess getDbAccess() {return dbAccess;}

    public static void initAllConnections() {for(DBManager dbM : values()) {dbM.dbAccess.init();}}

    public static void closeAllConnections() {for(DBManager dbM : values()) {dbM.dbAccess.stop();}}
}