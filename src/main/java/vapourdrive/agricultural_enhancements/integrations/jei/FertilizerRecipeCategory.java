package vapourdrive.agricultural_enhancements.integrations.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.content.fertilizer.FertilizerRecipe;
import vapourdrive.agricultural_enhancements.content.fertilizer.producer.FertilizerProducerData;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static vapourdrive.vapourware.shared.base.AbstractBaseMachineScreen.isInRect;

public class FertilizerRecipeCategory implements IRecipeCategory<RecipeHolder<FertilizerRecipe>> {

    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(AgriculturalEnhancements.MODID, "fertilizer_producer");
    public final static ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AgriculturalEnhancements.MODID, "textures/gui/fertilizer_producer_jei.png");

    private final IDrawable background;
    private final IDrawable icon;

    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;
    private final LoadingCache<Integer, IDrawableAnimated> cachedFuel;
    private final IDrawableStatic n;
    private final IDrawableStatic p;
    private final IDrawableStatic k;
    protected final DecimalFormat df = new DecimalFormat("#,###");


    public FertilizerRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 136, 52);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Registration.FERTILIZER_PRODUCER_ITEM.get()));

        this.cachedArrows = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public @NotNull IDrawableAnimated load(@NotNull Integer time) {
                        return guiHelper.drawableBuilder(TEXTURE, 179, 0, 24, 17)
                                .buildAnimated(time, IDrawableAnimated.StartDirection.LEFT, false);
                    }
                });

        this.cachedFuel = CacheBuilder.newBuilder()
                .maximumSize(45)
                .build(new CacheLoader<>() {
                    @Override
                    public @NotNull IDrawableAnimated load(@NotNull Integer time) {
                        return guiHelper.drawableBuilder(TEXTURE, 153, 0, 8, 45)
                                .buildAnimated(time, IDrawableAnimated.StartDirection.TOP, true);
                    }
                });
        this.n = guiHelper.createDrawable(TEXTURE, 161, 0, 6, 45);
        this.p = guiHelper.createDrawable(TEXTURE, 167, 0, 6, 45);
        this.k = guiHelper.createDrawable(TEXTURE, 173, 0, 6, 45);
    }

    protected IDrawableAnimated getArrow() {
        int fertTime = 40;
        return this.cachedArrows.getUnchecked(fertTime);
    }

    protected IDrawableAnimated getFuel() {
        int breakTime = 1600;
        return this.cachedFuel.getUnchecked(breakTime);
    }

//    @Override
//    public @NotNull RecipeType<FertilizerRecipe> getRecipeType() {
//        return JEI_Plugin.FERTILIZER_TYPE;
//    }

    @Override
    public @NotNull RecipeType<RecipeHolder<FertilizerRecipe>> getRecipeType() {
        return JEI_Plugin.FERTILIZER_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("agriculturalenhancements.fertilizer_producer");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FertilizerRecipe> recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 18, 33).addIngredients(recipe.value().getIngredient());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 99, 10).addItemStack(new ItemStack(Registration.FERTILIZER.get()));
    }

//    @Override
//    public void setRecipe(IRecipeLayoutBuilder builder, FertilizerRecipe recipe, @NotNull IFocusGroup focuses) {
//        builder.addSlot(RecipeIngredientRole.INPUT, 18, 33).addIngredients(recipe.getIngredient());
//        builder.addSlot(RecipeIngredientRole.OUTPUT, 99, 10).addItemStack(new ItemStack(Registration.FERTILIZER.get()));
//    }

    @Override
    public void draw(@NotNull RecipeHolder<FertilizerRecipe> recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IDrawableAnimated arrow = getArrow();
        arrow.draw(guiGraphics, 71, 19);
        IDrawableAnimated fuel = getFuel();
        fuel.draw(guiGraphics, 3, 4);

        int[] outputs = recipe.value().getOutputs();

        n.draw(guiGraphics, 41, 4, Math.max(0, 45 - outputs[0] / 100), 0, 0, 0);
        p.draw(guiGraphics, 51, 4, Math.max(0, 45 - outputs[1] / 100), 0, 0, 0);
        k.draw(guiGraphics, 61, 4, Math.max(0, 45 - outputs[2] / 100), 0, 0, 0);
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(@NotNull RecipeHolder<FertilizerRecipe> recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> hoveringText = new ArrayList<>();
        FertilizerProducerData.Data[] elements = {FertilizerProducerData.Data.N, FertilizerProducerData.Data.P, FertilizerProducerData.Data.K};
        int i = 0;
        int[] elementValues = recipe.value().getOutputs();
        for (FertilizerProducerData.Data element : elements) {
            if (isInRect(40 + (10 * i), 2, 8, 48, (int) mouseX, (int) mouseY)) {
                hoveringText.add(Component.literal(element.name() + ": ").append(df.format(elementValues[i])));
            }
            i++;
        }

        if (isInRect(71, 19, 23, 17, (int) mouseX, (int) mouseY)) {
            hoveringText.add(Component.translatable("agriculturalenhancements.fertilizer_producer.info_2", ConfigSettings.FERTILIZER_PRODUCER_NUTRIENTS_PER_FERTILIZER.get()));
        }

        return hoveringText;
    }

}
