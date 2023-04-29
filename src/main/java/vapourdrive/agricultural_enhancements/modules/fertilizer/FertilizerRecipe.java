package vapourdrive.agricultural_enhancements.modules.fertilizer;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;

public class FertilizerRecipe implements Recipe<SimpleContainer> {
    protected final ResourceLocation id;
    protected final Ingredient ingredient;
    protected final int time;
    protected final int n;
    protected final int p;
    protected final int k;

    public FertilizerRecipe(ResourceLocation id, Ingredient ingredient, int time, int n, int p, int k){
        this.id=id;
        this.ingredient=ingredient;
        this.time=time;
        this.n=n;
        this.p=p;
        this.k=k;
    }

    @Override
    public boolean matches(@NotNull SimpleContainer pContainer, @NotNull Level pLevel) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer pContainer) {
        return getResultItem().copy();
    }

    public int[] getOutputs() {
        return new int[]{this.n, this.p, this.k};
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return ingredient.getItems()[0].hasCraftingRemainingItem() ? ingredient.getItems()[0].getCraftingRemainingItem(): new ItemStack(Items.AIR);
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
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "fertilizer";
    }

    public static class Serializer implements RecipeSerializer<FertilizerRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(AgriculturalEnhancements.MODID, "fertilizer");

        @Override
        public @NotNull FertilizerRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            Ingredient ingredient;
            if (GsonHelper.isArrayNode(json, "ingredient")) {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "ingredient"));
            } else {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            }
            int time = GsonHelper.getAsInt(json, "time");
            int n = GsonHelper.getAsInt(json, "n");
            int p = GsonHelper.getAsInt(json, "p");
            int k = GsonHelper.getAsInt(json, "k");
            return new FertilizerRecipe(id, ingredient, time, n, p, k);
        }

        @Override
        public @Nullable FertilizerRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf pBuffer) {
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            int[] results = pBuffer.readVarIntArray();
            int time = results[0];
            int n = results[1];
            int p = results[2];
            int k = results[3];
            return new FertilizerRecipe(id, ingredient, time, n, p, k);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf pBuffer, FertilizerRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);
            pBuffer.writeVarIntArray(new int[]{pRecipe.time, pRecipe.n, pRecipe.p, pRecipe.k});
        }
    }
}
