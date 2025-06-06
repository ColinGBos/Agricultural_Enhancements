package vapourdrive.agricultural_enhancements.setup;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.content.fertilizer.producer.FertilizerProducerScreen;
import vapourdrive.agricultural_enhancements.content.harvester.HarvesterScreen;
import vapourdrive.agricultural_enhancements.content.irrigation.irrigation_controller.IrrigationControllerScreen;
import vapourdrive.agricultural_enhancements.content.manager.CropManagerScreen;

@EventBusSubscriber(modid = AgriculturalEnhancements.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

//    public static void setup(final FMLClientSetupEvent event) {
//        event.enqueueWork(() -> {
//            ItemBlockRenderTypes.setRenderLayer(Registration.DUSKBLOOM_BLOCK.get(), RenderType.cutout());
//        });
//    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(Registration.HARVESTER_MENU.get(), HarvesterScreen::new);
        event.register(Registration.IRRIGATION_CONTROLLER_MENU.get(), IrrigationControllerScreen::new);
        event.register(Registration.FERTILIZER_PRODUCER_MENU.get(), FertilizerProducerScreen::new);
        event.register(Registration.CROP_MANAGER_MENU.get(), CropManagerScreen::new);
    }
}
