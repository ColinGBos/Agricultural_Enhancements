package vapourdrive.agricultural_enhancements.content.base;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class FarmerWrench extends Item {
    public static final TagKey<Item> wrench = ItemTags.create(new ResourceLocation("forge", "tools/wrench"));

    public FarmerWrench(Item.Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        BlockState state = ctx.getLevel().getBlockState(pos);

        if (Objects.requireNonNull(ctx.getPlayer()).isCrouching()) {
            if (state.getBlock() instanceof AbstractBaseMachineBlock machine) {
                if (machine.sneakWrenchMachine(ctx.getPlayer(), ctx.getLevel(), pos)) {
                    return InteractionResult.SUCCESS;
                }
            }
        }

        ctx.getLevel().setBlockAndUpdate(pos, state.rotate(ctx.getLevel(), pos, Rotation.CLOCKWISE_90));
        state.rotate(ctx.getLevel(), pos, Rotation.CLOCKWISE_90);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("agriculturalenhancements.farmer_wrench.info"));
    }
}
