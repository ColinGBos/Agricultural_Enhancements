package vapourdrive.agricultural_enhancements.content.irrigation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.PositionImpl;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.setup.Registration;

public class IrrigationPipeBlock extends PipeBlock implements IIrrigationBlock {

    public static final IntegerProperty IRRIGATION = IntegerProperty.create("irrigation", 0, 15);

    public IrrigationPipeBlock() {
        super(0.125F, BlockBehaviour.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(1.5f)
                .requiresCorrectToolForDrops()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(UP, Boolean.FALSE).setValue(DOWN, Boolean.FALSE).setValue(IRRIGATION, 0));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.getStateForPlacement(pContext.getLevel(), pContext.getClickedPos());
    }

    public BlockState getStateForPlacement(BlockGetter pLevel, BlockPos pPos) {
        BlockState blockstate = pLevel.getBlockState(pPos.below());
        BlockState blockstate1 = pLevel.getBlockState(pPos.above());
        BlockState blockstate2 = pLevel.getBlockState(pPos.north());
        BlockState blockstate3 = pLevel.getBlockState(pPos.east());
        BlockState blockstate4 = pLevel.getBlockState(pPos.south());
        BlockState blockstate5 = pLevel.getBlockState(pPos.west());
        BlockState[] states = {blockstate, blockstate1, blockstate2, blockstate3, blockstate4, blockstate5};
        int irrigation = 0;
        for (BlockState state : states) {
            if (state.hasProperty(IRRIGATION)) {
                int neighbor_irrigation = state.getValue(IRRIGATION);
//                AgriculturalEnhancements.debugLog("Neighbor"+ state+" Irrigation: "+ irrigation);
                if (neighbor_irrigation > irrigation) {
                    irrigation = neighbor_irrigation;
                }
            }
        }
        if (irrigation > 0) {
            irrigation -= 1;
        }
        AgriculturalEnhancements.debugLog("Irrigation: " + irrigation);
        return this.defaultBlockState().setValue(IRRIGATION, irrigation).setValue(UP, canConnect(blockstate1, Direction.UP)).setValue(DOWN, canConnect(blockstate, Direction.DOWN)).setValue(NORTH, canConnect(blockstate2, Direction.NORTH)).setValue(EAST, canConnect(blockstate3, Direction.EAST)).setValue(SOUTH, canConnect(blockstate4, Direction.SOUTH)).setValue(WEST, canConnect(blockstate5, Direction.WEST));
    }

    public BlockState updateIrrigationStrength(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        BlockState blockstate = pLevel.getBlockState(pPos.below());
        BlockState blockstate1 = pLevel.getBlockState(pPos.above());
        BlockState blockstate2 = pLevel.getBlockState(pPos.north());
        BlockState blockstate3 = pLevel.getBlockState(pPos.east());
        BlockState blockstate4 = pLevel.getBlockState(pPos.south());
        BlockState blockstate5 = pLevel.getBlockState(pPos.west());
        BlockState[] states = {blockstate, blockstate1, blockstate2, blockstate3, blockstate4, blockstate5};
        int irrigation = 0;
        if (pState != null) {
            irrigation = pState.getValue(IRRIGATION);
        }
        for (BlockState state : states) {
            if (state.hasProperty(IRRIGATION)) {
                int neighbor_irrigation = state.getValue(IRRIGATION);
//                AgriculturalEnhancements.debugLog("Neighbor"+ state+" Irrigation: "+ irrigation);
                if (neighbor_irrigation > irrigation) {
                    irrigation = neighbor_irrigation;
                }
            }
        }
        if (irrigation > 0) {
            irrigation -= 1;
        }
//        AgriculturalEnhancements.debugLog("Irrigation: "+ irrigation);
        return this.defaultBlockState().setValue(IRRIGATION, irrigation);
    }

    public boolean canConnect(BlockState state, Direction direction) {
        if (direction == Direction.UP) {
            return state.is(this);
        }
        if (state.getBlock() instanceof IIrrigationBlock) {
            return true;
        }
        if (state.is(Registration.IRRIGATION_CONTROLLER_BLOCK.get()) && state.hasProperty(HorizontalDirectionalBlock.FACING)) {
            if (direction == Direction.DOWN) {
                return true;
            } else return state.getValue(HorizontalDirectionalBlock.FACING).equals(direction);
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pOldState.is(pState.getBlock()) && !pLevel.isClientSide) {
            pState = this.updateIrrigationStrength(pLevel, pPos, pState);
            int my_level = pState.getValue(IRRIGATION);
//            AgriculturalEnhancements.debugLog("Irrigation (onPlace): "+my_level);
            for (Direction direction : Direction.values()) {
                BlockState neighbor = pLevel.getBlockState(pPos.relative(direction));
                if (neighbor.getBlock() instanceof IIrrigationBlock pipe) {
                    if (my_level > 1) {
                        if (my_level - neighbor.getValue(IRRIGATION) > 0) {
                            AgriculturalEnhancements.debugLog("Irrigation (updating): " + my_level);
                            pLevel.setBlockAndUpdate(pPos.relative(direction), neighbor.setValue(IRRIGATION, my_level - 1));
                            pipe.bringNeighboursUp(direction.getOpposite(), pLevel, pPos.relative(direction), my_level - 1);
                        }
                    }
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
            if (!pLevel.isClientSide) {
                for (Direction direction : Direction.values()) {
                    BlockState neighbor = pLevel.getBlockState(pPos.relative(direction));
                    if (neighbor.getBlock() instanceof IIrrigationBlock pipe) {
                        int neighborLevel = neighbor.getValue(IRRIGATION);
//                        if (level >= neighborLevel && neighborLevel > 0) {
                        if (neighborLevel > 0) {
//                            pLevel.setBlock(pPos.relative(direction), neighbor.setValue(IRRIGATION, 0), 2);
//                            neighbor.updateShape(direction, neighbor.setValue(IRRIGATION, 0), pLevel, pPos, pPos.relative(direction));

                            pLevel.setBlockAndUpdate(pPos.relative(direction), neighbor.setValue(IRRIGATION, 0));
                            pipe.bringNeighboursDown(direction.getOpposite(), pLevel, pPos.relative(direction), neighborLevel, pPos);
                        }
//                        else if(neighborLevel > level){
//                            pipe.bringNeighboursUp(direction.getOpposite(), pLevel, pPos.relative(direction), neighborLevel);
//                        }
                    }
                }
            }
        }
    }


    public void bringNeighboursUp(Direction fromDirection, Level pLevel, BlockPos pPos, int level) {
        if (level > 1) {
            for (Direction direction : Direction.values()) {
                if (direction != fromDirection) {
                    BlockState neighbor = pLevel.getBlockState(pPos.relative(direction));
                    if (neighbor.getBlock() instanceof IIrrigationBlock pipe) {
                        if (level - neighbor.getValue(IRRIGATION) > 0) {
                            pLevel.setBlockAndUpdate(pPos.relative(direction), neighbor.setValue(IRRIGATION, level - 1));
                            pipe.bringNeighboursUp(direction.getOpposite(), pLevel, pPos.relative(direction), level - 1);
                        }
                    }
                }
            }
        }
    }

    public void bringNeighboursDown(Direction fromDirection, Level pLevel, BlockPos pPos, int level, BlockPos originPos) {
        if (!pPos.closerToCenterThan(new PositionImpl(originPos.getX(), originPos.getY(), originPos.getZ()), 16)) {
            return;
        }
        for (Direction direction : Direction.values()) {
            if (direction != fromDirection) {
                BlockState neighbor = pLevel.getBlockState(pPos.relative(direction));
                if (neighbor.getBlock() instanceof IIrrigationBlock pipe) {
                    int neighborLevel = neighbor.getValue(IRRIGATION);
                    //if (level >= neighborLevel && neighborLevel > 0) {
                    if (neighborLevel > 0) {
//                        neighbor.updateShape(direction, neighbor.setValue(IRRIGATION, 0), pLevel, pPos, pPos.relative(direction));
//                        pLevel.setBlock(pPos.relative(direction), neighbor.setValue(IRRIGATION, 0),2);

                        pLevel.setBlockAndUpdate(pPos.relative(direction), neighbor.setValue(IRRIGATION, 0));
                        AgriculturalEnhancements.debugLog("Bringing irrigation down: " + pLevel.getBlockState(pPos.relative(direction)).getValue(IRRIGATION));
                        pipe.bringNeighboursDown(direction.getOpposite(), pLevel, pPos.relative(direction), level, originPos);
                    }
//                    else if (neighborLevel>level){
//                        pipe.bringNeighboursUp(null, pLevel, pPos.relative(direction), neighborLevel);
//                    }
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult trace) {
        if (level.isClientSide) {
            ItemStack stick = player.getMainHandItem();
            if (stick.is(Items.STICK)) {
//                MachineUtils.animate(level, pos, level.getRandom(), SoundEvents.CROP_BREAK);
                int irrigation_level = state.getValue(IRRIGATION);
                float pitch = 1.4f;
                if (irrigation_level > 0) {
                    pitch = 1f - (((float) irrigation_level / 70f));
                }
                level.playSound(player, pos, SoundEvents.COPPER_BREAK, SoundSource.BLOCKS, 1.0F, pitch);
                AgriculturalEnhancements.debugLog("Irrigation: " + state.getValue(IRRIGATION));
            }
        } else if (AgriculturalEnhancements.isDebugMode()) {
            if (player.isCrouching()) {
                level.setBlockAndUpdate(pos, state.setValue(IRRIGATION, 15));
                IIrrigationBlock pipe = (IIrrigationBlock) state.getBlock();
                pipe.bringNeighboursUp(null, level, pos, 15);
                AgriculturalEnhancements.debugLog("Irrigation: " + state.getValue(IRRIGATION));
            }
        }
        return InteractionResult.PASS;
    }

    /**
     * Update the provided state given the provided neighbor direction and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific direction passed in.
     */
    @Override
    @SuppressWarnings("deprecation")
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pFacing, @NotNull BlockState pFacingState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pFacingPos) {
//        AgriculturalEnhancements.debugLog("Direction: "+ pFacing);
        if (pFacing == Direction.UP) {
            return pState.setValue(PROPERTY_BY_DIRECTION.get(pFacing), pFacingState.is(this));
        } else {
            return pState.setValue(PROPERTY_BY_DIRECTION.get(pFacing), canConnect(pFacingState, pFacing));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, IRRIGATION);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull PathComputationType pType) {
        return true;
    }


}
