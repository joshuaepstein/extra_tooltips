package joshuaepstein.extra_tooltips;

import com.google.common.collect.Lists;
import joshuaepstein.extra_tooltips.config.ModConfigs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class Main implements ModInitializer {

    public static final String MOD_ID = "extra_tooltips";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModConfigs.register();
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("extra_tooltips").then(CommandManager.literal("reload").executes(context -> {
                try {
                    ModConfigs.register();
                    (context.getSource()).getPlayerOrThrow().sendMessage(Text.of("Reloaded Config"), true);
                    return 1;
                } catch (Exception var2) {
                    var2.printStackTrace();
                    (context.getSource()).getPlayerOrThrow().sendMessage(Text.of("Reloading Config Failed"), true);
                    (context.getSource()).getPlayerOrThrow().sendMessage(Text.literal("Check the formatting guide on the mod download page (click here)!").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click here to view mod download page!"))).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/config-tooltips"))));
                    throw var2;
                }
            })));

            dispatcher.register(CommandManager.literal("extra_tooltips").then(CommandManager.literal("version").executes(context -> {
                (context.getSource()).getPlayerOrThrow().sendMessage(Text.literal("You are running " + Formatting.GREEN + "extra_tooltips-fabric-1.19"));
                return 1;
            })));
        }));
        ItemTooltipCallback.EVENT.register((stack, context, list) -> {
            if(!stack.isEmpty()){
                Item item = stack.getItem();
                ModConfigs.TOOLTIPS.getTooltipString(item).ifPresent((str) -> {
                    List<String> added = Lists.reverse(Lists.newArrayList(str.split("\n")));
                    for(String s : added){
                        list.add(Text.of(s));
                    }
                });
            }
        });
    }
}
