package joshuaepstein.extratooltips;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import joshuaepstein.extratooltips.config.TooltipConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.List;

@Mod(Main.MOD_ID)
public class Main {
    public static final String MOD_ID = "extra_tooltips";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String VERSION = "p1.0-forge-1.19";

    public Main() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::onCommandRegister);
        init();
    }

    public static void init() {
        ModConfigs.register();
    }


    public static class ModConfigs {
        public static TooltipConfig TOOLTIPS;

        public static void register() {
            TOOLTIPS = (TooltipConfig)(new TooltipConfig()).readConfig();
        }
    }


    @Mod.EventBusSubscriber({Dist.CLIENT})
    public static class TooltipEvent {
        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            Main.ModConfigs.TOOLTIPS.getTooltipString(event.getItemStack().getItem()).ifPresent(str -> {
                List<Component> tooltip = event.getToolTip();
                List<String> added = Lists.reverse(Lists.newArrayList(str.split("\n")));
                if (!added.isEmpty()) {
                    tooltip.add(1, Component.literal(""));
                    for (String newStr : added)
                        tooltip.add(1, (Component.literal(newStr)).withStyle(ChatFormatting.GRAY));
                }
            });
        }
    }

    public void onCommandRegister(RegisterCommandsEvent event) {
        ModCommands.registerCommands(event.getDispatcher(), event.getEnvironment());
    }

    public static class ModCommands {
        public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection env) {
            dispatcher.register( Commands.literal("extra_tooltips").then(Commands.literal("reload").executes(context -> {
                try {
                    Main.ModConfigs.register();
                    (context.getSource()).getPlayerOrException().sendSystemMessage((Component.literal("Reloaded Config")).withStyle(ChatFormatting.GREEN));
                } catch (Exception e) {
                    e.printStackTrace();
                    (context.getSource()).getPlayerOrException().sendSystemMessage((Component.literal("Reloaded Config Failed")).withStyle(ChatFormatting.RED));
                    (context.getSource()).getPlayerOrException().sendSystemMessage((Component.literal("Check the " + ChatFormatting.WHITE + "mods download page (click here)" + ChatFormatting.GOLD +" for a guide on config formatting.")).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/config-tooltips")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open the config formatting guide.")))));
                    throw e;
                }
                return 1;
            })));

//            Version command
            dispatcher.register(Commands.literal("extra_tooltips").then(Commands.literal("version").executes(context -> {
                (context.getSource()).getPlayerOrException().sendSystemMessage((Component.literal(ChatFormatting.WHITE + "You are running " + ChatFormatting.GREEN + "extra_tooltips_" + Main.VERSION)));
                return 1;
            })));
        }
    }
}
