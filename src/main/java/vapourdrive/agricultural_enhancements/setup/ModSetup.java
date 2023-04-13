package vapourdrive.agricultural_enhancements.setup;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.commands.ModCommands;

@Mod.EventBusSubscriber(modid = AgriculturalEnhancements.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup {
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab("agriculturalenhancements") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Registration.HARVESTER_BLOCK.get());
        }
    };

    public static void init(final FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public static void serverLoad(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }
}
