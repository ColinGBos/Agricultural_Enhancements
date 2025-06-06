package vapourdrive.agricultural_enhancements.data.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.setup.ModTags;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AgriculturalEnhancements.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(ModTags.Blocks.NEEDS_DUSKBLOOM_TOOL).addTags(BlockTags.NEEDS_IRON_TOOL);
        tag(ModTags.Blocks.INCORRECT_FOR_DUSKBLOOM_TOOL).addTags(BlockTags.INCORRECT_FOR_IRON_TOOL).remove(ModTags.Blocks.NEEDS_DUSKBLOOM_TOOL);
        this.registerCommonTags();
        this.registerMinecraftTags();


    }

    private void registerMinecraftTags() {
        tag(BlockTags.BEACON_BASE_BLOCKS).add(Registration.DUSKBLOOM_SHARD_BLOCK.get());
    }

    private void registerCommonTags() {
        tag(ModTags.Blocks.STORAGE_BLOCKS_DUSKBLOOM_GLOB).add(Registration.DUSKBLOOM_GLOB_BLOCK.get());
        tag(ModTags.Blocks.STORAGE_BLOCKS_DUSKBLOOM_SHARD).add(Registration.DUSKBLOOM_SHARD_BLOCK.get());
    }

}
