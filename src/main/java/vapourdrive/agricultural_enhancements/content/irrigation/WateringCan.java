package vapourdrive.agricultural_enhancements.content.irrigation;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.content.soil.TilledSoilBlock;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;

public class WateringCan extends Item {
    DecimalFormat df = new DecimalFormat("#,###");

    public WateringCan(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext ctx) {
        return InteractionResult.PASS;
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (getWater(pPlayer.getItemInHand(pUsedHand)) < getMaxWater()) {
            BlockPos pos;
            BlockHitResult blockhitresult = getPlayerPOVHitResult(pPlayer.getLevel(), pPlayer, ClipContext.Fluid.SOURCE_ONLY);
            if (blockhitresult.getType() == HitResult.Type.BLOCK) {
                pos = blockhitresult.getBlockPos();
                BlockState state = pLevel.getBlockState(pos);
                if (state.getBlock() instanceof LiquidBlock liquidBlock) {
                    if (liquidBlock.getFluidState(state).isSourceOfType(Fluids.WATER)) {
                        setWater(pPlayer.getItemInHand(pUsedHand), getMaxWater());
                        liquidBlock.pickupBlock(pLevel, pos, state);
                    }

                }
            }
        }

        pPlayer.startUsingItem(pUsedHand);
        AgriculturalEnhancements.debugLog("Started Using");
//        water(pos, pLevel, pPlayer.getItemInHand(pUsedHand));

        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));

    }

    public void water(BlockPos pos, Level level, ItemStack can) {
        AgriculturalEnhancements.debugLog("Water: " + getWater(can));
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int soilOffset = 0;
                BlockPos blockPos = pos.offset(i, 0, j);
                BlockState state = level.getBlockState(blockPos);
                if (!state.isAir() && state.getBlock() instanceof CropBlock crop) {
                    soilOffset = -1;
                    if (level.getRandom().nextFloat() > 0.5) {
                        if (!level.isClientSide()) {
                            crop.randomTick(state, (ServerLevel) level, blockPos, level.getRandom());
                        }
                    }
                }
                BlockPos soilPos = pos.offset(i, soilOffset, j);
                BlockState soilState = level.getBlockState(soilPos);
                if (!soilState.isAir() && soilState.getBlock() instanceof TilledSoilBlock) {
                    if (level.getRandom().nextFloat() > 0.5) {
                        if (!level.isClientSide()) {
                            int moisture = soilState.getValue(TilledSoilBlock.SOIL_MOISTURE);
                            level.setBlock(soilPos, soilState.setValue(TilledSoilBlock.SOIL_MOISTURE, Math.min(TilledSoilBlock.MAX_MOISTURE, moisture + 2)), 19);
//                            soilState.setValue(TilledSoilBlock.SOIL_MOISTURE, Math.min(TilledSoilBlock.MAX_MOISTURE, moisture+1));
                        }
                    }
                }
            }
        }
    }

    public void splash(Level level, BlockPos pos) {
        for (int i = 0; i <= 5; i++) {
            double d3 = (level.getRandom().nextDouble() - 0.5) * 0.2;
            double d4 = (level.getRandom().nextDouble() - 0.5) * 0.2;
            //                AgriculturalEnhancements.debugLog("X speed: "+d0+" Z speed"+d2);
            level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5 + d3 * 5, pos.getY() + 1.25, pos.getZ() + 0.5 + d4 * 5, d3, 0.0D, d4);
        }
    }

    public void onUsingTick(ItemStack stack, LivingEntity pLivingEntity, int count) {
        if (getWater(stack) <= 0) {
            return;
        }

        if (count % 2 == 0 && count < 995) {
            AgriculturalEnhancements.debugLog("Tick");
            if (pLivingEntity instanceof Player player) {
                BlockPos pos = player.getOnPos().relative(Direction.UP).relative(player.getDirection());
                BlockHitResult blockhitresult = getPlayerPOVHitResult(player.getLevel(), player, ClipContext.Fluid.NONE);
                if (blockhitresult.getType() == HitResult.Type.BLOCK) {
                    pos = blockhitresult.getBlockPos();
                }
                if (count % 10 == 0) {
                    AgriculturalEnhancements.debugLog("Count: " + count);
                    water(pos, pLivingEntity.getLevel(), stack);
                    consumeWater(stack, 50);
                }
                splash(pLivingEntity.getLevel(), pos);
            }
        }
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.BLOCK;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack pStack) {
        return Math.round((float) getWater(pStack) / (float) getMaxWater() * 13.0F);
    }

    @Override
    public int getBarColor(@NotNull ItemStack pStack) {
        float f = (float) getWater(pStack) / (float) getMaxWater() * 0.5f;
        return Mth.hsvToRgb(0.6F, 0.5F + f, 0.5F + f);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return 1000;
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().putInt("Water", 0);
        return stack;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal("Water: ").append(df.format(getWater(pStack)) + "/" + df.format(getMaxWater()) + " mB").withStyle(ChatFormatting.BLUE));
    }

    public int getWater(ItemStack stack) {
        if (!stack.hasTag()) {
            return 0;
        } else {
            assert stack.getTag() != null;
            return stack.getTag().getInt("Water");
        }
    }

    public void setWater(ItemStack stack, int water) {
        stack.getOrCreateTag().putInt("Water", Math.min(water, getMaxWater()));
    }

    public void consumeWater(ItemStack stack, int water) {
        AgriculturalEnhancements.debugLog("Consuming: " + water);
        stack.getOrCreateTag().putInt("Water", Math.max(getWater(stack) - water, 0));
    }

    public int getMaxWater() {
        return 1000;
    }

}
