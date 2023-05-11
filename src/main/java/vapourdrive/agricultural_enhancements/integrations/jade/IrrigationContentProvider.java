package vapourdrive.agricultural_enhancements.integrations.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import vapourdrive.agricultural_enhancements.modules.irrigation.IrrigationPipeBlock;
import vapourdrive.agricultural_enhancements.modules.soil.TilledSoilBlock;

public enum IrrigationContentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
        boolean irrigation = blockAccessor.getBlockState().getValue(IrrigationPipeBlock.IRRIGATION)>0;
        tooltip.add(Component.translatable("agriculturalenhancements.jade.irrigation", irrigation));
    }

    @Override
    public ResourceLocation getUid() {
        return JadePlugin.IRRIGATION;
    }
}
