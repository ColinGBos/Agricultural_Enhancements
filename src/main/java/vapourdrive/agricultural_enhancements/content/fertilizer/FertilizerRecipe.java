package vapourdrive.agricultural_enhancements.content.fertilizer;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.Arrays;

public class FertilizerRecipe implements Recipe<SimpleContainer> {
    protected final ResourceLocation id;
    protected final Lazy<Ingredient> ingredient;
    protected final int n;
    protected final int p;
    protected final int k;

    public FertilizerRecipe(ResourceLocation id, Lazy<Ingredient> ingredient, int n, int p, int k) {
        this.id = id;
        this.ingredient = ingredient;
        this.n = n;
        this.p = p;
        this.k = k;
    }

    @Override
    public boolean matches(@NotNull SimpleContainer pContainer, @NotNull Level pLevel) {
        AgriculturalEnhancements.debugLog("Checking container " + (pContainer.getItem(0)));
        AgriculturalEnhancements.debugLog("Checking ingredient " + Arrays.toString(getIngredient().getItems()));

        boolean ret = this.getIngredient().test(pContainer.getItem(0));
        AgriculturalEnhancements.debugLog("Matches " + ret);
        return ret;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer pContainer) {
        return getResultItem().copy();
    }

    public int[] getOutputs() {
        return new int[]{this.n, this.p, this.k};
    }

    public Ingredient getIngredient() {
        return ingredient.get();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return new ItemStack(Registration.FERTILISER.get());
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<FertilizerRecipe> {
        public static final Type INSTANCE = new Type();
        //public static final String ID = "fertilizer";

        private Type() {
        }
    }

    public static class Serializer implements RecipeSerializer<FertilizerRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        //public static final ResourceLocation ID = new ResourceLocation(AgriculturalEnhancements.MODID, "fertilizer");

        @Override
        public @NotNull FertilizerRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            Lazy<Ingredient> ingredient = Lazy.of(() -> Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient")));
            int n = GsonHelper.getAsInt(json, "n");
            int p = GsonHelper.getAsInt(json, "p");
            int k = GsonHelper.getAsInt(json, "k");

            return new FertilizerRecipe(id, ingredient, n, p, k);
        }

        @Override
        public @Nullable FertilizerRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf pBuffer) {
            Lazy<Ingredient> ingredient = Lazy.of(() -> Ingredient.fromNetwork(pBuffer));
            int[] results = pBuffer.readVarIntArray();
            int n = results[0];
            int p = results[1];
            int k = results[2];
            return new FertilizerRecipe(id, ingredient, n, p, k);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf pBuffer, FertilizerRecipe pRecipe) {
            pRecipe.getIngredient().toNetwork(pBuffer);
            pBuffer.writeVarIntArray(new int[]{pRecipe.n, pRecipe.p, pRecipe.k});
        }
    }
}
