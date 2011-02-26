package com.nijikokun.bukkit.iChat;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijiko.Misc;
import com.nijiko.Messaging;
import com.nijiko.iChat.configuration.ConfigurationHandler;
import com.nijiko.iChat.configuration.DefaultConfiguration;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

/*
 * SignShops
 * Copyright (C) 2011  Nijikokun <nijikokun@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class iChat extends JavaPlugin {
    /*
     * Loggery Foggery
     */

    public static final Logger log = Logger.getLogger("Minecraft");

    /*
     * Central Data pertaining directly to the plugin name & versioning.
     */
    public static String name = "iChat";
    public static String codename = "Maria Holic";
    public static String version = "1.5";
    
    /**
     * Listener for the plugin system.
     */
    public Listener l = new Listener(this);

    /**
     * Controller for permissions and security.
     */
    public static Permissions Permissions;

    /**
     * Miscellaneous object for various functions that don't belong anywhere else
     */
    public Misc Misc = new Misc();

    /**
     * Configuration
     */
    public DefaultConfiguration config;

    /*
     * Variables
     */
    public static boolean show = true;

    public void onDisable() {
        log.info(Messaging.bracketize(name) + " version " + Messaging.bracketize(version) + " (" + codename + ") disabled");
    }

    public void onEnable() {
        // Create files.
        getDataFolder().mkdirs();
        Misc.touch(getDataFolder(), "config.yml");

        // Initilize configuration.
        this.config = new ConfigurationHandler(getConfiguration());

        // Load Configuration File
        getConfiguration().load();

        // Load Configuration Settings
        this.config.load();

        // Register
        registerEvents();
    }

    public void Disable() {
        this.show = false;
    }

    public String formatMessage(Player player, String message, boolean formatted) {
        String format = "";
        String custom = "";
        String group = Permissions.Security.getGroup(player.getName());
        String prefix = Permissions.Security.getGroupPrefix(group);
        String suffix = Permissions.Security.getGroupSuffix(group);
        String userPrefix = Permissions.Security.getPermissionString(player.getName(), "prefix");
        String userSuffix = Permissions.Security.getPermissionString(player.getName(), "suffix");

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
        custom = (this.config.format == null) ? "[+prefix+group+suffix&f] +name: +message" : this.config.format;
        format = Messaging.argument(custom,
                new String[]{"+group,+g", "+name,+n", "+suffix,+s", "+prefix,+p", "+message,+m"},
                new String[]{group, (formatted) ? "%1$s" : player.getDisplayName(), suffix, prefix, (formatted) ? "%2$s" : censored(message)});

        return Messaging.parse(format);
    }
    
    private String star(String word) {
        String str = "";

        for (int i = 0; i < word.length(); i++) {
            str = str + this.config.censor_char;
        }

        return str;
    }

    public String censored(String input) {
        String output = "";
        String[] sifened = input.split(" ");
        int i = 0;

        if(this.config.censor != null) {
            if(this.config.censor.size() > 0 && sifened.length > 0) {
                for (String item : sifened) {
                    for (String word : this.config.censor) {
                        if (item.equalsIgnoreCase(word)) {
                            item = star(item);

                            if(i != (sifened.length-1) && this.config.censor_colored) {
                                item = this.config.censor_color + item + this.config.string_color;
                            }
                        }
                    }

                    output = ((i != 0) ? output + " " + item : item);

                    i++;
                }
            } else {
                return input;
            }
        } else {
            return input;
        }

        if(output.equals("") || output.isEmpty()) {
            return input;
        }

        return output;
    }

    public String healthBar(Player player, boolean bracketized) {
        String hb_color = "&2";
        int health = player.getHealth();

        int length = 10;
        int bars = Math.round(health / 2);
        int remainder = length - bars;

        if (bars >= 7) {
            hb_color = "&2";
        } else if (bars < 7 && bars >= 3) {
            hb_color = "&e";
        } else if (bars < 3) {
            hb_color = "&4";
        }

        if(bracketized) {
            return "&f[" + hb_color + Misc.repeat('|', bars) + "&8" + Misc.repeat('|', remainder) + "&f]";
        } else {
            return hb_color + Misc.repeat('|', bars) + "&8" + Misc.repeat('|', remainder);
        }
    }

    private Listeners Listeners = new Listeners();

    private class Listeners extends ServerListener {

        public Listeners() {
        }

        @Override
        public void onPluginEnabled(PluginEvent event) {
            if(event.getPlugin().getDescription().getName().equals("Permissions")) {
                iChat.Permissions = (Permissions)event.getPlugin();
                log.info(Messaging.bracketize(name) + " Attached plugin to Permissions. Enjoy~");
            }
        }
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, Listeners, Priority.Monitor, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, l, Priority.Normal, this);
    }
}
