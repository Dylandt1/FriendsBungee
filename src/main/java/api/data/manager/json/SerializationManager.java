package api.data.manager.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

public class SerializationManager
{
    private final Gson gson;

    public SerializationManager()
    {
        this.gson = initGson();
    }

    public String serialize(Object obj)
    {
        return this.gson.toJson(obj);
    }

    public Object deserialize(String json, Class<?> c)
    {
        return this.gson.fromJson(json, c);
    }

    private Gson initGson()
    {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
    }
}
