package fr.patapom.friendsbg.fbg.data.manager;

import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.tmmods.tmapi.bungee.TMBungeeAPI;
import fr.tmmods.tmapi.data.manager.redis.RedisAccess;
import fr.tmmods.tmapi.data.manager.redis.RedisCredentials;

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

public enum RedisManager
{
    FBG_REDIS(new RedisCredentials(FriendsBG.getInstance().getConfig().getString("redis.host"),
            FriendsBG.getInstance().getConfig().getInt("redis.port"),
            FriendsBG.getInstance().getConfig().getString("redis.password"),
            FriendsBG.getInstance().getConfig().getString("redis.clientName"),
            FriendsBG.getInstance().getConfig().getInt("redis.threads"),
            FriendsBG.getInstance().getConfig().getInt("redis.nettyThreads"),
            FriendsBG.getInstance().getConfig().getInt("redis.dataBase")));

    private final RedisAccess redisAccess;

    RedisManager(RedisCredentials credentials) {this.redisAccess = new RedisAccess(credentials);}

    public RedisAccess getRedisAccess() {return redisAccess;}

    public static void initAllConnections() {for(RedisManager rdm : values()) {rdm.redisAccess.init();}}

    public static void closeAllConnections() {for(RedisManager rdm : values()) {rdm.redisAccess.getRedisCli().shutdown();}}
}