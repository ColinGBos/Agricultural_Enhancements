package vapourdrive.agricultural_enhancements.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class TooltipEvent {
    @SubscribeEvent
    public static void onToolTipEarly(ItemTooltipEvent event) {
        if (AgriculturalEnhancements.isDebugMode()) {
            List<Component> tips = event.getToolTip();
            ItemStack stack = event.getItemStack();
            tips.add(Component.literal(Arrays.toString(stack.getTags().toArray())));
        }
    }
}
