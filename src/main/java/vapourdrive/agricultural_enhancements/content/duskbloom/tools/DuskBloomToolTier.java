package vapourdrive.agricultural_enhancements.content.duskbloom.tools;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import vapourdrive.agricultural_enhancements.setup.ModTags;
import vapourdrive.agricultural_enhancements.setup.Registration;

public class DuskBloomToolTier {
    public static final Tier DUSKBLOOM = new SimpleTier(ModTags.Blocks.INCORRECT_FOR_DUSKBLOOM_TOOL, 475, 6.5F, 2.5F, 18,
            () -> Ingredient.of(Registration.DUSKBLOOM_SHARD.get()));
}
