package vapourdrive.agricultural_enhancements.integrations.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.content.fertilizer.producer.FertilizerProducerTile;
import vapourdrive.agricultural_enhancements.content.manager.CropManagerTile;

import java.text.DecimalFormat;

public enum FertilizerProducerContentProvider implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {
    INSTANCE;
    private final DecimalFormat df = new DecimalFormat("#,###");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
        if (blockAccessor.getServerData().contains("n")) {
            int i = blockAccessor.getServerData().getInt("n");
            tooltip.add(Component.translatable("agriculturalenhancements.n", df.format(i)).withStyle(ChatFormatting.GOLD));
            tooltip.append(Component.literal(", "));
        }
        if (blockAccessor.getServerData().contains("p")) {
            int i = blockAccessor.getServerData().getInt("p");
            tooltip.append(Component.translatable("agriculturalenhancements.p", df.format(i)).withStyle(ChatFormatting.WHITE));
            tooltip.append(Component.literal(", "));
        }
        if (blockAccessor.getServerData().contains("k")) {
            int i = blockAccessor.getServerData().getInt("k");
            tooltip.append(Component.translatable("agriculturalenhancements.k", df.format(i)).withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return JadePlugin.FERTILIZER_PRODUCER;
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level world, BlockEntity t, boolean showDetails) {
        if (t instanceof FertilizerProducerTile user) {
            data.putInt("n", user.getCurrentElement(FertilizerProducerTile.Element.N) / ConfigSettings.FERTILIZER_PRODUCER_INGREDIENT_TIME.get());
            data.putInt("p", user.getCurrentElement(FertilizerProducerTile.Element.P) / ConfigSettings.FERTILIZER_PRODUCER_INGREDIENT_TIME.get());
            data.putInt("k", user.getCurrentElement(FertilizerProducerTile.Element.K) / ConfigSettings.FERTILIZER_PRODUCER_INGREDIENT_TIME.get());
        }
    }
}
