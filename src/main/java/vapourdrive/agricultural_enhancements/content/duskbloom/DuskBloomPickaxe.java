package vapourdrive.agricultural_enhancements.content.duskbloom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.vapourware.shared.utils.CompUtils;

import java.util.List;

public class DuskBloomPickaxe extends PickaxeItem {

    public DuskBloomPickaxe(Tier tier, Item.Properties properties) {
        super(tier, properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        DuskBloomTools.inventoryTick(stack, level, entity, slotId, isSelected);

    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(CompUtils.getComp(AgriculturalEnhancements.MODID, "duskbloom_tool").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
