package vapourdrive.agricultural_enhancements.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;

import static vapourdrive.agricultural_enhancements.AgriculturalEnhancements.seeds;

//@EventBusSubscriber(modid = AgriculturalEnhancements.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModSetup {
    public static void init(FMLCommonSetupEvent event) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof BlockItem blockItem) {
                if (blockItem.getBlock() instanceof CropBlock || blockItem.getBlock() instanceof BushBlock) {
                    seeds.add(item);
                }
            }
        }
        AgriculturalEnhancements.debugLog(seeds.toString());
    }

}
