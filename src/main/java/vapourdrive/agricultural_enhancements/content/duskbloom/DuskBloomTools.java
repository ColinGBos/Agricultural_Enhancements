package vapourdrive.agricultural_enhancements.content.duskbloom;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DuskBloomTools {
    public static void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        long daytime = level.dayTime();
        if(daytime > 12500 && daytime < 13500) {
            if (level.getRandom().nextFloat() > 0.95) {
                if (stack.getDamageValue() > 0) {
                    stack.setDamageValue(stack.getDamageValue()-1);
                }
            }
        }
    }
}
