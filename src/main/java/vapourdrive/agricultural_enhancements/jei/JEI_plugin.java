package vapourdrive.agricultural_enhancements.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.modules.harvester.HarvesterScreen;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JeiPlugin
public class JEI_plugin implements IModPlugin {

//    private IRecipeCategory<SmeltingRecipe> furnaceCategory;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(AgriculturalEnhancements.MODID, "jei_plugin");
    }


    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        AgriculturalEnhancements.debugLog("Adding Recipe Click Area");
//        registration.addRecipeClickArea(HarvesterScreen.class, 154, 6, 16, 16, RecipeTypes.FUELING);
//        registration.addGuiContainerHandler(HarvesterScreen.class, new JEIGuiClickableArea(154, 6, 16, 16));
        registration.addGuiContainerHandler(HarvesterScreen.class, new IGuiContainerHandler<>() {
            @Override
            public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull HarvesterScreen containerScreen, double mouseX, double mouseY) {
                ArrayList<Component> list = new ArrayList<>();
                list.add(Component.translatable("agriculturalenhancements.harvester.jei_info"));
                list.add(Component.translatable("jei.tooltip.show.recipes").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.LIGHT_PURPLE));
                IGuiClickableArea clickableArea = new JEIGuiClickableArea(157, 5, 14, 14, list, RecipeTypes.FUELING);
                return List.of(clickableArea);
            }
        });
    }

//    @Override
//    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
//        FurnaceMk2.debugLog("Register Recipe Transfer Handler");
//        registration.addRecipeTransferHandler(FurnaceMk2Container.class, RecipeTypes.FUELING, 39, 1, 0, 45);
//        registration.addRecipeTransferHandler(FurnaceMk2Container.class, RecipeTypes.SMELTING, 40, 1, 0, 45);
//    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        AgriculturalEnhancements.debugLog("Register recipe catalyst");
        registration.addRecipeCatalyst(new ItemStack(Registration.HARVESTER_BLOCK.get()), RecipeTypes.FUELING);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(new ItemStack(Registration.HARVESTER_ITEM.get()), VanillaTypes.ITEM_STACK, Component.translatable("agriculturalenhancements.harvester.jei_info"));
    }
}
