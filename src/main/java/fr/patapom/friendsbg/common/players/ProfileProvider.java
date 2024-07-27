package fr.patapom.friendsbg.common.players;

import fr.patapom.friendsbg.fbg.FriendsBG;
import fr.patapom.friendsbg.fbg.data.manager.DBManager;
import fr.patapom.friendsbg.fbg.data.manager.RedisManager;
import fr.tmmods.tmapi.data.manager.Files;
import fr.tmmods.tmapi.data.manager.Json.SerializationManager;
import fr.tmmods.tmapi.data.manager.redis.RedisAccess;
import fr.tmmods.tmapi.exceptions.ManagerNotFoundException;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.io.File;
import java.sql.*;
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

    private final UUID uuid;
    private final String name;
    private final String displayName;

    private final boolean redisEnable = FriendsBG.getInstance().redisEnable;
    private final boolean sqlEnable = FriendsBG.getInstance().sqlEnable;
    private RedisAccess redisAccess;
    private String REDIS_KEY = "fManager:";

    private final String prefixTables;
    private final String profilesTable;
    private final String friendsTable;

    /**
     * FriendsProvider initiator
     */
    public ProfileProvider(ProxiedPlayer player)
    {
        // Init all variables
        this.plugin = FriendsBG.getInstance();
        this.saveDirectory = new File(plugin.getDataFolder(), "/managers/fManagers/");
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.displayName = player.getDisplayName();
        if(redisEnable)
        {
            this.redisAccess = RedisManager.FBG_REDIS.getRedisAccess();
            this.REDIS_KEY = REDIS_KEY+uuid.toString();
        }
        this.prefixTables = FriendsBG.getInstance().prefixTables;
        this.profilesTable = FriendsBG.getInstance().profilesTable;
        this.friendsTable = FriendsBG.getInstance().friendsTable;
    }

    public ProfileProvider(UUID playerUUID)
    {
        // Init all variables
        this.plugin = FriendsBG.getInstance();
        this.saveDirectory = new File(plugin.getDataFolder(), "/managers/fManagers/");
        this.uuid = playerUUID;
        this.name = null;
        this.displayName = null;
        if(redisEnable)
        {
            this.redisAccess = RedisManager.FBG_REDIS.getRedisAccess();
            this.REDIS_KEY = REDIS_KEY+uuid.toString();
        }
        this.prefixTables = FriendsBG.getInstance().prefixTables;
        this.profilesTable = FriendsBG.getInstance().profilesTable;
        this.friendsTable = FriendsBG.getInstance().friendsTable;
    }

    /**
     * Function to get FriendsManager
     */
    public ProfileManager getPManager() throws ManagerNotFoundException
    {
        // Init null FriendsManager variable
        ProfileManager profile;
        // Get file and get SerializationManager instance for JSon files
        final File file = new File(saveDirectory, uuid.toString()+".json");
        final SerializationManager serManager = plugin.getSerializationManager();

        if(redisEnable && sqlEnable)
        {
            if(file.exists())
            {
                final String json = Files.loadFile(file);
                profile = (ProfileManager) serManager.deserialize(json, ProfileManager.class);
                setPManagerOnRedis(profile);
                file.delete();
            }else {
                // Get FriendsManager on Redis or Mysql server
                profile = getPManagerOnRedis();
                if(profile == null)
                {
                    profile = getPManagerOnMySQL();
                    setPManagerOnRedis(profile);
                }
            }
        }else if(sqlEnable)
        {
            if(file.exists())
            {
                final String json = Files.loadFile(file);
                profile = (ProfileManager) serManager.deserialize(json, ProfileManager.class);
                FriendsBG.getInstance().profiles.put(uuid, profile);
                file.delete();
            }else {
                if(FriendsBG.getInstance().profiles.containsKey(uuid))
                {
                    // Get FriendsManager on plugin cache
                    profile = FriendsBG.getInstance().profiles.get(uuid);
                }else {
                    // Get FriendsManager on Mysql server/media/dylan/Dylan_1To/Dev/Java/Minecraft/Plugins/TM-Hub
                    profile = getPManagerOnMySQL();
                    FriendsBG.getInstance().profiles.put(uuid, profile);
                }

                if(profile == null)
                {
                    profile = createPManager();
                    FriendsBG.getInstance().profiles.put(uuid, profile);
                }
            }
        }else if(redisEnable)
        {
            // Get FriendsManager on Redis server
            profile = getPManagerOnRedis();

            if(profile == null)
            {
                // Get FriendsManager on Json files or create new profile by default values
                if(file.exists())
                {
                    final String json = Files.loadFile(file);
                    profile = (ProfileManager) serManager.deserialize(json, ProfileManager.class);
                    FriendsBG.getInstance().profiles.put(uuid, profile);
                }else {
                    profile = new ProfileManager(uuid, name, displayName, true, true, true, true, null, null, null, new HashMap<>(), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
                    final String json = serManager.serialize(profile);
                    Files.save(file, json);
                }
                setPManagerOnRedis(profile);
            }
        }else
        {
            if(FriendsBG.getInstance().profiles.containsKey(uuid))
            {
                // Get FriendsManager on plugin cache
                profile = FriendsBG.getInstance().profiles.get(uuid);
            }else {
                // Get FriendsManager on Json files or create new profile by default values
                if(file.exists())
                {
                    final String json = Files.loadFile(file);
                    profile = (ProfileManager) serManager.deserialize(json, ProfileManager.class);
                    FriendsBG.getInstance().profiles.put(uuid, profile);
                }else {
                    profile = new ProfileManager(uuid, name, displayName, true, true, true, true, null, null, null, new HashMap<>(), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
                    final String json = serManager.serialize(profile);
                    Files.save(file, json);
                }
            }
        }
        return profile;
    }

    /**
     * Function to save FriendsManager on Redis server or Json file
     */
    public void save(ProfileManager profile)
    {
        if(redisEnable)
        {
            setPManagerOnRedis(profile);
        }else {
            if(FriendsBG.getInstance().profiles.containsKey(uuid))
            {
                FriendsBG.getInstance().profiles.remove(uuid);
                FriendsBG.getInstance().profiles.put(uuid, profile);
            }else {
                FriendsBG.getInstance().profiles.put(uuid, profile);
            }
        }
    }

    public void saveOnJson(ProfileManager profile)
    {
        // Save FriendsManager on Json file
        final File file = new File(saveDirectory, uuid.toString()+".json");
        final SerializationManager serManager = plugin.getSerializationManager();
        final String json = serManager.serialize(profile);
        Files.save(file, json);
    }

    /**
     * Functions to save FriendsManager on Mysql server
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
            ProfileManager profile = getPManager();
            // Init DB connection
            Connection connection = DBManager.FBG_DATABASE.getDbAccess().getConnection();

            // Verify if profile exist in db
            PreparedStatement pst = connection.prepareStatement("SELECT * FROM "+prefixTables+profilesTable+" WHERE uuid = ?");
            pst.setString(1, uuid.toString());
            ResultSet rst = pst.executeQuery();
            if(!rst.next()) {insertPManager(profile);return;}

            // Init PreparedStatement n°1 and set values
            ps1 = connection.prepareStatement("UPDATE " + prefixTables+profilesTable + " SET name = ?, displayName = ?, fAllow = ?, msgAllow = ?, gpAllow = ?, teamsAllow = ?, teamId = ?, groupId = ?, rankInTeam = ?, lastJoin = ? WHERE uuid = ?");
            ps1.setString(1, name);
            ps1.setString(2, displayName);
            ps1.setInt(3, profile.fAllow()?1:0);
            ps1.setInt(4, profile.msgAllow()?1:0);
            ps1.setInt(5, profile.gpAllow()?1:0);
            ps1.setInt(6, profile.teamsAllow()?1:0);

            // teamId
            if(profile.getTeamId() == null)
            {
                ps1.setString(7, null);
            }else {
                ps1.setString(7, profile.getTeamId().toString());
            }
            // groupId
            if(profile.getGroupId() == null)
            {
                ps1.setString(8, null);
            }else {
                ps1.setString(8, profile.getTeamId().toString());
            }

            ps1.setString(9, profile.getRankInTeam());
            ps1.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
            ps1.setString(11, uuid.toString());
            // Execute PreparedStatement n°1 and close
            ps1.executeUpdate();
            ps1.close();

            // Init PreparedStatement n°2 and set values
            ps2 = connection.prepareStatement("SELECT * FROM "+prefixTables+friendsTable+" WHERE uuid = ?");
            ps2.setString(1, uuid.toString());
            // Execute PreparedStatement and init ResultSet
            rs2 = ps2.executeQuery();

            List<UUID> fUUIDList = new ArrayList<>();

            // Get friend list on db
            while(rs2.next())
            {
                if(!profile.getFriendsMap().containsValue(UUID.fromString(rs2.getString("friendUUID"))))
                {
                    PreparedStatement ps = connection.prepareStatement("DELETE FROM "+prefixTables+friendsTable+" WHERE uuid = ? and friendUUID = ?");
                    ps.setString(1, uuid.toString());
                    ps.setString(2, rs2.getString("friendUUID"));
                    ps.executeUpdate();
                    ps.close();
                }else {
                    fUUIDList.add(UUID.fromString(rs2.getString("friendUUID")));
                }
            }

            // Add/Update friends in db
            for(String fName : profile.getFriendsMap().keySet())
            {
                // Add new friends in db
                if(!fUUIDList.contains(profile.getFriendsMap().get(fName)))
                {
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO "+prefixTables+friendsTable+" (uuid, friendUUID, friendName) VALUES (?, ?, ?)");
                    ps.setString(1, uuid.toString());
                    ps.setString(2, profile.getFriendsMap().get(fName).toString());
                    ps.setString(3, fName);
                    ps.executeUpdate();
                    ps.close();
                }
            }

            ps2.close();
            connection.close();
        }catch (SQLException | ManagerNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Privates methods :
     */

    private void setPManagerOnRedis(ProfileManager profile)
    {
        final RedissonClient redisCli = redisAccess.getRedisCli();
        final RBucket<ProfileManager> fBucket = redisCli.getBucket(REDIS_KEY);
        fBucket.set(profile);
    }

    private ProfileManager getPManagerOnRedis()
    {
        final RedissonClient redisCli = redisAccess.getRedisCli();
        final RBucket<ProfileManager> fBucket = redisCli.getBucket(REDIS_KEY);
        return fBucket.get();
    }

    private ProfileManager getPManagerOnMySQL()
    {
        PreparedStatement ps1;
        PreparedStatement ps2;
        ResultSet rs1;
        ResultSet rs2;
        ProfileManager profile = null;
        try {
            Connection connection = DBManager.FBG_DATABASE.getDbAccess().getConnection();
            ps1 = connection.prepareStatement("SELECT * FROM "+prefixTables+profilesTable+" WHERE uuid = ?");
            ps1.setString(1, uuid.toString());
            rs1 = ps1.executeQuery();

            if(rs1.next())
            {
                UUID teamId = null;
                UUID groupId = null;
                Map<String, UUID> fList = new HashMap<>();

                ps2 = connection.prepareStatement("SELECT * FROM "+prefixTables+friendsTable+" WHERE uuid = ?");
                ps2.setString(1, uuid.toString());
                rs2 = ps2.executeQuery();

                while (rs2.next())
                {
                    fList.put(rs2.getString("friendName"), UUID.fromString(rs2.getString("friendUUID")));
                }

                String displayName = rs1.getString("displayName");
                boolean fAllow = rs1.getInt("fAllow") == 1;
                boolean msgAllow = rs1.getInt("msgAllow") == 1;
                boolean gpAllow = rs1.getInt("gpAllow") == 1;
                boolean teamsAllow = rs1.getInt("teamsAllow") == 1;
                String rankInTeam = rs1.getString("rankInTeam");

                if(rs1.getString("teamId") != null)
                {
                    teamId = UUID.fromString(rs1.getString("teamId"));
                }

                if(rs1.getString("groupId") != null)
                {
                    groupId = UUID.fromString(rs1.getString("groupId"));
                }

                Timestamp lastJoin = rs1.getTimestamp("lastJoin");
                Timestamp firstJoin = rs1.getTimestamp("firstJoin");
                profile = new ProfileManager(uuid, name, displayName, fAllow, msgAllow, gpAllow, teamsAllow, rankInTeam, teamId, groupId, fList, lastJoin, firstJoin);
            }else {
                profile = createPManager();
            }
            ps1.close();
            connection.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return profile;
    }

    private ProfileManager createPManager()
    {
        PreparedStatement ps;
        try {
            Connection connection = DBManager.FBG_DATABASE.getDbAccess().getConnection();
            ps = DBManager.FBG_DATABASE.getDbAccess().getConnection().prepareStatement("INSERT INTO "+prefixTables+profilesTable+" (uuid, name, displayName, fAllow, msgAllow, gpAllow, teamsAllow, teamId, groupId, rankInTeam, lastJoin, firstJoin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ProfileManager profile = new ProfileManager(uuid, name, displayName, true, true, true, true, null, null, null, new HashMap<>(), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.setString(3, displayName);
            ps.setInt(4, 1);
            ps.setInt(5, 1);
            ps.setInt(6, 1);
            ps.setInt(7, 1);
            ps.setString(8, null);
            ps.setString(9, null);
            ps.setString(10, null);
            ps.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(12, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
            ps.close();
            connection.close();
            return profile;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void insertPManager(ProfileManager profile)
    {
        PreparedStatement ps;
        try {
            Connection connection = DBManager.FBG_DATABASE.getDbAccess().getConnection();
            ps = DBManager.FBG_DATABASE.getDbAccess().getConnection().prepareStatement("INSERT INTO "+prefixTables+profilesTable+" (uuid, name, displayName, fAllow, msgAllow, gpAllow, teamsAllow, teamId, groupId, rankInTeam, lastJoin, firstJoin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.setString(3, profile.getDisplayName());
            ps.setInt(4, profile.fAllow()?0:1);
            ps.setInt(5, profile.msgAllow()?0:1);
            ps.setInt(6, profile.gpAllow()?0:1);
            ps.setInt(7, profile.teamsAllow()?0:1);

            // teamId
            if(profile.getTeamId() == null)
            {
                ps.setString(8, null);
            }else {
                ps.setString(8, profile.getTeamId().toString());
            }
            // groupId
            if(profile.getGroupId() == null)
            {
                ps.setString(9, null);
            }else {
                ps.setString(9, profile.getTeamId().toString());
            }

            ps.setString(10, profile.getRankInTeam());
            ps.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(12, profile.getFirstJoin());
            ps.executeUpdate();
            ps.close();
            connection.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
