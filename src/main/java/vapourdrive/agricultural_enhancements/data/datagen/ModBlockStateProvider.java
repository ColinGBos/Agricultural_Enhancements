package vapourdrive.agricultural_enhancements.data.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.setup.Registration;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AgriculturalEnhancements.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleCube(Registration.DUSKBLOOM_GLOB_BLOCK.get());
        simpleCube(Registration.DUSKBLOOM_SHARD_BLOCK.get());

    }

    protected void simpleCube(Block block){
        simpleBlockWithItem(block, cubeAll(block));
    }

}
