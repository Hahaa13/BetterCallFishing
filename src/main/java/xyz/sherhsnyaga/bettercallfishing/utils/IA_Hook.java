package xyz.sherhsnyaga.bettercallfishing.utils;

import org.bukkit.Bukkit;

public class IA_Hook {
    public static boolean isEnable(){
        return Bukkit.getPluginManager().getPlugin("ItemsAdder") != null;
    }
}
