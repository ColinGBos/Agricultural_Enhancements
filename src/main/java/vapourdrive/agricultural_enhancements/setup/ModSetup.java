package vapourdrive.agricultural_enhancements.setup;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;

import static vapourdrive.agricultural_enhancements.AgriculturalEnhancements.seeds;

@Mod.EventBusSubscriber(modid = AgriculturalEnhancements.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup {

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab("agriculturalenhancements") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Registration.HARVESTER_BLOCK.get());
        }
    };

    public static void init(final FMLCommonSetupEvent event) {
        for (Item item : ForgeRegistries.ITEMS) {
            if (item instanceof BlockItem blockItem) {
                if (blockItem.getBlock() instanceof CropBlock || blockItem.getBlock() instanceof BushBlock) {
                    seeds.add(item);
                }
            }
        }
        AgriculturalEnhancements.debugLog(seeds.toString());
    }

}
