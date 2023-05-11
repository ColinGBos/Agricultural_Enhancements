package vapourdrive.agricultural_enhancements.integrations.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import vapourdrive.agricultural_enhancements.modules.soil.TilledSoilBlock;

public enum CropContentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
        BlockState state = blockAccessor.getLevel().getBlockState(blockAccessor.getPosition().below());
        if (state.hasProperty(TilledSoilBlock.SOIL_NUTRIENTS) && state.hasProperty(TilledSoilBlock.SOIL_NUTRIENTS)) {
            int nutrients = state.getValue(TilledSoilBlock.SOIL_NUTRIENTS);
            int moisture = state.getValue(TilledSoilBlock.SOIL_MOISTURE);
            tooltip.add(Component.translatable("agriculturalenhancements.jade.nutrients", nutrients));
            tooltip.add(Component.translatable("agriculturalenhancements.jade.moisture", moisture));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return JadePlugin.CROPS;
    }
}
