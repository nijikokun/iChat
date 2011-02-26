package com.nijikokun.bukkit.iChat;

import com.nijiko.Misc;
import com.nijiko.Messaging;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Listener.java
 * <br /><br />
 * Listens for calls from hMod, and reacts accordingly.
 * 
 * @author Nijikokun <nijikokun@gmail.com>
 */
public class Listener extends PlayerListener {

    private static final Logger log = Logger.getLogger("Minecraft");
    /**
     * Miscellaneous object for various functions that don't belong anywhere else
     */
    public Misc Misc = new Misc();
    public static iChat plugin;

    public Listener(iChat instance) {
        plugin = instance;
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        final Player player = event.getPlayer();
        String message = event.getMessage();
        final String end = message.substring(message.length()-2, message.length());

        if(end.startsWith("&")) {
            message = message.substring(0, message.length()-2);
        }

        if(message == null ? "" == null : message.equals("")) {
            return;
        }

        if (plugin.show) {
            String format = "";
            String custom = "";
            String group = plugin.Permissions.Security.getGroup(player.getName());
            String prefix = plugin.Permissions.Security.getGroupPrefix(group);
            String suffix = plugin.Permissions.Security.getGroupSuffix(group);
            String userPrefix = plugin.Permissions.Security.getUserPermissionString(player.getName(), "prefix");
            String userSuffix = plugin.Permissions.Security.getUserPermissionString(player.getName(), "suffix");

            group = (group == null) ? "" : group;
            prefix = (prefix == null) ? "" : prefix;
            suffix = (suffix == null) ? "" : suffix;

            if (userPrefix != null) {
                if (!userPrefix.isEmpty()) {
                    prefix = userPrefix;
                }
            }

            if (userSuffix != null) {
                if (!userSuffix.isEmpty()) {
                    suffix = userSuffix;
                }
            }

            // Customization
            custom = (plugin.config.format == null) ? "[+prefix+group+suffix&f] +name: +message" : plugin.config.format;
            format = Messaging.argument(custom,
                    new String[]{"+group,+g", "+name,+n", "+suffix,+s", "+prefix,+p", "+message,+m", "+healthbar,+hb"},
                    new String[]{group, "%1$s", suffix, prefix, plugin.censored(message), plugin.healthBar(player, true)});

            event.setFormat(Messaging.parse(format));
        }
    }
}
