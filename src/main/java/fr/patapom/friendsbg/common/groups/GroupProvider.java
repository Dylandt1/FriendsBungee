package fr.patapom.friendsbg.common.groups;

import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.patapom.friendsbg.fbg.data.manager.RedisManager;
import fr.tmmods.tmapi.data.manager.redis.RedisAccess;
import fr.tmmods.tmapi.exceptions.ManagerNotFoundException;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.UUID;

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

public class GroupProvider
{
    private ProxiedPlayer player;
    private final UUID groupId;

    private final Configuration config = FriendsBG.getInstance().getConfig();

    private final boolean redisEnable = FriendsBG.getInstance().redisEnable;
    private RedisAccess redisAccess;
    private String REDIS_KEY = "group:";

    /**
     * Public methods :
     */

    public GroupProvider(ProxiedPlayer player)
    {
        this.player = player;
        this.groupId = UUID.randomUUID();
        if(redisEnable)
        {
            this.redisAccess = RedisManager.FBG_REDIS.getRedisAccess();
            this.REDIS_KEY = REDIS_KEY+groupId.toString();
        }
    }

    public GroupProvider(UUID groupId)
    {
        this.groupId = groupId;
        if(redisEnable)
        {
            this.redisAccess = RedisManager.FBG_REDIS.getRedisAccess();
            this.REDIS_KEY = REDIS_KEY+groupId.toString();
        }
    }

    public GroupManager getGManager() throws ManagerNotFoundException
    {
        GroupManager group;
        // Maximum members allowed in group
        int groupLength = config.getInt("groups.lengths.default");

        if(redisEnable)
        {
            group = getGManagerOnRedis();
            if(group == null)
            {
                for(String s : config.getStringList("groups.lengths"))
                {
                    if(player.hasPermission("fgb.group."+s)) {groupLength = config.getInt("groups.lengths."+s);}
                }
                group = new GroupManager(player , groupId, groupLength);
                setGManagerOnRedis(group);
            }
        }else
        {
            if(FriendsBG.getInstance().groups.containsKey(groupId))
            {
                group = FriendsBG.getInstance().groups.get(groupId);
            }else {
                group = new GroupManager(player , groupId, groupLength);
                FriendsBG.getInstance().groups.put(groupId, group);
            }
        }
        return group;
    }

    public void save(GroupManager groupManager)
    {
        if(redisEnable) {setGManagerOnRedis(groupManager);return;}
        FriendsBG.getInstance().groups.remove(groupId);
        FriendsBG.getInstance().groups.put(groupId, groupManager);
    }

    public void delete()
    {
        if(redisEnable)
        {
            final RedissonClient redisCli = redisAccess.getRedisCli();
            final RBucket<GroupManager> gBucket = redisCli.getBucket(REDIS_KEY);
            gBucket.delete();
        }else {
            FriendsBG.getInstance().groups.remove(groupId);
        }
    }

    public boolean gExist()
    {
        if(redisEnable) {return getGManagerOnRedis() != null;}
        return FriendsBG.getInstance().groups.containsKey(groupId);
    }

    /**
     * Privates methods :
     */

    private void setGManagerOnRedis(GroupManager groupManager)
    {
        final RedissonClient redisCli = redisAccess.getRedisCli();
        final RBucket<GroupManager> gBucket = redisCli.getBucket(REDIS_KEY);
        gBucket.set(groupManager);
    }

    private GroupManager getGManagerOnRedis()
    {
        final RedissonClient redisCli = redisAccess.getRedisCli();
        final RBucket<GroupManager> gBucket = redisCli.getBucket(REDIS_KEY);
        return gBucket.get();
    }
}
