package vapourdrive.agricultural_enhancements.modules.irrigation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.setup.Registration;

public class SprayerPipeBlock extends IrrigationPipeBlock {
    public SprayerPipeBlock() {
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
    }

    public BlockState getStateForPlacement(BlockGetter pLevel, BlockPos pPos) {
        BlockState blockstate1 = pLevel.getBlockState(pPos.above());
        BlockState blockstate2 = pLevel.getBlockState(pPos.north());
        BlockState blockstate3 = pLevel.getBlockState(pPos.east());
        BlockState blockstate4 = pLevel.getBlockState(pPos.south());
        BlockState blockstate5 = pLevel.getBlockState(pPos.west());
        return this.defaultBlockState().setValue(UP, canConnect(blockstate1)).setValue(NORTH, canConnect(blockstate2)).setValue(EAST, canConnect(blockstate3)).setValue(SOUTH, canConnect(blockstate4)).setValue(WEST, canConnect(blockstate5));
    }

    public boolean canConnect(BlockState state){
        return state.is(Registration.IRRIGATION_PIPE_BLOCK.get());
    }

    /**
     * Update the provided state given the provided neighbor direction and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific direction passed in.
     */
    @Override
    public @NotNull BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if(pFacing != Direction.DOWN) {
            return pState.setValue(PROPERTY_BY_DIRECTION.get(pFacing), pFacingState.getBlock() instanceof IrrigationPipeBlock);
        }
        return pState.setValue(PROPERTY_BY_DIRECTION.get(pFacing), false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return true;
    }


}
