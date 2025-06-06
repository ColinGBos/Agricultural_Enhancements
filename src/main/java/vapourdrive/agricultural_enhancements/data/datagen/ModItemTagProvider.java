package vapourdrive.agricultural_enhancements.data.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.setup.ModTags;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {


    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.registerMinecraftTags();
        this.registerCommonTags();
        this.registerNeoForgeTags();
    }

    private void registerNeoForgeTags() {
        tag(Tags.Items.FOODS).add(Registration.DUSKBLOOM_GLOB.get());
        tag(Tags.Items.FOODS_VEGETABLE).add(Registration.DUSKBLOOM_GLOB.get());
        tag(Tags.Items.SEEDS).add(Registration.DUSKBLOOM_SEEDS.get());
    }


    private void registerMinecraftTags() {
        tag(net.minecraft.tags.ItemTags.VILLAGER_PLANTABLE_SEEDS).add(Registration.DUSKBLOOM_SEEDS.get());

    }

    private void registerCommonTags() {
        tag(ModTags.Items.GEM_DUSKBLOOM_GLOB).add(Registration.DUSKBLOOM_GLOB.get());
        tag(ModTags.Items.GEM_DUSKBLOOM_SHARD).add(Registration.DUSKBLOOM_SHARD.get());
        tag(ModTags.Items.STORAGE_BLOCKS_DUSKBLOOM_GLOB).add(Registration.DUSKBLOOM_GLOB_BLOCK_ITEM.get());
        tag(ModTags.Items.STORAGE_BLOCKS_DUSKBLOOM_SHARD).add(Registration.DUSKBLOOM_SHARD_BLOCK_ITEM.get());
    }

}
