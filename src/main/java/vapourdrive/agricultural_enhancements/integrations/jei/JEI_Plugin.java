package vapourdrive.agricultural_enhancements.integrations.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.content.fertilizer.FertilizerRecipe;
import vapourdrive.agricultural_enhancements.content.fertilizer.producer.FertilizerProducerScreen;
import vapourdrive.agricultural_enhancements.content.harvester.HarvesterScreen;
import vapourdrive.agricultural_enhancements.content.irrigation.irrigation_controller.IrrigationControllerScreen;
import vapourdrive.agricultural_enhancements.content.manager.CropManagerScreen;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEI_Plugin implements IModPlugin {

    public static final RecipeType<FertilizerRecipe> FERTILIZER_TYPE = new RecipeType<>(FertilizerRecipeCategory.UID, FertilizerRecipe.class);
    public static final RecipeType<SeedRecipeWrapper> SEEDS = RecipeType.create(AgriculturalEnhancements.MODID, "crop_manager", SeedRecipeWrapper.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(AgriculturalEnhancements.MODID, "jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(HarvesterScreen.class, 157, 20, 15, 15, RecipeTypes.FUELING);
        registration.addRecipeClickArea(IrrigationControllerScreen.class, 125, 20, 15, 15, RecipeTypes.FUELING);
        registration.addRecipeClickArea(FertilizerProducerScreen.class, 142, 5, 15, 15, RecipeTypes.FUELING, FERTILIZER_TYPE);
        registration.addRecipeClickArea(CropManagerScreen.class, 142, 5, 15, 15, RecipeTypes.FUELING, SEEDS);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
//        AgriculturalEnhancements.debugLog("Register recipe catalyst");
        registration.addRecipeCatalyst(new ItemStack(Registration.HARVESTER_BLOCK.get()), RecipeTypes.FUELING);
        registration.addRecipeCatalyst(new ItemStack(Registration.IRRIGATION_CONTROLLER_BLOCK.get()), RecipeTypes.FUELING);
        registration.addRecipeCatalyst(new ItemStack(Registration.FERTILIZER_PRODUCER_BLOCK.get()), RecipeTypes.FUELING, FERTILIZER_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Registration.CROP_MANAGER_BLOCK.get()), RecipeTypes.FUELING, SEEDS);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(new ItemStack(Registration.HARVESTER_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.harvester.info"));
        registration.addIngredientInfo(new ItemStack(Registration.IRRIGATION_CONTROLLER_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.irrigation_controller.info"));
        registration.addIngredientInfo(new ItemStack(Registration.FERTILIZER_PRODUCER_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.fertilizer_producer.info", ConfigSettings.FERTILIZER_PRODUCER_NUTRIENTS_PER_FERTILIZER.get()));
        registration.addIngredientInfo(new ItemStack(Registration.CROP_MANAGER_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.crop_manager.info"));
        registration.addIngredientInfo(new ItemStack(Registration.SOIL_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.soil.info"));
        if (ConfigSettings.SOIL_REQUIRES_FERTILIZER.get()) {
            registration.addIngredientInfo(new ItemStack(Registration.TILLED_SOIL_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.tilled_soil.info"), Component.translatable("agriculturalenhancements.tilled_soil.fertilizer_true.info"));
        } else {
            registration.addIngredientInfo(new ItemStack(Registration.TILLED_SOIL_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.tilled_soil.info"), Component.translatable("agriculturalenhancements.tilled_soil.fertilizer_false.info"));
        }
        registration.addIngredientInfo(new ItemStack(Registration.IRRIGATION_PIPE_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.irrigation_pipe.info"));
        registration.addIngredientInfo(new ItemStack(Registration.SPRAYER_PIPE_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.sprayer_pipe.info", ConfigSettings.SPRAYER_VERTICAL_RANGE.get()));
        registration.addIngredientInfo(new ItemStack(Registration.WATERING_CAN.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.watering_can.info"));

        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<FertilizerRecipe> recipeList = recipeManager.getAllRecipesFor(FertilizerRecipe.Type.INSTANCE);
        registration.addRecipes(FERTILIZER_TYPE, recipeList);
        registration.addRecipes(SEEDS, getSeedRecipes());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FertilizerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SeedRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    private List<SeedRecipeWrapper> getSeedRecipes() {
        List<SeedRecipeWrapper> seedList = new ArrayList<>();
        for (ItemLike seed : AgriculturalEnhancements.seeds) {
            if (seed.asItem() instanceof BlockItem seedBlockItem) {
                seedList.add(new SeedRecipeWrapper(seed, seedBlockItem.getBlock().getName()));
            }
        }
        return seedList;
    }

}
