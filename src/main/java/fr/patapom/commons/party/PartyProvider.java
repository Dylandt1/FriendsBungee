package fr.patapom.commons.party;

import fr.patapom.fbg.data.manager.redis.RedisAccess;
import fr.patapom.fbg.FriendsBG;
import fr.patapom.tmapi.exceptions.ManagerNotFoundException;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.UUID;

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

public class PartyProvider
{
    private ProxiedPlayer player;
    private final UUID groupId;

    private final Configuration config = FriendsBG.getInstance().getConfig();
    private final String lvPath = config.getString("groups.levels");

    private final boolean redisEnable = FriendsBG.getInstance().redisEnable;
    private RedisAccess redisAccess;
    private String REDIS_KEY = "pManager:";

    /**
     * Public methods :
     */

    public PartyProvider(ProxiedPlayer player)
    {
        this.player = player;
        this.groupId = UUID.randomUUID();
        if(redisEnable)
        {
            this.redisAccess = RedisAccess.getInstance();
            this.REDIS_KEY = REDIS_KEY+groupId.toString();
        }
    }

    public PartyProvider(UUID groupId)
    {
        this.groupId = groupId;
        if(redisEnable)
        {
            this.redisAccess = RedisAccess.getInstance();
            this.REDIS_KEY = REDIS_KEY+groupId.toString();
        }
    }

    public PartyManager getPManager() throws ManagerNotFoundException
    {
        PartyManager pManager;
        int partyLength = config.getInt("groups.levels.default");
        if(redisEnable)
        {
            pManager = getPManagerOnRedis();
            if(pManager == null)
            {
                for(String s : config.getStringList("groups.levels"))
                {
                    if(player.hasPermission("fgb.group."+s)) {partyLength = config.getInt("groups.levels."+s);}
                }
                pManager = new PartyManager(player , groupId, partyLength);
                setPManagerOnRedis(pManager);
            }
        }else
        {
            if(FriendsBG.parties.containsKey(groupId))
            {
                pManager = FriendsBG.parties.get(groupId);
            }else {
                pManager = new PartyManager(player , groupId, partyLength);
                FriendsBG.parties.put(groupId, pManager);
            }
        }
        return pManager;
    }

    public void save(PartyManager partyManager)
    {
        if(redisEnable) {setPManagerOnRedis(partyManager);return;}
        FriendsBG.parties.replace(groupId, partyManager);
    }

    public boolean pExist()
    {
        if(redisEnable) {return getPManagerOnRedis() != null;}
        return FriendsBG.parties.containsKey(groupId);
    }

    /**
     * Privates methods :
     */

    private void setPManagerOnRedis(PartyManager partyManager)
    {
        final RedissonClient redisCli = redisAccess.getRedisCli();
        final RBucket<PartyManager> pBucket = redisCli.getBucket(REDIS_KEY);
        pBucket.set(partyManager);
    }

    private PartyManager getPManagerOnRedis()
    {
        final RedissonClient redisCli = redisAccess.getRedisCli();
        final RBucket<PartyManager> pBucket = redisCli.getBucket(REDIS_KEY);
        return pBucket.get();
    }
}
