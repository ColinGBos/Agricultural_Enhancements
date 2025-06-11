package vapourdrive.agricultural_enhancements.content.duskbloom.tools;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DuskBloomTools {
    public static void inventoryTick(@NotNull ItemStack stack, @NotNull Level level) {
        int daytime = (int) (level.getDayTime() % 24000);
        if(daytime > 12500 && daytime < 13500) {
            if (level.getRandom().nextFloat() < 0.02) {
                if (stack.getDamageValue() > 0) {
                    stack.setDamageValue(stack.getDamageValue()-1);
                }
            }
        }
    }
}
