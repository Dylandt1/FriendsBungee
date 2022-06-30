package api.files;

import java.io.*;

/**
 * This file is part of FriendsBungee (FriendsBG-Free), a bungeecord friends plugin.
 *
 * Copyright (C) <2022>  <Dylan André>
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

public class Files
{
    public static void save(File file, String entry)
    {
        final FileWriter writer;

        try {
            createFile(file);
            writer = new FileWriter(file);
            writer.write(entry);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("ERROR Files.save"+"\n\n"+"An exception occurred when save file : "+file.getName()+"\n\n"+e.getMessage());
        }
    }

    public static String loadFile(File file)
    {
        if(file.exists())
        {
            try {
                final BufferedReader reader = new BufferedReader(new FileReader(file));
                final StringBuilder str = new StringBuilder();

                String line;

                while((line = reader.readLine()) != null)
                {
                    str.append(line);
                }
                reader.close();
                return str.toString();
            } catch (IOException e)
            {
                throw new RuntimeException("ERROR Files.loadFile"+"\n\n"+"An exception occurred when load file : "+file.getName()+"\n\n"+e.getMessage());
            }
        }
        return "";
    }

    private static void createFile(File file) throws IOException
    {
        if(!file.exists())
        {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }
}
