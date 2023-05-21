package vapourdrive.agricultural_enhancements.setup;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.content.fertilizer.producer.FertilizerProducerScreen;
import vapourdrive.agricultural_enhancements.content.harvester.HarvesterScreen;
import vapourdrive.agricultural_enhancements.content.irrigation.irrigation_controller.IrrigationControllerScreen;
import vapourdrive.agricultural_enhancements.content.manager.CropManagerScreen;

@Mod.EventBusSubscriber(modid = AgriculturalEnhancements.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void setup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.HARVESTER_CONTAINER.get(), HarvesterScreen::new);
            MenuScreens.register(Registration.IRRIGATION_CONTROLLER_CONTAINER.get(), IrrigationControllerScreen::new);
            MenuScreens.register(Registration.FERTILIZER_PRODUCER_CONTAINER.get(), FertilizerProducerScreen::new);
            MenuScreens.register(Registration.CROP_MANAGER_CONTAINER.get(), CropManagerScreen::new);
        });
    }
}
