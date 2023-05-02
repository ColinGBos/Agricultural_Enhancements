package vapourdrive.agricultural_enhancements.modules.fertilizer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class FertilizerProducerItem extends BlockItem {
    public FertilizerProducerItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
//        DecimalFormat df = new DecimalFormat("#,###");
//        list.add(Component.literal(df.format(ConfigSettings.FURNACE_BASE_SPEED.get()*100 )+"% speed").withStyle(ChatFormatting.BLUE));
        list.add(Component.translatable("message.agriculturalenhancements.fertilizer_producer_1").withStyle(ChatFormatting.BLUE));
        list.add(Component.translatable("message.agriculturalenhancements.fertilizer_producer_2").withStyle(ChatFormatting.BLUE));
    }
}
