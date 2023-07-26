package fr.patapom.friendsbg.fbg.config;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

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

public class ConfigsManager
{
    @Deprecated
    public static Configuration loadConfig(String fileName, Plugin plugin) {return createFileConfiguration(fileName, plugin);}

    private static Configuration createFileConfiguration(String fileName, Plugin plugin)
    {
        if(!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();

        File file = new File(plugin.getDataFolder(), fileName+".yml");

        if(!file.exists())
        {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
                InputStream is = plugin.getResourceAsStream(fileName+".yml");
                OutputStream os = Files.newOutputStream(file.toPath());
                ByteStreams.copy(is, os);
            } catch (IOException e) {
                throw new RuntimeException("§cERROR §bCfgM.ctFile1"+"\n\n"+"§fAn exception occurred when create file : §b"+fileName+"\n\n§6"+e.getMessage());
            }
        }
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), fileName+".yml"));
        } catch (IOException e) {
            throw new RuntimeException("§cERROR §bCfgM.ctFile2"+"\n\n"+"§fAn exception occurred when create file : §b"+fileName+"\n\n§6"+e.getMessage());
        }
    }

    public static Configuration getConfig(String fileName, Plugin plugin)
    {
        return createFileConfiguration(fileName, plugin);
    }

    public static void saveConfig(Configuration config, String fileName, Plugin plugin)
    {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(plugin.getDataFolder(), fileName+".yml"));
        } catch (IOException e) {
            throw new RuntimeException("§cERROR §bCfgM.save"+"\n\n"+"§fAn exception occurred when save file : §b"+fileName+"\n\n§6"+e.getMessage());
        }
    }
}
