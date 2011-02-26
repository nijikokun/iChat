package com.nijiko.iChat.configuration;

import org.bukkit.util.config.Configuration;

/**
 * Handles default configuration and loads data.
 * 
 * @author Nijiko
 */
public class ConfigurationHandler extends DefaultConfiguration {

    private Configuration config;

    public ConfigurationHandler(Configuration config) {
        this.config = config;
    }

    public void load() {
        this.format = this.config.getString("message-format", this.format);
        this.censor = this.config.getStringList("censor-list", null);
        this.censor_char = this.config.getString("censor-char", "*");
        this.censor_colored = this.config.getBoolean("censor-colored", false);
        this.censor_color = this.config.getString("censor-color", "&f");
        this.string_color = this.config.getString("censor-string-color", "&f");
    }
}
