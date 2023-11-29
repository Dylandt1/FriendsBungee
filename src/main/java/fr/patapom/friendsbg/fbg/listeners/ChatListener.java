package fr.patapom.friendsbg.fbg.listeners;

import fr.patapom.friendsbg.fbg.FriendsBG;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener
{
    private final boolean antiSpam = FriendsBG.getInstance().getConfig().getBoolean("msg.antiSpam.use");
    private final int antiSpamLevel = FriendsBG.getInstance().getConfig().getInt("msg.antiSpam.level");
    private final int cool_down = FriendsBG.getInstance().getConfig().getInt("msg.antiSpam.cool_down")*1000;
    private final String antiSpamMsg = FriendsBG.getInstance().getConfig().getString("msg.antiSpam.message").replace("&", "§");

    @EventHandler
    public void onChat(ChatEvent e)
    {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();

        if(antiSpam && antiSpamLevel == 2 || antiSpamLevel == 3)
        {
            if(!FriendsBG.getInstance().cooldown.containsKey(p.getUniqueId()))
            {
                FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
            }else if(System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()) > cool_down)
            {
                FriendsBG.getInstance().cooldown.remove(p.getUniqueId());
                FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
            }
            else {
                e.setCancelled(true);
                sendMessage(p, antiSpamMsg.replace("%cooldown%", String.valueOf((cool_down - (System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()))) / 1000)));
            }
        }
    }

    private void sendMessage(ProxiedPlayer p, String s) {p.sendMessage(new TextComponent(s));}
}
