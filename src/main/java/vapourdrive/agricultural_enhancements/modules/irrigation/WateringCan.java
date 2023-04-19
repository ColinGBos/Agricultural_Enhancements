package vapourdrive.agricultural_enhancements.modules.irrigation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WateringCan extends Item {
    public WateringCan(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        BlockPos pos = ctx.getClickedPos();

        water(pos, ctx.getLevel());

        return InteractionResult.SUCCESS;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        BlockPos pos = pPlayer.getOnPos().relative(Direction.UP).relative(pPlayer.getDirection());
        water(pos, pLevel);

        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));

    }

    public void water(BlockPos pos, Level level) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                BlockPos blockPos = pos.offset(i, 0, j);
                BlockState state = level.getBlockState(blockPos);
                if (!state.isAir() && state.getBlock() instanceof CropBlock crop) {
                    if (level.getRandom().nextFloat() > 0.9) {
                        if (!level.isClientSide()) {
                            crop.randomTick(state, (ServerLevel) level, blockPos, level.getRandom());
                        }
                    }
                }
                for (int k = 0; k <= 5; k++) {
                    double d3 = (level.getRandom().nextDouble() - 0.5) * 0.2;
                    double d4 = (level.getRandom().nextDouble() - 0.5) * 0.2;
//                AgriculturalEnhancements.debugLog("X speed: "+d0+" Z speed"+d2);
                    level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5 + d3 * 5, pos.getY() + 1, pos.getZ() + 0.5 + d4 * 5, d3, 0.0D, d4);
                }
            }
        }
    }
}
