package fr.patapom.friendsbg.fbg.listeners;

import fr.patapom.friendsbg.fbg.FriendsBG;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class ChatListener implements Listener
{
    private final boolean antiSpam = FriendsBG.getInstance().getConfig().getBoolean("msg.antiSpam.use");
    private final int antiSpamLevel = FriendsBG.getInstance().getConfig().getInt("msg.antiSpam.level");
    private final int cool_down = FriendsBG.getInstance().getConfig().getInt("msg.antiSpam.cool_down")*1000;
    private final String antiSpamMsg = FriendsBG.getInstance().getMsgConfig().getString("msg.antiSpam.message").replace("&", "§");
    private final List<String> cmdAliases = FriendsBG.getInstance().cmdAliases;

    @EventHandler
    public void onChat(ChatEvent e)
    {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        String msg = e.getMessage();

        if(antiSpam)
        {
            if(!e.isCommand())
            {
                if(antiSpamLevel == 2 || antiSpamLevel == 4 || antiSpamLevel == 6 || antiSpamLevel == 7)
                {
                    if(!FriendsBG.getInstance().cooldown.containsKey(p.getUniqueId()))
                    {
                        FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                    }else if(System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()) > cool_down)
                    {
                        FriendsBG.getInstance().cooldown.remove(p.getUniqueId());
                        FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                    }else {
                        e.setCancelled(true);
                        sendMessage(p, antiSpamMsg.replace("%cooldown%", String.valueOf((cool_down - (System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()))) / 1000)));
                    }
                }
            }else {

                List<String> alias = new ArrayList<>();

                alias.add("message");
                alias.add("resend");
                alias.addAll(FriendsBG.getInstance().getMsgConfig().getStringList("msg.cmdAlias.send"));
                alias.addAll(FriendsBG.getInstance().getMsgConfig().getStringList("msg.cmdAlias.resend"));

                if(antiSpamLevel == 1 || antiSpamLevel == 4)
                {
                    for(String s : alias){
                        if(msg.startsWith(s))
                        {
                            if(!FriendsBG.getInstance().cooldown.containsKey(p.getUniqueId()))
                            {
                                FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                            }else if(System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()) > cool_down)
                            {
                                FriendsBG.getInstance().cooldown.remove(p.getUniqueId());
                                FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                            }else {
                                e.setCancelled(true);
                                sendMessage(p, antiSpamMsg.replace("%cooldown%", String.valueOf((cool_down - (System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()))) / 1000)));
                            }
                        }
                    }
                }else if(antiSpamLevel == 3)
                {
                    for(String s : cmdAliases)
                    {
                        if(msg.startsWith(s)) {return;}
                    }

                    for(String s : alias)
                    {
                        if(msg.startsWith(s)) {return;}
                    }

                    if(!FriendsBG.getInstance().cooldown.containsKey(p.getUniqueId()))
                    {
                        FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                    }else if(System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()) > cool_down)
                    {
                        FriendsBG.getInstance().cooldown.remove(p.getUniqueId());
                        FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                    }else {
                        e.setCancelled(true);
                        sendMessage(p, antiSpamMsg.replace("%cooldown%", String.valueOf((cool_down - (System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()))) / 1000)));
                    }
                }else if (antiSpamLevel == 5)
                {
                    for(String s : cmdAliases)
                    {
                        if(msg.startsWith(s)) {return;}
                    }

                    if(!FriendsBG.getInstance().cooldown.containsKey(p.getUniqueId()))
                    {
                        FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                    }else if(System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()) > cool_down)
                    {
                        FriendsBG.getInstance().cooldown.remove(p.getUniqueId());
                        FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                    }else {
                        e.setCancelled(true);
                        sendMessage(p, antiSpamMsg.replace("%cooldown%", String.valueOf((cool_down - (System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()))) / 1000)));
                    }
                }else if (antiSpamLevel == 6)
                {
                    for(String s : cmdAliases)
                    {
                        if(msg.startsWith(s)) {return;}
                    }

                    for(String s : alias)
                    {
                        if(msg.startsWith(s)) {return;}
                    }

                    if(!FriendsBG.getInstance().cooldown.containsKey(p.getUniqueId()))
                    {
                        FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                    }else if(System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()) > cool_down)
                    {
                        FriendsBG.getInstance().cooldown.remove(p.getUniqueId());
                        FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                    }else {
                        e.setCancelled(true);
                        sendMessage(p, antiSpamMsg.replace("%cooldown%", String.valueOf((cool_down - (System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()))) / 1000)));
                    }
                }else if (antiSpamLevel == 7)
                {
                    for(String s : cmdAliases)
                    {
                        if(msg.startsWith(s)) {return;}
                    }

                    if(!FriendsBG.getInstance().cooldown.containsKey(p.getUniqueId()))
                    {
                        FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                    }else if(System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()) > cool_down)
                    {
                        FriendsBG.getInstance().cooldown.remove(p.getUniqueId());
                        FriendsBG.getInstance().cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                    }else {
                        e.setCancelled(true);
                        sendMessage(p, antiSpamMsg.replace("%cooldown%", String.valueOf((cool_down - (System.currentTimeMillis() - FriendsBG.getInstance().cooldown.get(p.getUniqueId()))) / 1000)));
                    }
                }
            }
        }
    }

    private void sendMessage(ProxiedPlayer p, String s) {p.sendMessage(new TextComponent(s));}
}
