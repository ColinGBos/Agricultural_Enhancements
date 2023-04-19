package vapourdrive.agricultural_enhancements.modules.irrigation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IIrrigationBlock {

    BlockState updateIrrigationStrength(BlockGetter pLevel, BlockPos pPos, BlockState pState);

    boolean canConnect(BlockState state, Direction direction);

    void bringNeighboursDown(Direction fromDirection, Level pLevel, BlockPos pPos, int level, BlockPos originPos);

    void bringNeighboursUp(Direction fromDirection, Level pLevel, BlockPos pPos, int level);
}
