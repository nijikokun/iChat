package com.nijiko.iChat.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic configuration loader.
 * 
 * @author Nijiko
 */
public abstract class DefaultConfiguration {

    public String format = "[+prefix+group+suffix&f] +name: +message";
    public String censor_char = "*";
    public boolean censor_colored = false;
    public String censor_color = "&f";
    public String string_color = "&f";
    public List<String> censor = new ArrayList<String>();

    public abstract void load();
}
