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
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.modules.base.IFuelUser;
import vapourdrive.agricultural_enhancements.modules.soil.TilledSoilBlock;

import java.text.DecimalFormat;
import java.util.Objects;

public enum IFuelUserContentProvider implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {
    INSTANCE;
    private DecimalFormat df = new DecimalFormat("#,###");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
        if (blockAccessor.getServerData().contains("Fuel")) {
            int i = blockAccessor.getServerData().getInt("Fuel");
            tooltip.add(Component.translatable("agriculturalenhancements.jade.fuel", df.format(i)));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return JadePlugin.FUEL;
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level world, BlockEntity t, boolean showDetails) {
        if(t instanceof IFuelUser user) {
            data.putInt("Fuel", user.getCurrentFuel()/100);
        }
    }
}
