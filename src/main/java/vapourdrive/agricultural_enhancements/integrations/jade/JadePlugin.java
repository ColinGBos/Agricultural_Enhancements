package vapourdrive.agricultural_enhancements.integrations.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CropBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import vapourdrive.agricultural_enhancements.modules.irrigation.IIrrigationBlock;
import vapourdrive.agricultural_enhancements.modules.irrigation.IrrigationPipeBlock;
import vapourdrive.agricultural_enhancements.modules.soil.TilledSoilBlock;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    public static final ResourceLocation FUEL = new ResourceLocation("agriculturalenhancements.fuel");
    public static final ResourceLocation SOIL = new ResourceLocation("agriculturalenhancements.soil");
    public static final ResourceLocation CROPS = new ResourceLocation("agriculturalenhancements.crops");
    public static final ResourceLocation IRRIGATION = new ResourceLocation("agriculturalenhancements.irrigation");

    @Override
    public void register(IWailaCommonRegistration registration) {
        //TODO register data providers
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(SoilContentProvider.INSTANCE, TilledSoilBlock.class);
        registration.registerBlockComponent(CropContentProvider.INSTANCE, CropBlock.class);
        registration.registerBlockComponent(IrrigationContentProvider.INSTANCE, IrrigationPipeBlock.class);
    }

}
