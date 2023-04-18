package fr.patapom.commons.friends;

import fr.patapom.fbg.data.manager.sql.DBManager;
import fr.patapom.fbg.data.manager.sql.SqlManager;
import fr.patapom.tmapi.data.manager.Json.SerializationManager;
import fr.patapom.fbg.data.manager.redis.RedisAccess;
import fr.patapom.tmapi.data.manager.Files;
import fr.patapom.fbg.FriendsBG;
import fr.patapom.tmapi.exceptions.ManagerNotFoundException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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

    private final ProxiedPlayer player;
    private final UUID uuid;
    private final String name;
    private final String displayName;

    private final boolean redisEnable = FriendsBG.getInstance().redisEnable;
    private final boolean sqlEnable = FriendsBG.getInstance().sqlEnable;
    private RedisAccess redisAccess;
    private String REDIS_KEY = "fManager:";

    /**
     * FriendsProvider initiator
     */
    public FriendsProvider(UUID playerUUID)
    {
        // Init all variables
        this.player = ProxyServer.getInstance().getPlayer(playerUUID);
        this.plugin = FriendsBG.getInstance();
        this.saveDirectory = new File(plugin.getDataFolder(), "/managers/fManagers/");
        this.uuid = playerUUID;
        this.name = player.getName();
        this.displayName = player.getDisplayName();
        if(redisEnable)
        {
            this.redisAccess = RedisAccess.getInstance();
            // Format REDIS_KEY to fManager:XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
            this.REDIS_KEY = REDIS_KEY+uuid.toString();
        }
    }

    /**
     * Function to get FriendsManager objects on Json files, Mysql server or Redis server
     */
    public FriendsManager getFManager() throws ManagerNotFoundException
    {
        // Init null FriendsManager variable
        FriendsManager fManager;
        // Set save directory and get SerializationManager instance for JSon files
        final File file = new File(saveDirectory, uuid.toString()+".json");
        final SerializationManager serManager = plugin.getSerializationManager();

        if(redisEnable && sqlEnable)
        {
            // Get FriendsManager on Redis or Mysql server
            fManager = getFManagerOnRedis();
            if(fManager == null)
            {
                fManager = getFManagerOnMySQL();
                setFManagerOnRedis(fManager);
            }
        }else if(redisEnable)
        {
            // Get FriendsManager on Redis server
            fManager = getFManagerOnRedis();

            if(fManager == null)
            {
                // Get FriendsManager on Json files or create new profile by default values
                if(file.exists())
                {
                    final String json = Files.loadFile(file);
                    fManager = (FriendsManager) serManager.deserialize(json, FriendsManager.class);
                }else {
                    fManager = new FriendsManager(uuid, name, displayName, true, true, null, new HashMap<>());
                    final String json = serManager.serialize(fManager);
                    Files.save(file, json);
                }
                setFManagerOnRedis(fManager);
            }
        }else if(sqlEnable)
        {
            // Get FriendsManager on Mysql server
            fManager = getFManagerOnMySQL();

            if(fManager == null)
            {
                if(FriendsBG.fManagers.containsKey(uuid))
                {
                    // Get FriendsManager on plugin cache
                    fManager = FriendsBG.fManagers.get(uuid);
                }else
                {
                    if(file.exists())
                    {
                        // Get FriendsManager on Json files or create new profile by default values
                        final String json = Files.loadFile(file);
                        fManager = (FriendsManager) serManager.deserialize(json, FriendsManager.class);
                    }else {
                        fManager = getFManagerOnMySQL();
                        final String json = serManager.serialize(fManager);
                        Files.save(file, json);
                    }
                }
            }
        }else
        {
            if(FriendsBG.fManagers.containsKey(uuid))
            {
                // Get FriendsManager on plugin cache
                fManager = FriendsBG.fManagers.get(uuid);
            }else {
                // Get FriendsManager on Json files or create new profile by default values
                if(file.exists())
                {
                    final String json = Files.loadFile(file);
                    fManager = (FriendsManager) serManager.deserialize(json, FriendsManager.class);
                }else {
                    fManager = new FriendsManager(uuid, name, displayName, true, true, null, new HashMap<>());
                    final String json = serManager.serialize(fManager);
                    Files.save(file, json);
                }
            }
        }
        return fManager;
    }

    /**
     * Function to save FriendsManager on Redis server or Json file
     */
    public void save(FriendsManager friendsManager)
    {
        // Save FriendsManager on Redis server
        if(redisEnable) {setFManagerOnRedis(friendsManager);return;}

        // Save FriendsManager on plugin cache and Json file for security
        FriendsBG.fManagers.replace(uuid, friendsManager);
        final File file = new File(saveDirectory, uuid.toString()+".json");
        final SerializationManager serManager = plugin.getSerializationManager();
        final String json = serManager.serialize(friendsManager);
        Files.save(file, json);
    }

    /**
     * Function to save FriendsManager on Mysql server
     */
    public void updateDB()
    {
        // Init null PreparedStatements n°1
        PreparedStatement ps1;
        // Init null PreparedStatements n°2 and associated ResulSet
        PreparedStatement ps2;
        ResultSet rs2;
        try
        {
            // Get FriendsManager and create if not exist
            FriendsManager fManager = getFManager();
            // Init DB connection
            Connection connection = DBManager.DATABASE_ACCESS.getDBAccess().getConnection();
            // Init PreparedStatement n°1 and set values
            ps1 = connection.prepareStatement("UPDATE "+SqlManager.getPrefixTables()+SqlManager.getTableName()+" SET name = ?, displayName = ?, requestsAllow = ?, msgAllow = ?, groupId = ? WHERE uuid = ?");
            ps1.setString(1, name);
            ps1.setString(2, displayName);
            ps1.setInt(3, fManager.requestsAllow()?1:0);
            ps1.setInt(4, fManager.msgAllow()?1:0);
            ps1.setString(5, fManager.getGroupId().toString());
            ps1.setString(6, uuid.toString());
            // Execute PreparedStatement n°1 and close
            ps1.executeUpdate();
            ps1.close();

            // Init PreparedStatement n°2 and set values
            ps2 = connection.prepareStatement("SELECT * FROM "+SqlManager.getPrefixTables()+SqlManager.getFTable()+" WHERE uuid = ?");
            ps2.setString(1, fManager.getUUID().toString());
            // Execute PreparedStatement and init ResultSet
            rs2 = ps2.executeQuery();

            List<UUID> fUUIDList = new ArrayList<>();

            while(rs2.next())
            {
                fUUIDList.add(UUID.fromString(rs2.getString("friendUUID")));
            }

            for(String fName : fManager.getFriendsMap().keySet())
            {
                // Add new friends in DB
                if(!fUUIDList.contains(fManager.getFriendsMap().get(fName)))
                {
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO "+SqlManager.getPrefixTables()+SqlManager.getFTable()+" (uuid, friendUUID, friendName) VALUES (?, ?, ?)");
                    ps.setString(1, uuid.toString());
                    ps.setString(2, fManager.getFriendsMap().get(fName).toString());
                    ps.setString(3, fName);
                    ps.executeUpdate();
                    ps.close();
                    return;
                }
            }

            // Ajoutter la suppression des anciens amis

            ps2.close();
            connection.close();
        }catch (SQLException | ManagerNotFoundException e) {
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
        final File file = new File(saveDirectory, uuid.toString()+".json");
        final SerializationManager serManager = plugin.getSerializationManager();
        FriendsManager fManager = null;

        if(file.exists())
        {
            // Get FriendsManager on Json files for no db or redis usage transition
            final String json = Files.loadFile(file);
            fManager = (FriendsManager) serManager.deserialize(json, FriendsManager.class);
            if(redisEnable) {setFManagerOnRedis(fManager);}
            createFManager(fManager);
            return fManager;
        }

        try {
            Connection connection = DBManager.DATABASE_ACCESS.getDBAccess().getConnection();
            PreparedStatement ps1 = connection.prepareStatement("SELECT * FROM "+SqlManager.getPrefixTables()+SqlManager.getTableName()+" WHERE uuid = ?");
            PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM "+SqlManager.getPrefixTables()+SqlManager.getFTable()+" WHERE uuid = ?");

            ps1.setString(1, uuid.toString());
            ps2.setString(1, uuid.toString());

            ResultSet rs1 = ps1.executeQuery();
            ResultSet rs2 = ps2.executeQuery();
            if(rs1.next())
            {
                Map<String, UUID> fList = new HashMap<>();
                while (rs2.next())
                {
                    fList.put(rs2.getString("friendName"), UUID.fromString(rs2.getString("friendUUID")));
                }
                String displayName = rs1.getString("displayName");
                boolean requestsAllow = rs1.getInt("requestsAllow") == 1;
                boolean msgAllow = rs1.getInt("msgAllow") == 1;
                UUID groupId = UUID.fromString(rs1.getString("groupId"));
                fManager = new FriendsManager(uuid, name, displayName, requestsAllow, msgAllow, groupId, fList);
            }else {
                fManager = createFManager();
            }
            ps1.close();
            connection.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return fManager;
    }

    private FriendsManager createFManager()
    {
        PreparedStatement ps;
        try {
            Connection connection = DBManager.DATABASE_ACCESS.getDBAccess().getConnection();
            ps = DBManager.DATABASE_ACCESS.getDBAccess().getConnection().prepareStatement("INSERT INTO "+SqlManager.getPrefixTables()+SqlManager.getTableName()+" (uuid, name, displayName, requestsAllow, msgAllow, groupId) VALUES (?, ?, ?, ?, ?, ?)");
            FriendsManager fManager = new FriendsManager(uuid, name, displayName, true, true, null, new HashMap<>());
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.setString(3, displayName);
            ps.setInt(4, 1);
            ps.setInt(5, 1);
            ps.setString(6, null);
            ps.executeUpdate();
            ps.close();
            connection.close();
            return fManager;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createFManager(FriendsManager fManager)
    {
        try {
            Connection connection = DBManager.DATABASE_ACCESS.getDBAccess().getConnection();
            PreparedStatement ps1 = DBManager.DATABASE_ACCESS.getDBAccess().getConnection().prepareStatement("SELECT * FROM "+SqlManager.getPrefixTables()+SqlManager.getTableName()+" WHERE uuid = ?");
            ResultSet rs1 = ps1.executeQuery();
            if(!rs1.next()) {createFManager();}

            PreparedStatement ps2 = DBManager.DATABASE_ACCESS.getDBAccess().getConnection().prepareStatement("INSERT INTO "+SqlManager.getPrefixTables()+SqlManager.getTableName()+" (uuid, name, displayName, requestsAllow, msgAllow, groupId) VALUES (?, ?, ?, ?, ?, ?)");
            ps2.setString(1, uuid.toString());
            ps2.setString(2, name);
            ps2.setString(3, fManager.getDisplayName());
            ps2.setInt(4, fManager.requestsAllow()?1:0);
            ps2.setInt(5, fManager.msgAllow()?1:0);
            ps2.setString(6, fManager.getGroupId().toString());
            ps2.executeUpdate();
            ps2.close();
            connection.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
