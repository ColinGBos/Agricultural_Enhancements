package vapourdrive.agricultural_enhancements.content.duskbloom.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.content.duskbloom.tools.DuskBloomTools;
import vapourdrive.vapourware.shared.utils.CompUtils;

import java.util.List;

public class DuskbloomArmorItem extends ArmorItem {
    public DuskbloomArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        DuskBloomTools.inventoryTick(stack, level);
    }

    @Override
    public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()) {
            tooltipComponents.add(CompUtils.getComp(AgriculturalEnhancements.MODID, "duskbloom_tool").withStyle(ChatFormatting.GRAY));
        } else {
            CompUtils.addShiftInfo(tooltipComponents);
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
