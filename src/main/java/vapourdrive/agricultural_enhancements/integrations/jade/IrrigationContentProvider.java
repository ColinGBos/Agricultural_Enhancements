package vapourdrive.agricultural_enhancements.integrations.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import vapourdrive.agricultural_enhancements.content.irrigation.IrrigationPipeBlock;

public enum IrrigationContentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
        int irrigation = blockAccessor.getBlockState().getValue(IrrigationPipeBlock.IRRIGATION);
        tooltip.add(Component.translatable("agriculturalenhancements.irrigation", irrigation));
    }

    @Override
    public ResourceLocation getUid() {
        return JadePlugin.IRRIGATION;
    }
}
