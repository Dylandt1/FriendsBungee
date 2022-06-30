package api.utils;

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

public class PluginUpdateChecker
{
    /**
     * This part is planned for version 1.0 (RELEASE) of the plugin;
     * This should be the beginning of the class allocated to checking the availability of new version on the official
     * repository. You are free to adapt it according to your needs.
     *
     * This class is not functional in the state !!
     */
    private final String versionAvailable;

    public PluginUpdateChecker()
    {
        this.versionAvailable = checkOnRepositoryVersion();
    }

    public boolean isAvailable() {return versionAvailable != null;}
    public String getVersionAvailable() {return versionAvailable;}

    private String checkOnRepositoryVersion()
    {
        return null;
    }
}
