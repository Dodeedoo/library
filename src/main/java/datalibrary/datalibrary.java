package datalibrary;

import org.bukkit.plugin.java.JavaPlugin;

public class datalibrary {
    private static JavaPlugin plugin;

    // For other classes in our library
    public static JavaPlugin getPlugin() {
        return plugin;
    }

    // This method must not be used any where in the library!
    public static void setPlugin(JavaPlugin plugin) {
        datalibrary.plugin = plugin;
    }



}
