package vapourdrive.agricultural_enhancements.data.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.setup.ModTags;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.concurrent.CompletableFuture;

import static vapourdrive.vapourware.shared.utils.RegistryUtils.getIngredientFromTag;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Registration.DUSKBLOOM_SHARD_BLOCK_ITEM.get())
                .pattern("HHH").pattern("HHH").pattern("HHH")
                .define('H', Ingredient.of(ModTags.Items.GEM_DUSKBLOOM_SHARD))
                .unlockedBy("has_duskbloom_shard", has(ModTags.Items.GEM_DUSKBLOOM_SHARD))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Registration.DUSKBLOOM_GLOB_BLOCK_ITEM.get())
                .pattern("HHH").pattern("HHH").pattern("HHH")
                .define('H', getIngredientFromTag("c", "gems/duskbloom_glob"))
                .unlockedBy("has_duskbloom_glob", has(ModTags.Items.GEM_DUSKBLOOM_GLOB))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Registration.DUSKBLOOM_GLOB.get(), 9)
                .requires(Registration.DUSKBLOOM_GLOB_BLOCK_ITEM.get())
                .unlockedBy("has_duskbloom_glob", has(ModTags.Items.GEM_DUSKBLOOM_GLOB))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Registration.DUSKBLOOM_SHARD.get(), 9)
                .requires(Registration.DUSKBLOOM_SHARD_BLOCK_ITEM.get())
                .unlockedBy("has_duskbloom_shard", has(ModTags.Items.GEM_DUSKBLOOM_SHARD))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.DUSKBLOOM_PICKAXE.get())
                .pattern("HHH").pattern(" R ").pattern(" R ")
                .define('H', getIngredientFromTag("c", "gems/duskbloom_shard"))
                .define('R', getIngredientFromTag("c", "rods/wooden"))
                .unlockedBy("has_duskbloom_shard", has(ModTags.Items.GEM_DUSKBLOOM_SHARD))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.DUSKBLOOM_AXE.get())
                .pattern("HH ").pattern("HR ").pattern(" R ")
                .define('H', getIngredientFromTag("c", "gems/duskbloom_shard"))
                .define('R', getIngredientFromTag("c", "rods/wooden"))
                .unlockedBy("has_duskbloom_shard", has(ModTags.Items.GEM_DUSKBLOOM_SHARD))
                .save(output);

//        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.DUSKBLOOM_AXE.get())
//                .pattern(" HH").pattern(" RH").pattern(" R ")
//                .define('H', Registration.DUSKBLOOM_SHARD.get())
//                .define('R', getIngredientFromTag("c", "rods/wooden"))
//                .unlockedBy("has_duskbloom_shard", has(Registration.DUSKBLOOM_SHARD.get()))
//                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.DUSKBLOOM_SWORD.get())
                .pattern(" H ").pattern(" H ").pattern(" R ")
                .define('H', getIngredientFromTag("c", "gems/duskbloom_shard"))
                .define('R', getIngredientFromTag("c", "rods/wooden"))
                .unlockedBy("has_duskbloom_shard", has(ModTags.Items.GEM_DUSKBLOOM_SHARD))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.DUSKBLOOM_SHOVEL.get())
                .pattern(" H ").pattern(" H ").pattern(" R ")
                .define('H', getIngredientFromTag("c", "gems/duskbloom_shard"))
                .define('R', getIngredientFromTag("c", "rods/wooden"))
                .unlockedBy("has_duskbloom_shard", has(ModTags.Items.GEM_DUSKBLOOM_SHARD))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.DUSKBLOOM_HOE.get())
                .pattern("HH ").pattern(" R ").pattern(" R ")
                .define('H', getIngredientFromTag("c", "gems/duskbloom_shard"))
                .define('R', getIngredientFromTag("c", "rods/wooden"))
                .unlockedBy("has_duskbloom_shard", has(ModTags.Items.GEM_DUSKBLOOM_SHARD))
                .save(output);

    }
}
