package vapourdrive.agricultural_enhancements.mixin;

import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BeetrootBlock.class)
public class MixinBeetrootBlock {
    @Final
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D),
            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D),
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D),
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 10.0D, 15.0D)
    };
}
