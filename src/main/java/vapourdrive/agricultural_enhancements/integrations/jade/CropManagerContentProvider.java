package vapourdrive.agricultural_enhancements.integrations.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import vapourdrive.agricultural_enhancements.content.manager.CropManagerTile;

import java.text.DecimalFormat;

public enum CropManagerContentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;
    private final DecimalFormat df = new DecimalFormat("#,###");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
        if (blockAccessor.getServerData().contains("Fertilizer")) {
            int i = blockAccessor.getServerData().getInt("Fertilizer");
            tooltip.add(Component.translatable("agriculturalenhancements.fertilizer", df.format(i)).withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return JadePlugin.CROP_MANAGER;
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof CropManagerTile user) {
            data.putInt("Fertilizer", user.getCurrentFertilizer());
        }
    }
}
