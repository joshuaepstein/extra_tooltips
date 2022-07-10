package joshuaepstein.extra_tooltips.config;

import joshuaepstein.extra_tooltips.Main;

public class ModConfigs {
    public static TooltipsConfig TOOLTIPS;

    public ModConfigs() {
    }

    public static void register() {
        TOOLTIPS = (TooltipsConfig)(new TooltipsConfig()).readConfig();
        Main.LOGGER.info("Configs registered and loading.");
    }
}
