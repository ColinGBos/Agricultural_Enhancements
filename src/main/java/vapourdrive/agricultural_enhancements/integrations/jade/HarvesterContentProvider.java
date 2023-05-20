package vapourdrive.agricultural_enhancements.integrations.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import vapourdrive.agricultural_enhancements.content.harvester.HarvesterTile;

public enum HarvesterContentProvider implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
        if (blockAccessor.getServerData().contains("NonDestructive")) {
            boolean nonDestructive = blockAccessor.getServerData().getBoolean("NonDestructive");
            tooltip.add(Component.translatable("agriculturalenhancements.harvester.nondestructive_short."+nonDestructive));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return JadePlugin.HARVESTER;
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level world, BlockEntity t, boolean showDetails) {
        if (t instanceof HarvesterTile harvester) {
            data.putBoolean("NonDestructive", harvester.isNonDestructive());
        }
    }
}
