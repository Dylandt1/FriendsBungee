package fr.patapom.commons.party;

import api.data.manager.json.SerializationManager;
import api.data.manager.redis.RedisAccess;
import api.files.Files;
import fr.patapom.commons.party.PartyManager;
import fr.patapom.fbg.FriendsBG;
import fr.patapom.fbg.utils.exceptions.PManagerNotFoundException;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.io.File;
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
    private final FriendsBG plugin;
    private final File saveDirectory;
    private final UUID groupId;
    private ProxiedPlayer owner;

    private final Configuration config = FriendsBG.getInstance().getConfig();
    private final String lvPath = config.getString("groups.levels");
    private final int DEFAULT = config.getInt(lvPath+".defaults");

    private final boolean redisEnable = FriendsBG.getInstance().redisEnable;
    private RedisAccess redisAccess;
    private String REDIS_KEY = "pManager:";

    /**
     * Public methods :
     */

    // New party :
    public PartyProvider(ProxiedPlayer owner)
    {
        this.plugin = FriendsBG.getInstance();
        this.groupId = UUID.randomUUID();
        this.owner = owner;
        this.saveDirectory = new File(plugin.getDataFolder(), "/managers/pManagers/");
        if(redisEnable)
        {
            this.redisAccess = RedisAccess.getInstance();
            this.REDIS_KEY = REDIS_KEY+groupId.toString();
        }
    }

    // Existing party :
    public PartyProvider(UUID groupID)
    {
        this.plugin = FriendsBG.getInstance();
        this.groupId = groupID;
        this.saveDirectory = new File(plugin.getDataFolder(), "/managers/pManagers/");
        if(redisEnable)
        {
            this.redisAccess = RedisAccess.getInstance();
            this.REDIS_KEY = REDIS_KEY+groupId.toString();
        }
    }

    public PartyManager getPManager() throws PManagerNotFoundException
    {
        PartyManager pManager;
        final File file = new File(saveDirectory, groupId.toString()+".json");
        final SerializationManager serManager = plugin.getSerializationManager();
        int partyLength = config.getInt("groups.levels.default");
        if(redisEnable)
        {
            pManager = getPManagerOnRedis();
            if(pManager == null)
            {
                if(file.exists())
                {
                    final String json = Files.loadFile(file);
                    pManager = (PartyManager) serManager.deserialize(json, PartyManager.class);
                }else {
                    for(String s : config.getStringList("groups.levels"))
                    {
                        if(owner.hasPermission("fgb.group."+s)) {partyLength = config.getInt("groups.levels."+s);}
                    }
                    pManager = new PartyManager(owner , groupId, partyLength);
                    final String json = serManager.serialize(pManager);
                    Files.save(file, json);
                }
                setPManagerOnRedis(pManager);
            }
        }else
        {
            if(file.exists())
            {
                final String json = Files.loadFile(file);
                pManager = (PartyManager) serManager.deserialize(json, PartyManager.class);
            }else {
                pManager = new PartyManager(owner , groupId, partyLength);
                final String json = serManager.serialize(pManager);
                Files.save(file, json);
            }
        }
        return pManager;
    }

    public void save(PartyManager partyManager)
    {
        if(redisEnable)
        {
            setPManagerOnRedis(partyManager);
        }
        final File file = new File(saveDirectory, groupId.toString()+".json");
        final SerializationManager serManager = plugin.getSerializationManager();
        final String json = serManager.serialize(partyManager);
        Files.save(file, json);
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
