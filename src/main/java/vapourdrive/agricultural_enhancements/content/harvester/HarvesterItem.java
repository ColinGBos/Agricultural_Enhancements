package vapourdrive.agricultural_enhancements.content.harvester;

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
import vapourdrive.vapourware.shared.base.BaseMachineItem;

import javax.annotation.Nullable;
import java.util.List;

public class HarvesterItem extends BaseMachineItem {
    public HarvesterItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        list.add(Component.translatable("agriculturalenhancements.harvester.info_1").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, list, flag);
    }

    @Override
    protected List<Component> appendAdditionalTagInfo(List<Component> list, CompoundTag tag) {
        list.add(Component.translatable("agriculturalenhancements.harvester.nondestructive_short." + tag.getBoolean(AgriculturalEnhancements.MODID + ".destructive")));
        return list;
    }

    @Override
    protected void updateAdditional(BlockEntity blockentity, CompoundTag tag) {
        if (blockentity instanceof HarvesterTile machine) {
            machine.setMode(tag.getBoolean(AgriculturalEnhancements.MODID + ".destructive"));
        }
        super.updateAdditional(blockentity, tag);
    }
}
