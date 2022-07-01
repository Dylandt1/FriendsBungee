package fr.patapom.commons.friends;

import api.data.manager.json.SerializationManager;
import api.data.manager.redis.RedisAccess;
import api.data.manager.sql.DBAccess;
import api.data.manager.sql.DBManager;
import api.files.Files;
import fr.patapom.commons.friends.FriendsManager;
import fr.patapom.fbg.FriendsBG;
import fr.patapom.fbg.utils.exceptions.FManagerNotFoundException;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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

public class FriendsProvider
{
    private final FriendsBG plugin;
    private final File saveDirectory;
    private final UUID uuid;

    private static final String prefixTables = DBAccess.getPrefixTables();
    private static final String tableName = DBAccess.getTableName();
    private static final String tableAllow = "fbg_allow";

    private final boolean redisEnable = FriendsBG.getInstance().redisEnable;
    private final boolean sqlEnable = FriendsBG.getInstance().sqlEnable;
    private RedisAccess redisAccess;
    private String REDIS_KEY = "fManager:";

    /**
     * Public methods :
     */

    public FriendsProvider(UUID playerUUID)
    {
        this.plugin = FriendsBG.getInstance();
        this.saveDirectory = new File(plugin.getDataFolder(), "/managers/fManagers/");
        this.uuid = playerUUID;
        if(redisEnable)
        {
            this.redisAccess = RedisAccess.getInstance();
            this.REDIS_KEY = REDIS_KEY+uuid.toString();
        }
    }

    public FriendsManager getFManager() throws FManagerNotFoundException
    {
        FriendsManager fManager;
        final File file = new File(saveDirectory, uuid.toString()+".json");
        final SerializationManager serManager = plugin.getSerializationManager();
        if(redisEnable && sqlEnable)
        {
            fManager = getFManagerOnRedis();
            if(fManager == null)
            {
                fManager = getFManagerOnMySQL();
                setFManagerOnRedis(fManager);
            }
        }else if(redisEnable)
        {
            fManager = getFManagerOnRedis();
            if(fManager == null)
            {
                if(file.exists())
                {
                    final String json = Files.loadFile(file);
                    fManager = (FriendsManager) serManager.deserialize(json, FriendsManager.class);
                }else {
                    fManager = new FriendsManager(uuid, false, new HashMap<>());
                    final String json = serManager.serialize(fManager);
                    Files.save(file, json);
                }
                setFManagerOnRedis(fManager);
            }
        }else if(sqlEnable)
        {
            if(file.exists())
            {
                final String json = Files.loadFile(file);
                fManager = (FriendsManager) serManager.deserialize(json, FriendsManager.class);
            }else {
                fManager = getFManagerOnMySQL();
                final String json = serManager.serialize(fManager);
                Files.save(file, json);
            }
        }else
        {
            if(file.exists())
            {
                final String json = Files.loadFile(file);
                fManager = (FriendsManager) serManager.deserialize(json, FriendsManager.class);
            }else {
                fManager = new FriendsManager(uuid, false, new HashMap<>());
                final String json = serManager.serialize(fManager);
                Files.save(file, json);
            }
        }
        return fManager;
    }

    public void save(FriendsManager friendsManager)
    {
        final File file = new File(saveDirectory, uuid.toString()+".json");
        final SerializationManager serManager = plugin.getSerializationManager();
        final String json = serManager.serialize(friendsManager);

        if(redisEnable)
        {
            setFManagerOnRedis(friendsManager);
        }
        Files.save(file, json);
    }

    public void updateDB()
    {
        PreparedStatement ps1;
        PreparedStatement ps2;
        PreparedStatement ps3;
        try
        {
            Connection connection = DBManager.DATABASE_ACCESS.getDBAccess().getConnection();
            FriendsManager fManager = getFManager();

            ps1 = connection.prepareStatement("UPDATE "+tableAllow+" SET isAllow = ? WHERE uuid = ?");
            if(fManager.isAllow())
            {
                ps1.setInt(1, 1);
            }else {
                ps1.setInt(1, 0);
            }
            ps1.setString(2, uuid.toString());
            ps1.executeUpdate();
            ps1.close();

            ps2 = connection.prepareStatement("DELETE FROM "+prefixTables+tableName+" WHERE player_uuid = ?");
            ps2.setString(1, uuid.toString());
            ps2.executeUpdate();
            ps2.close();

            for(String s : fManager.getFriendsMap().keySet())
            {
                ps3 = connection.prepareStatement("INSERT INTO "+prefixTables+tableName+" (player_uuid, friend_uuid, friend_name) VALUES (?, ?, ?)");
                ps3.setString(1, uuid.toString());
                ps3.setString(2, fManager.getFriendsMap().get(s).toString());
                ps3.setString(3, s);
                ps3.executeUpdate();
                ps3.close();
            }

            connection.close();
        }catch (SQLException | FManagerNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Privates methods :
     */

    private void setFManagerOnRedis(FriendsManager friendsManager)
    {
        final RedissonClient redisCli = redisAccess.getRedisCli();
        final RBucket<FriendsManager> fBucket = redisCli.getBucket(REDIS_KEY);
        fBucket.set(friendsManager);
    }

    private FriendsManager getFManagerOnRedis()
    {
        final RedissonClient redisCli = redisAccess.getRedisCli();
        final RBucket<FriendsManager> fBucket = redisCli.getBucket(REDIS_KEY);
        return fBucket.get();
    }

    private FriendsManager getFManagerOnMySQL()
    {
        PreparedStatement ps1;
        PreparedStatement ps2;
        ResultSet rs1;
        ResultSet rs2;
        FriendsManager fManager = null;
        try {
            Connection connection = DBManager.DATABASE_ACCESS.getDBAccess().getConnection();
            ps1 = connection.prepareStatement("SELECT * FROM "+prefixTables+tableName+" WHERE player_uuid = ?");
            ps2 = connection.prepareStatement("SELECT isAllow FROM "+tableAllow+" WHERE uuid = ?");

            ps1.setString(1, uuid.toString());
            ps2.setString(1, uuid.toString());

            rs1 = ps1.executeQuery();
            rs2 = ps2.executeQuery();
            if(rs2.next())
            {
                Map<String, UUID> fList = new HashMap<>();
                while(rs1.next())
                {
                    fList.put(rs1.getString("friend_name"), UUID.fromString(rs1.getString("friend_uuid")));
                }
                boolean isAllow = rs2.getInt("isAllow") == 1;
                fManager = new FriendsManager(uuid, isAllow, fList);
            }else {
                fManager = createFManager(uuid);
            }
            ps1.close();
            ps2.close();
            connection.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return fManager;
    }

    private FriendsManager createFManager(UUID playerUUID)
    {
        PreparedStatement ps;
        try {
            Connection connection = DBManager.DATABASE_ACCESS.getDBAccess().getConnection();
            ps = DBManager.DATABASE_ACCESS.getDBAccess().getConnection().prepareStatement("INSERT INTO "+tableAllow+" (uuid, isAllow) VALUES (?, ?)");
            FriendsManager fManager = new FriendsManager(playerUUID, false, new HashMap<>());
            ps.setString(1, playerUUID.toString());
            ps.setInt(2, 0);
            ps.executeUpdate();
            ps.close();
            connection.close();
            return fManager;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
