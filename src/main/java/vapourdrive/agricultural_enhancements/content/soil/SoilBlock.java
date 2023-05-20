package vapourdrive.agricultural_enhancements.content.soil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class SoilBlock extends TilledSoilBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public SoilBlock() {
        super();
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pFacing, @NotNull BlockState pFacingState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pFacingPos) {
        pLevel.scheduleTick(pCurrentPos, this, pLevel.getRandom().nextInt(200) + 400);

        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void tick(BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        int moistureIn = pState.getValue(SOIL_MOISTURE);
        int moistureOut = moistureIn;
        int potentialMoisture = getMaxMoisture(pLevel, pPos);
        if (potentialMoisture - 1 > moistureIn) {
            moistureOut++;
        } else if (moistureIn >= potentialMoisture) {
            moistureOut--;
        }
        moistureOut = Math.max(moistureOut, 0);

        if (moistureIn != moistureOut) {
            pLevel.scheduleTick(pPos, this, pRandom.nextInt(200) + 400);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(SOIL_MOISTURE, Math.min(moistureOut, MAX_MOISTURE)));
        }
    }

    @Override
    public void randomTick(BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
    }

    @Override
    public void fallOn(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockPos pPos, @NotNull Entity pEntity, float pFallDistance) {
    }


    @Override
    public boolean canSustainPlant(@NotNull BlockState state, @NotNull BlockGetter world, BlockPos pos, @NotNull Direction facing, net.minecraftforge.common.IPlantable plantable) {
        net.minecraftforge.common.PlantType type = plantable.getPlantType(world, pos.relative(facing));
        return net.minecraftforge.common.PlantType.PLAINS.equals(type);
    }
}
