package vapourdrive.agricultural_enhancements.content.manager;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.content.base.BaseMachineItem;

import javax.annotation.Nullable;
import java.util.List;

public class CropManagerItem extends BaseMachineItem {
    public CropManagerItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        list.add(Component.translatable("agriculturalenhancements.crop_manager.info_1").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, list, flag);
    }

    @Override
    protected List<Component> appendAdditionalTagInfo(List<Component> list, CompoundTag tag) {
        String fertilizer = df.format(tag.getInt(AgriculturalEnhancements.MODID + ".fertilizer") / ConfigSettings.CROP_MANAGER_SOIL_PROCESS_TIME.get());
        list.add(Component.translatable("agriculturalenhancements.fertilizer", fertilizer));
        return list;
    }

    @Override
    protected void updateAdditional(BlockEntity blockentity, CompoundTag tag) {
        if (blockentity instanceof CropManagerTile machine) {
            machine.addFertilizer(tag.getInt(AgriculturalEnhancements.MODID + ".fertilizer"), false);
        }
        super.updateAdditional(blockentity, tag);
    }
}
