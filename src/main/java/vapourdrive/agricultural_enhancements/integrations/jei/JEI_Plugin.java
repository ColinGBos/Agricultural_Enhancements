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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.modules.fertilizer.FertilizerRecipe;
import vapourdrive.agricultural_enhancements.modules.fertilizer.producer.FertilizerProducerScreen;
import vapourdrive.agricultural_enhancements.modules.harvester.HarvesterScreen;
import vapourdrive.agricultural_enhancements.modules.irrigation.irrigation_controller.IrrigationControllerScreen;
import vapourdrive.agricultural_enhancements.modules.manager.CropManagerScreen;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEI_Plugin implements IModPlugin {

    public static RecipeType<FertilizerRecipe> FERTILIZER_TYPE =
            new RecipeType<>(FertilizerRecipeCategory.UID, FertilizerRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(AgriculturalEnhancements.MODID, "jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(HarvesterScreen.class, 157, 20, 15, 15, RecipeTypes.FUELING);
        registration.addRecipeClickArea(IrrigationControllerScreen.class, 125, 20, 15, 15, RecipeTypes.FUELING);
        registration.addRecipeClickArea(FertilizerProducerScreen.class, 142, 5, 15, 15, RecipeTypes.FUELING, FERTILIZER_TYPE);
        registration.addRecipeClickArea(CropManagerScreen.class, 142, 5, 15, 15, RecipeTypes.FUELING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
//        AgriculturalEnhancements.debugLog("Register recipe catalyst");
        registration.addRecipeCatalyst(new ItemStack(Registration.HARVESTER_BLOCK.get()), RecipeTypes.FUELING);
        registration.addRecipeCatalyst(new ItemStack(Registration.IRRIGATION_CONTROLLER_BLOCK.get()), RecipeTypes.FUELING);
        registration.addRecipeCatalyst(new ItemStack(Registration.FERTILIZER_PRODUCER_BLOCK.get()), RecipeTypes.FUELING, FERTILIZER_TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(new ItemStack(Registration.HARVESTER_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.harvester.info"));
        registration.addIngredientInfo(new ItemStack(Registration.IRRIGATION_CONTROLLER_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.irrigation_controller.info"));
        registration.addIngredientInfo(new ItemStack(Registration.FERTILIZER_PRODUCER_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.fertilizer_producer.info"));
        registration.addIngredientInfo(new ItemStack(Registration.CROP_MANAGER_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.crop_manager.info"));

        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        List<FertilizerRecipe> recipeList = recipeManager.getAllRecipesFor(FertilizerRecipe.Type.INSTANCE);
        registration.addRecipes(FERTILIZER_TYPE, recipeList);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FertilizerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }
}
