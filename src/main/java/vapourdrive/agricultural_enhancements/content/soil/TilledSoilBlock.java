package vapourdrive.agricultural_enhancements.content.soil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.FarmlandWaterManager;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.setup.Registration;

public class TilledSoilBlock extends Block {
    public static final IntegerProperty SOIL_MOISTURE = IntegerProperty.create("soil_moisture", 0, 5);
    public static final IntegerProperty SOIL_NUTRIENTS = IntegerProperty.create("soil_nutrients", 0, 5);
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    public static final int MAX_MOISTURE = 5;
    public static final int MAX_NUTRIENTS = 5;

    public TilledSoilBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).instrument(NoteBlockInstrument.BANJO).strength(0.6f).sound(SoundType.GRAVEL));
        this.registerDefaultState(this.stateDefinition.any().setValue(SOIL_MOISTURE, 0).setValue(SOIL_NUTRIENTS, 3));
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState pState) {
        return true;
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pFacing, @NotNull BlockState pFacingState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pFacingPos) {
//        AgriculturalEnhancements.debugLog("Facing: "+pFacing+", state: "+pFacingState);
        if (pFacing == Direction.UP && !pState.canSurvive(pLevel, pCurrentPos)) {
            int moistureIn = pState.getValue(SOIL_MOISTURE);
            int nutrients = pState.getValue(SOIL_NUTRIENTS);
            pLevel.setBlock(pCurrentPos, Registration.SOIL_BLOCK.get().defaultBlockState().setValue(SOIL_MOISTURE, moistureIn).setValue(SOIL_NUTRIENTS, nutrients), 3);
        } else {
            pLevel.scheduleTick(pCurrentPos, this, pLevel.getRandom().nextInt(200) + 400);
        }

        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

//    @Override
//    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult trace) {
//        if (level.isClientSide) {
//            AgriculturalEnhancements.debugLog("State: moisture: " + state.getValue(SOIL_MOISTURE) + ", nutrients: " + state.getValue(SOIL_NUTRIENTS));
//        } else if (player.getItemInHand(InteractionHand.OFF_HAND).is(Items.BONE_MEAL)) {
//            level.setBlockAndUpdate(pos, state.setValue(SOIL_NUTRIENTS, 5));
//        }
//        return InteractionResult.PASS;
//    }

    @Override
    public boolean canSurvive(@NotNull BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockState blockstate = pLevel.getBlockState(pPos.above());
        return meetsSurviveConditions(blockstate);
    }

    @SuppressWarnings("deprecation")
    private boolean meetsSurviveConditions(BlockState blockstate) {
        return !blockstate.isSolid() || blockstate.getBlock() instanceof FenceGateBlock || blockstate.getBlock() instanceof MovingPistonBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return getStateForPlacement(pContext.getLevel(), pContext.getClickedPos());
    }

    public BlockState getStateForPlacement(Level level, BlockPos pos) {
//        int baseMoisture = Math.max((int) ((level.getBiome(pos).get().getModifiedClimateSettings().downfall() - 0.3f) / 0.2f), 0);
        int baseMoisture = Math.max((int) ((level.getBiome(pos).value().getModifiedClimateSettings().downfall() - 0.3f) / 0.2f), 0);
        return this.defaultBlockState().setValue(SOIL_MOISTURE, baseMoisture);
    }

    @Override
    public boolean useShapeForLightOcclusion(@NotNull BlockState pState) {
        return true;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void tick(BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        int moistureIn = pState.getValue(SOIL_MOISTURE);
        int nutrients = pState.getValue(SOIL_NUTRIENTS);
        if (!meetsSurviveConditions(pLevel.getBlockState(pPos.above()))) {
            pLevel.setBlockAndUpdate(pPos, Registration.SOIL_BLOCK.get().defaultBlockState().setValue(SOIL_MOISTURE, moistureIn).setValue(SOIL_NUTRIENTS, nutrients));
            return;
        }
        int moistureOut = getMoistureOut(moistureIn, pLevel, pPos);

        if (moistureIn != moistureOut) {
            pLevel.scheduleTick(pPos, this, pRandom.nextInt(200) + 400);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(SOIL_MOISTURE, Math.min(moistureOut, MAX_MOISTURE)));
        }
    }

    @Override
    public void randomTick(BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        int moistureIn = pState.getValue(SOIL_MOISTURE);
        int nutrientIn = pState.getValue(SOIL_NUTRIENTS);
        if (!meetsSurviveConditions(pLevel.getBlockState(pPos.above()))) {
            pLevel.setBlockAndUpdate(pPos, Registration.SOIL_BLOCK.get().defaultBlockState().setValue(SOIL_MOISTURE, moistureIn).setValue(SOIL_NUTRIENTS, nutrientIn));
            return;
        }

        int nutrientOut = nutrientIn;
        BlockPos blockPos = pPos.above();
        BlockState state = pLevel.getBlockState(blockPos);
        if (!state.isAir() && state.getBlock() instanceof CropBlock crop) {
            for (int l = 0; l < nutrientIn * 3 + moistureIn; l++) {
                if (pLevel.getRandom().nextFloat() <= ConfigSettings.SOIL_CHANCE_TO_BOOST_CROP_GROWTH.get()) {
//                    crop.randomTick(state, pLevel, blockPos, pRandom);
                    if (pRandom.nextFloat() > 0.85){
                        crop.performBonemeal(pLevel, pRandom, blockPos, state);
                    }
                }
            }
            if (pLevel.getRandom().nextFloat() <= ConfigSettings.SOIL_CHANCE_TO_LOSE_NUTRIENTS.get()) {
                nutrientOut--;
            }
        }
        int moistureOut = getMoistureOut(moistureIn, pLevel, pPos);

        nutrientOut = Math.max(nutrientOut, 0);
        if (nutrientIn != nutrientOut || moistureIn != moistureOut) {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(SOIL_NUTRIENTS, Math.min(nutrientOut, MAX_NUTRIENTS)).setValue(SOIL_MOISTURE, Math.min(moistureOut, MAX_MOISTURE)));
        }

    }

    public static int getMoistureOut(int moistureIn, Level level, BlockPos pos) {
        int moistureOut = moistureIn;
        int potentialMoisture = getMaxMoisture(level, pos);
        if (potentialMoisture - 1 > moistureIn) {
            moistureOut++;
        } else if (moistureIn >= potentialMoisture) {
            moistureOut--;
        }
        return Math.max(moistureOut, 0);
    }

    @Override
    public void fallOn(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockPos pPos, @NotNull Entity pEntity, float pFallDistance) {
        if (ConfigSettings.SOIL_SOFT_TRAMPLE.get()) {
            if (!pLevel.isClientSide && CommonHooks.onFarmlandTrample(pLevel, pPos, Blocks.DIRT.defaultBlockState(), pFallDistance, pEntity)) {
                rollBackCrops(pLevel, pPos);
            }
        }

        super.fallOn(pLevel, pState, pPos, pEntity, pFallDistance);
    }

    public static void rollBackCrops(Level pLevel, BlockPos pPos) {
        BlockPos blockPos = pPos.above();
        BlockState state = pLevel.getBlockState(blockPos);
        if (!state.isAir() && state.getBlock() instanceof CropBlock crop) {
            pLevel.setBlockAndUpdate(blockPos, crop.defaultBlockState());
        }
    }

    public static int getMaxMoisture(Level level, BlockPos pPos) {
        if (level.isRainingAt(pPos.above())) {
            return 6;
        }
        if (FarmlandWaterManager.hasBlockWaterTicket(level, pPos)) {
            return 6;
        }
        BlockState state = level.getBlockState(pPos);
        int greatestNeighbor = 0;
        for (Direction direction : Direction.values()) {
            BlockPos testPos = pPos.relative(direction);
            if (state.canBeHydrated(level, pPos, level.getFluidState(testPos), testPos)) {
                return 6;
            }
            BlockState testState = level.getBlockState(pPos.relative(direction));
            if (testState.hasProperty(SOIL_MOISTURE)) {
                int targetMoisture = testState.getValue(SOIL_MOISTURE);
                greatestNeighbor = Math.max(greatestNeighbor, targetMoisture);
            }
        }
        int baseMoisture = getEnvMoisture(level, pPos);
        AgriculturalEnhancements.debugLog("Base moisture: " + baseMoisture);
        return Math.max(greatestNeighbor, baseMoisture);
    }

    public static int getEnvMoisture(LevelReader level, BlockPos pos) {
        return Math.max((int) ((level.getBiome(pos).value().getModifiedClimateSettings().downfall() - 0.2f) / 0.15f), 0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(SOIL_MOISTURE).add(SOIL_NUTRIENTS);
    }

//    @Override
//    public boolean canSustainPlant(@NotNull BlockState state, @NotNull BlockGetter world, BlockPos pos, @NotNull Direction facing, net.minecraftforge.common.IPlantable plantable) {
//        net.minecraftforge.common.PlantType type = plantable.getPlantType(world, pos.relative(facing));
//
//        if (net.minecraftforge.common.PlantType.CROP.equals(type)) {
//            return true;
//        } else return net.minecraftforge.common.PlantType.PLAINS.equals(type);
//    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public @NotNull TriState canSustainPlant(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull Direction facing, BlockState plantState) {
        if (plantState.getBlock() instanceof CropBlock) {
            return TriState.TRUE;
        }
        return TriState.DEFAULT;
    }
}
