package vapourdrive.agricultural_enhancements.data.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
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
        tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(Registration.DUSKBLOOM_BOOTS.get());
        tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(Registration.DUSKBLOOM_LEGGINGS.get());
        tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(Registration.DUSKBLOOM_CHESTPLATE.get());
        tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(Registration.DUSKBLOOM_HELMET.get());
        tag(ItemTags.PICKAXES).add(Registration.DUSKBLOOM_PICKAXE.get());
        tag(ItemTags.AXES).add(Registration.DUSKBLOOM_AXE.get());
        tag(ItemTags.SHOVELS).add(Registration.DUSKBLOOM_SHOVEL.get());
        tag(ItemTags.HOES).add(Registration.DUSKBLOOM_HOE.get());
        tag(ItemTags.SWORDS).add(Registration.DUSKBLOOM_SWORD.get());

    }

    private void registerCommonTags() {
        tag(ModTags.Items.GEM_DUSKBLOOM_GLOB).add(Registration.DUSKBLOOM_GLOB.get());
        tag(ModTags.Items.GEM_DUSKBLOOM_SHARD).add(Registration.DUSKBLOOM_SHARD.get());
        tag(ModTags.Items.STORAGE_BLOCKS_DUSKBLOOM_GLOB).add(Registration.DUSKBLOOM_GLOB_BLOCK_ITEM.get());
        tag(ModTags.Items.STORAGE_BLOCKS_DUSKBLOOM_SHARD).add(Registration.DUSKBLOOM_SHARD_BLOCK_ITEM.get());
    }

}
