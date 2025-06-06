package vapourdrive.agricultural_enhancements.setup;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.vapourware.shared.utils.RegistryUtils;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEEDS_DUSKBLOOM_TOOL = RegistryUtils.getBlockTag(AgriculturalEnhancements.MODID, "needs_duskbloom_tool");
        public static final TagKey<Block> INCORRECT_FOR_DUSKBLOOM_TOOL = RegistryUtils.getBlockTag(AgriculturalEnhancements.MODID,"incorrect_for_duskbloom_tool");

        public static final TagKey<Block> STORAGE_BLOCKS_DUSKBLOOM_GLOB = RegistryUtils.getBlockTag("c","storage_blocks/duskbloom_glob");
        public static final TagKey<Block> STORAGE_BLOCKS_DUSKBLOOM_SHARD = RegistryUtils.getBlockTag("c","storage_blocks/duskbloom_shard");
    }

    public static class Items {
        public static final TagKey<Item> STORAGE_BLOCKS_DUSKBLOOM_GLOB = RegistryUtils.getItemTag("c","storage_blocks/duskbloom_glob");
        public static final TagKey<Item> STORAGE_BLOCKS_DUSKBLOOM_SHARD = RegistryUtils.getItemTag("c","storage_blocks/duskbloom_shard");
        public static final TagKey<Item> GEM_DUSKBLOOM_SHARD = RegistryUtils.getItemTag("c","gems/duskbloom_shard");
        public static final TagKey<Item> GEM_DUSKBLOOM_GLOB = RegistryUtils.getItemTag("c","gems/duskbloom_glob");

    }


}
