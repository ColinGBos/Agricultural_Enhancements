package vapourdrive.agricultural_enhancements.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.modules.harvester.HarvesterScreen;
import vapourdrive.agricultural_enhancements.modules.irrigation.irrigation_controller.IrrigationControllerScreen;
import vapourdrive.agricultural_enhancements.setup.Registration;

@JeiPlugin
public class JEI_plugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(AgriculturalEnhancements.MODID, "jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(HarvesterScreen.class, 157, 20, 15, 15,RecipeTypes.FUELING);
        registration.addRecipeClickArea(IrrigationControllerScreen.class, 125, 20, 15, 15,RecipeTypes.FUELING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
//        AgriculturalEnhancements.debugLog("Register recipe catalyst");
        registration.addRecipeCatalyst(new ItemStack(Registration.HARVESTER_BLOCK.get()), RecipeTypes.FUELING);
        registration.addRecipeCatalyst(new ItemStack(Registration.IRRIGATION_CONTROLLER_BLOCK.get()), RecipeTypes.FUELING);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(new ItemStack(Registration.HARVESTER_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.harvester.info"));
        registration.addIngredientInfo(new ItemStack(Registration.IRRIGATION_CONTROLLER_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.irrigation_controller.info"));
    }
}
