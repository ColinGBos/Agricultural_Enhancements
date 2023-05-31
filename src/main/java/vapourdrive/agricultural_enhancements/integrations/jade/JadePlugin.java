package vapourdrive.agricultural_enhancements.integrations.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CropBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import vapourdrive.agricultural_enhancements.content.fertilizer.producer.FertilizerProducerBlock;
import vapourdrive.agricultural_enhancements.content.fertilizer.producer.FertilizerProducerTile;
import vapourdrive.agricultural_enhancements.content.harvester.HarvesterBlock;
import vapourdrive.agricultural_enhancements.content.harvester.HarvesterTile;
import vapourdrive.agricultural_enhancements.content.irrigation.IrrigationPipeBlock;
import vapourdrive.agricultural_enhancements.content.manager.CropManagerBlock;
import vapourdrive.agricultural_enhancements.content.manager.CropManagerTile;
import vapourdrive.agricultural_enhancements.content.soil.TilledSoilBlock;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    public static final ResourceLocation SOIL = new ResourceLocation("agriculturalenhancements.soil");
    public static final ResourceLocation CROPS = new ResourceLocation("agriculturalenhancements.crops");
    public static final ResourceLocation IRRIGATION = new ResourceLocation("agriculturalenhancements.irrigation");
    public static final ResourceLocation HARVESTER = new ResourceLocation("agriculturalenhancements.harvester");
    public static final ResourceLocation CROP_MANAGER = new ResourceLocation("agriculturalenhancements.crop_manager");
    public static final ResourceLocation FERTILIZER_PRODUCER = new ResourceLocation("agriculturalenhancements.fertilizer_producer");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(HarvesterContentProvider.INSTANCE, HarvesterTile.class);
        registration.registerBlockDataProvider(CropManagerContentProvider.INSTANCE, CropManagerTile.class);
        registration.registerBlockDataProvider(FertilizerProducerContentProvider.INSTANCE, FertilizerProducerTile.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(SoilContentProvider.INSTANCE, TilledSoilBlock.class);
        registration.registerBlockComponent(CropContentProvider.INSTANCE, CropBlock.class);
        registration.registerBlockComponent(IrrigationContentProvider.INSTANCE, IrrigationPipeBlock.class);
        registration.registerBlockComponent(HarvesterContentProvider.INSTANCE, HarvesterBlock.class);
        registration.registerBlockComponent(CropManagerContentProvider.INSTANCE, CropManagerBlock.class);
        registration.registerBlockComponent(FertilizerProducerContentProvider.INSTANCE, FertilizerProducerBlock.class);
    }

}
