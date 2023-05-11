package vapourdrive.agricultural_enhancements.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CropBlock.class)
public class MixinCropBlock {
//    @Inject(at = @At("HEAD"), method = "getShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
//    private void getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext, CallbackInfoReturnable<VoxelShape> callback) {
//        VoxelShape shape = SHAPE_BY_AGE[pState.getValue(this.getAgeProperty())];
////        AgriculturalEnhancements.debugLog("getShape: "+shape);
//        double xMin = Math.max(shape.min(Direction.Axis.X),1.0D/8D);
//        double yMin = shape.min(Direction.Axis.Y);
//        double zMin = Math.max(shape.min(Direction.Axis.Z),1.0D/8D);
//        double xMax = Math.min(shape.max(Direction.Axis.X), 1-(1.0D/8D));
//        double yMax = shape.max(Direction.Axis.Y);
//        double zMax = Math.min(shape.max(Direction.Axis.Z), 1-(1.0D/8D));
//        callback.setReturnValue(Block.box(xMin*16, yMin*16, zMin*16, xMax*16, yMax*16, zMax*16));
//    }
//
//    @Shadow
//    public @NotNull IntegerProperty getAgeProperty() {
//        throw new IllegalStateException("Mixin failed to shadow getItem()");
//    }


    @Final
    @Shadow
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D), Block.box(1.0D, 0.0D, 1.0D, 15.0D, 4.0D, 15.0D), Block.box(1.0D, 0.0D, 1.0D, 15.0D, 6.0D, 15.0D), Block.box(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(1.0D, 0.0D, 1.0D, 15.0D, 10.0D, 15.0D), Block.box(1.0D, 0.0D, 1.0D, 15.0D, 12.0D, 15.0D), Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D), Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D)};
}

