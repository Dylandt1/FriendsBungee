package fr.patapom.friendsbg.fbg.data.manager;

import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.tmmods.tmapi.data.manager.redis.RedisAccess;
import fr.tmmods.tmapi.data.manager.redis.RedisCredentials;

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
