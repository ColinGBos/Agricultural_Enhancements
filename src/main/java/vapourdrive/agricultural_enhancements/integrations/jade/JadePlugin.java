package vapourdrive.agricultural_enhancements.integrations.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CropBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import vapourdrive.agricultural_enhancements.content.base.AbstractBaseFuelUserTile;
import vapourdrive.agricultural_enhancements.content.base.AbstractBaseMachineBlock;
import vapourdrive.agricultural_enhancements.content.irrigation.IrrigationPipeBlock;
import vapourdrive.agricultural_enhancements.content.soil.TilledSoilBlock;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    public static final ResourceLocation FUEL = new ResourceLocation("agriculturalenhancements.fuel");
    public static final ResourceLocation SOIL = new ResourceLocation("agriculturalenhancements.soil");
    public static final ResourceLocation CROPS = new ResourceLocation("agriculturalenhancements.crops");
    public static final ResourceLocation IRRIGATION = new ResourceLocation("agriculturalenhancements.irrigation");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(IFuelUserContentProvider.INSTANCE, AbstractBaseFuelUserTile.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(SoilContentProvider.INSTANCE, TilledSoilBlock.class);
        registration.registerBlockComponent(CropContentProvider.INSTANCE, CropBlock.class);
        registration.registerBlockComponent(IrrigationContentProvider.INSTANCE, IrrigationPipeBlock.class);
        registration.registerBlockComponent(IFuelUserContentProvider.INSTANCE, AbstractBaseMachineBlock.class);
    }

}
