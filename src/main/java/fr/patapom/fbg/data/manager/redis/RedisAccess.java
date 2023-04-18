package fr.patapom.fbg.data.manager.redis;

import fr.patapom.fbg.FriendsBG;
import fr.patapom.tmapi.data.manager.redis.RedisCredentials;
import net.md_5.bungee.config.Configuration;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

/**
 * This file is part of FriendsBungee (FriendsBG-Free), a bungeecord friends plugin.
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

public class RedisAccess
{
    public static RedisAccess instance;

    private static final Configuration cfg = FriendsBG.getInstance().getConfig();
    private final RedissonClient redisCli;

    public RedisAccess(RedisCredentials credentials)
    {
        instance = this;
        this.redisCli = initRedis(credentials);
    }

    public RedissonClient initRedis(RedisCredentials credentials)
    {
        final Config config = new Config();

        config.setCodec(new JsonJacksonCodec());
        config.setThreads(cfg.getInt("redis.threads"));
        config.setNettyThreads(cfg.getInt("redis.nettyThreads"));
        config.useSingleServer()
                .setAddress(credentials.getUrl())
                .setPassword(credentials.getPassword())
                .setClientName(credentials.getClientName())
                .setDatabase(cfg.getInt("redis.dataBase"));

        return Redisson.create(config);
    }

    public static RedisAccess getInstance() {return instance;}

    public RedissonClient getRedisCli() {return redisCli;}

    public static void init()
    {
        String host = cfg.getString("redis.host");
        int port = cfg.getInt("redis.port");
        String password = cfg.getString("redis.password");
        String clientName = cfg.getString("redis.clientName");
        new RedisAccess(new RedisCredentials(host,port,password,clientName));
    }

    public static void close()
    {
        if(getInstance().getRedisCli() != null)
        {
            RedisAccess.instance.getRedisCli().shutdown();
        }
    }
}
