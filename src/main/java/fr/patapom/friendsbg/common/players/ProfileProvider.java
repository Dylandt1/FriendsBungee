package fr.patapom.friendsbg.common.players;

import fr.patapom.friendsbg.fbg.data.manager.DBManager;
import fr.patapom.friendsbg.fbg.data.manager.RedisManager;
import fr.patapom.friendsbg.fbg.json.Files;
import fr.patapom.friendsbg.fbg.json.SerializationManager;
import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.tmmods.tmapi.data.manager.redis.RedisAccess;
import fr.tmmods.tmapi.data.manager.sql.SqlManager;
import fr.tmmods.tmapi.exceptions.ManagerNotFoundException;
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

public class ProfileProvider
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

    private String prefixTables;

    /**
     * FriendsProvider initiator
     */
    public ProfileProvider(UUID playerUUID)
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
            this.redisAccess = RedisManager.FBG_REDIS.getRedisAccess();
            this.REDIS_KEY = REDIS_KEY+uuid.toString();
        }
        this.prefixTables = plugin.getConfig().getString("mysql.prefixTables");
    }

    /**
     * Function to get FriendsManager objects on Json files, Mysql server or Redis server
     */
    public ProfileManager getFManager() throws ManagerNotFoundException
    {
        // Init null FriendsManager variable
        ProfileManager fManager;
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
                    fManager = (ProfileManager) serManager.deserialize(json, ProfileManager.class);
                }else {
                    fManager = new ProfileManager(uuid, name, displayName, true, true, null, new HashMap<>());
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
                        fManager = (ProfileManager) serManager.deserialize(json, ProfileManager.class);
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
                    fManager = (ProfileManager) serManager.deserialize(json, ProfileManager.class);
                }else {
                    fManager = new ProfileManager(uuid, name, displayName, true, true, null, new HashMap<>());
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
    public void save(ProfileManager friendsManager)
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
            ProfileManager fManager = getFManager();
            // Init DB connection
            Connection connection = DBManager.FBG_DATABASE.getDbAccess().getConnection();
            // Init PreparedStatement n°1 and set values
            ps1 = connection.prepareStatement("UPDATE " + prefixTables+FriendsBG.getInstance().sqlProfilesManager.getTableName() + "SET name = ?, displayName = ?, requestsAllow = ?, msgAllow = ?, groupId = ? WHERE uuid = ?");
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
            ps2 = connection.prepareStatement("SELECT * FROM "+prefixTables+FriendsBG.getInstance().sqlFListManager.getTableName()+" WHERE uuid = ?");
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
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO "+prefixTables+FriendsBG.getInstance().sqlFListManager.getTableName()+" (uuid, friendUUID, friendName) VALUES (?, ?, ?)");
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

    private void setFManagerOnRedis(ProfileManager friendsManager)
    {
        final RedissonClient redisCli = redisAccess.getRedisCli();
        final RBucket<ProfileManager> fBucket = redisCli.getBucket(REDIS_KEY);
        fBucket.set(friendsManager);
    }

    private ProfileManager getFManagerOnRedis()
    {
        final RedissonClient redisCli = redisAccess.getRedisCli();
        final RBucket<ProfileManager> fBucket = redisCli.getBucket(REDIS_KEY);
        return fBucket.get();
    }

    private ProfileManager getFManagerOnMySQL()
    {
        PreparedStatement ps1;
        PreparedStatement ps2;
        ResultSet rs1;
        ResultSet rs2;
        ProfileManager fManager = null;
        try {
            Connection connection = DBManager.FBG_DATABASE.getDbAccess().getConnection();
            ps1 = connection.prepareStatement("SELECT * FROM "+prefixTables+FriendsBG.getInstance().sqlProfilesManager.getTableName()+" WHERE uuid = ?");
            ps2 = connection.prepareStatement("SELECT * FROM "+prefixTables+FriendsBG.getInstance().sqlFListManager.getTableName()+" WHERE uuid = ?");

            ps1.setString(1, uuid.toString());
            ps2.setString(1, uuid.toString());

            rs1 = ps1.executeQuery();
            rs2 = ps2.executeQuery();
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
                fManager = new ProfileManager(uuid, name, displayName, requestsAllow, msgAllow, groupId, fList);
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

    private ProfileManager createFManager()
    {
        PreparedStatement ps;
        try {
            Connection connection = DBManager.FBG_DATABASE.getDbAccess().getConnection();
            ps = DBManager.FBG_DATABASE.getDbAccess().getConnection().prepareStatement("INSERT INTO "+prefixTables+FriendsBG.getInstance().sqlProfilesManager.getTableName()+" (uuid, name, displayName, requestsAllow, msgAllow, groupId) VALUES (?, ?, ?, ?, ?, ?)");
            ProfileManager fManager = new ProfileManager(uuid, name, displayName, true, true, null, new HashMap<>());
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

    @Deprecated
    private void createFManager(ProfileManager fManager)
    {
        try {
            Connection connection = DBManager.FBG_DATABASE.getDbAccess().getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM "+prefixTables+FriendsBG.getInstance().sqlProfilesManager.getTableName()+" WHERE uuid = ?");
            ResultSet rs = ps.executeQuery();
            if(!rs.next())
            {
                createFManager();
            }else {
                ps = connection.prepareStatement("INSERT INTO "+prefixTables+FriendsBG.getInstance().sqlProfilesManager.getTableName()+" (uuid, name, displayName, requestsAllow, msgAllow, groupId) VALUES (?, ?, ?, ?, ?, ?)");
                ps.setString(1, uuid.toString());
                ps.setString(2, name);
                ps.setString(3, fManager.getDisplayName());
                ps.setInt(4, fManager.requestsAllow()?1:0);
                ps.setInt(5, fManager.msgAllow()?1:0);
                ps.setString(6, fManager.getGroupId().toString());
                ps.executeUpdate();
                ps.close();
                connection.close();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
