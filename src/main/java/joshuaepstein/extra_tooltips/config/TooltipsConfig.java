package joshuaepstein.extra_tooltips.config;

import com.google.gson.annotations.Expose;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TooltipsConfig extends Config {
    @Expose
    private final List<TooltipEntry> tooltips = new ArrayList();

    public TooltipsConfig() {
    }

    public Optional<String> getTooltipString(Item item) {
        String itemRegistryName = Registry.ITEM.getKey(item).get().getValue().toString();
        return this.tooltips.stream().filter((entry) -> entry.item.equals(itemRegistryName)).map(TooltipEntry::getValue).findFirst();
    }

    public String getName() {
        return "tooltip";
    }

    protected void reset() {
        this.tooltips.clear();
        this.tooltips.add(new TooltipEntry(Registry.ITEM.getKey(Items.STONE).get().getValue().toString(), "Stone... It is a block.\nThe most common block.\nOr maybe dirt is...\nI dunno..."));
    }

    public static class TooltipEntry {
        @Expose
        private String item;
        @Expose
        private String value;

        public TooltipEntry(String item, String value) {
            this.item = item;
            this.value = value;
        }

        public String getItem() {
            return this.item;
        }

        public String getValue() {
            return this.value;
        }
    }
}
