package vapourdrive.agricultural_enhancements.setup;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.modules.harvester.HarvesterScreen;

@Mod.EventBusSubscriber(modid = AgriculturalEnhancements.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void setup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.HARVESTER_CONTAINER.get(), HarvesterScreen::new);
        });
    }

//    @SubscribeEvent
//    public void onTooltipPre(RenderTooltipEvent.GatherComponents event) {
//        Item item = event.getItemStack().getItem();
//        if (item.getRegistryName().getNamespace().equals(FurnaceMk2.MODID)) {
//            event.setMaxWidth(200);
//        }
//    }
}
