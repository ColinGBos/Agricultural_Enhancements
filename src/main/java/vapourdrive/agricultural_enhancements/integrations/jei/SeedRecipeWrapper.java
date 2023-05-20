package vapourdrive.agricultural_enhancements.integrations.jei;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class SeedRecipeWrapper {

    private final MutableComponent blockName;
    private final Ingredient seed;

    public SeedRecipeWrapper(ItemLike seed, MutableComponent blockName) {
        this.blockName = blockName;
        this.seed = Ingredient.of(seed);
    }

    public MutableComponent getBlockName() {
        return blockName;
    }

    public Ingredient getSeedIngredient() {
        return seed;
    }
}
