package vapourdrive.agricultural_enhancements.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;

@Mixin(CropBlock.class)
public class MixinCropBlock {
    @Inject(at = @At("HEAD"), method = "getShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
    private void getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext, CallbackInfoReturnable<VoxelShape> callback) {
        VoxelShape shape;
        if(ConfigSettings.REPLACE_CROP_BLOCK_SHAPE.get()){
            shape = SKINNY_SHAPE[pState.getValue(this.getAgeProperty())];
        } else{
            shape = SHAPE_BY_AGE[pState.getValue(this.getAgeProperty())];
        }

        callback.setReturnValue(shape);
    }

    @Shadow
    public @NotNull IntegerProperty getAgeProperty() {
        throw new IllegalStateException("Mixin failed to shadow getAgeProperty()");
    }
    @Final
    @Shadow
    private static VoxelShape[] SHAPE_BY_AGE;

    private static final VoxelShape[] SKINNY_SHAPE = new VoxelShape[]{
        Block.box(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D),
        Block.box(1.0D, 0.0D, 1.0D, 15.0D, 4.0D, 15.0D),
        Block.box(1.0D, 0.0D, 1.0D, 15.0D, 6.0D, 15.0D),
        Block.box(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D),
        Block.box(1.0D, 0.0D, 1.0D, 15.0D, 10.0D, 15.0D),
        Block.box(1.0D, 0.0D, 1.0D, 15.0D, 12.0D, 15.0D),
        Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D),
        Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D)
    };
}

