package vapourdrive.agricultural_enhancements.content.irrigation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.content.soil.TilledSoilBlock;
import vapourdrive.agricultural_enhancements.setup.Registration;

public class SprayerPipeBlock extends IrrigationPipeBlock implements IIrrigationBlock {

    public SprayerPipeBlock() {
        this.registerDefaultState(this.stateDefinition.any().setValue(IRRIGATION, 0).setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
    }

    public BlockState getStateForPlacement(BlockGetter pLevel, BlockPos pPos) {
        BlockState blockstate1 = pLevel.getBlockState(pPos.above());
        BlockState blockstate2 = pLevel.getBlockState(pPos.north());
        BlockState blockstate3 = pLevel.getBlockState(pPos.east());
        BlockState blockstate4 = pLevel.getBlockState(pPos.south());
        BlockState blockstate5 = pLevel.getBlockState(pPos.west());
        BlockState[] states = {blockstate1, blockstate2, blockstate3, blockstate4, blockstate5};
        int irrigation = 0;
        for (BlockState state : states) {
            if (state.hasProperty(IRRIGATION)) {
                int neighbor_irrigation = state.getValue(IRRIGATION);
                if (neighbor_irrigation > irrigation) {
                    irrigation = neighbor_irrigation;
                }
            }
        }
        if (irrigation > 0) {
            irrigation -= 1;
        }

        return this.defaultBlockState().setValue(IRRIGATION, irrigation).setValue(DOWN, false).setValue(UP, canConnect(blockstate1, Direction.NORTH)).setValue(NORTH, canConnect(blockstate2, Direction.NORTH)).setValue(EAST, canConnect(blockstate3, Direction.EAST)).setValue(SOUTH, canConnect(blockstate4, Direction.SOUTH)).setValue(WEST, canConnect(blockstate5, Direction.WEST));
    }


    /**
     * Update the provided state given the provided neighbor direction and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific direction passed in.
     */
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pFacing, @NotNull BlockState pFacingState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pFacingPos) {
        pLevel.scheduleTick(pCurrentPos, this, pLevel.getRandom().nextInt(20) + 10);

        if (pFacing != Direction.DOWN) {
            int irrigation = pState.getValue(IRRIGATION);
            if (pFacingState.hasProperty(IRRIGATION)) {
                int neighbor_irrigation = pFacingState.getValue(IRRIGATION);
                if (neighbor_irrigation > irrigation) {
                    irrigation = neighbor_irrigation - 1;
                }
            }
            return pState.setValue(PROPERTY_BY_DIRECTION.get(pFacing), canConnect(pFacingState, pFacing)).setValue(IRRIGATION, irrigation);
        }
        return pState.setValue(PROPERTY_BY_DIRECTION.get(pFacing), false);
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction) {
        if (direction == Direction.DOWN) {
            return false;
        }
        return state.getBlock() instanceof IIrrigationBlock || state.is(Registration.IRRIGATION_CONTROLLER_BLOCK.get());
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState pState) {
        return false;

//        return pState.getValue(IRRIGATION) > 0;
    }

    @Override
    public boolean canSurvive(@NotNull BlockState pState, @NotNull LevelReader pLevel, @NotNull BlockPos pPos) {
        AgriculturalEnhancements.debugLog("Checking if the block can survive");
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos blockPos = pPos.offset(x, y, z);
                    if (blockPos == pPos) {
                        continue;
                    }
                    BlockState state = pLevel.getBlockState(blockPos);

                    if (!state.isAir() && state.is(this)) {
//                        AgriculturalEnhancements.debugLog("state: "+state);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void tick(@NotNull BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        if (pState.getValue(IRRIGATION)>0){
            performGrowAndWater(pLevel, pPos, pRandom);
        }
        pLevel.scheduleTick(pPos, this, pRandom.nextInt(40) + 60);
    }

    @Override
    public void randomTick(@NotNull BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
//        performGrowAndWater(pLevel, pPos, pRandom);
    }

    public void performGrowAndWater(@NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y > -ConfigSettings.SPRAYER_VERTICAL_RANGE.get(); y--) {
                    boolean stop = false;
                    BlockPos blockPos = pPos.offset(x, y, z);
                    BlockState state = pLevel.getBlockState(blockPos);
                    if (!state.isAir()){
                        if (pRandom.nextFloat() > 0.7f && state.getBlock() instanceof CropBlock crop) {
                            if (pRandom.nextFloat() <= ConfigSettings.SPRAYER_CHANCE_TO_BOOST_CROP_GROWTH.get()) {
                                for (int l = 0; l < ConfigSettings.SPRAYER_CROP_TICK_COUNT.get(); l++) {
                                    if (pRandom.nextFloat() > 0.85){
                                        crop.performBonemeal(pLevel, pRandom, blockPos, state);
                                    }
                                }
                            }
                        }
                        stop = true;
                        performWater(x, y, z, pLevel, pPos);
                    }

                    if (stop) {
                        break;
                    }
                }
            }
        }
    }

    public void performWater(int x, int y, int z, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos){
        for (int l = y; l >= -3; l--) {
            BlockPos soilPos = pPos.offset(x, y, z);
            BlockState soilState = pLevel.getBlockState(soilPos);
            if (!soilState.isAir() && soilState.getBlock() instanceof TilledSoilBlock) {
                if (!pLevel.isClientSide()) {
                    pLevel.setBlockAndUpdate(soilPos, soilState.setValue(TilledSoilBlock.SOIL_MOISTURE, TilledSoilBlock.MAX_MOISTURE));
                    break;
                }
            }
        }
    }

    @Override
    public void animateTick(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        animateSprinkles(pState, pLevel, pPos, pRandom);

    }

    public void animateSprinkles(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        if (pLevel.getRandom().nextFloat() > ConfigSettings.SPRAYER_CHANCE_TO_ANIMATE.get() || pState.getValue(IRRIGATION) <= 0) {
            return;
        }
        for (int l = 0; l <= 100; l++) {
            double d0 = (pRandom.nextDouble() - 0.5) * 0.15;
            double d2 = (pRandom.nextDouble() - 0.5) * 0.15;
            pLevel.addParticle(ParticleTypes.SPLASH, pPos.getX() + 0.5 + d0 * 10, pPos.getY() + 0.2, pPos.getZ() + 0.5 + d2 * 10, d0, 0.0D, d2);
        }
    }

}
