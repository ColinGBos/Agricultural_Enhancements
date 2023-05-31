package vapourdrive.agricultural_enhancements.content.irrigation.irrigation_controller;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import vapourdrive.vapourware.shared.base.BaseMachineItem;

import javax.annotation.Nullable;
import java.util.List;

public class IrrigationControllerItem extends BaseMachineItem {
    public IrrigationControllerItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        list.add(Component.translatable("agriculturalenhancements.irrigation_controller.info_1").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, list, flag);
    }
}
