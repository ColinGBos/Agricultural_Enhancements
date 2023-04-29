package vapourdrive.agricultural_enhancements.modules.fertilizer;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.modules.soil.TilledSoilBlock;

import javax.annotation.Nullable;
import java.util.List;

public class Fertilizer extends Item {
    public Fertilizer(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("agriculturalenhancements.fertiliser.info").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext ctx) {
        AgriculturalEnhancements.debugLog("Use item");
        for (int i = 0; i<=1; i++) {
            BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos().relative(Direction.DOWN, i));
            if (state.hasProperty(TilledSoilBlock.SOIL_NUTRIENTS)) {
                if (state.getValue(TilledSoilBlock.SOIL_NUTRIENTS) < TilledSoilBlock.MAX_NUTRIENTS) {
                    ctx.getLevel().setBlockAndUpdate(ctx.getClickedPos().relative(Direction.DOWN, i), state.setValue(TilledSoilBlock.SOIL_NUTRIENTS, TilledSoilBlock.MAX_NUTRIENTS));
                    ctx.getItemInHand().shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }
}
