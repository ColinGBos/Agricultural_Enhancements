package vapourdrive.agricultural_enhancements.content.fertilizer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.setup.Registration;

public class FertilizerRecipe implements Recipe<SingleRecipeInput> {
    protected final Ingredient ingredient;
    protected final int n;
    protected final int p;
    protected final int k;

    public FertilizerRecipe(Ingredient ingredient, int n, int p, int k) {
        this.ingredient = ingredient;
        this.n = n;
        this.p = p;
        this.k = k;
    }

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level pLevel) {
        return this.getIngredient().test(input.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SingleRecipeInput input, @NotNull HolderLookup.Provider access) {
        return getResultItem(access).copy();
    }

    public int[] getOutputs() {
        return new int[]{this.n, this.p, this.k};
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }


    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider access) {
        return new ItemStack(Registration.FERTILIZER.get());
    }

//    @Override
//    public @NotNull ResourceLocation getId() {
//        return id;
//    }

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
        @SuppressWarnings("unused")
        public static final String ID = "fertilizer";

        private Type() {
        }
    }

    public static class Serializer implements RecipeSerializer<FertilizerRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        @SuppressWarnings("unused")
//        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(AgriculturalEnhancements.MODID, "fertilizer");
        public static final StreamCodec<RegistryFriendlyByteBuf, FertilizerRecipe> STREAM_CODEC = StreamCodec.of(FertilizerRecipe.Serializer::toNetwork, FertilizerRecipe.Serializer::fromNetwork);

        private static final MapCodec<FertilizerRecipe> CODEC = RecordCodecBuilder.mapCodec((builder) -> {
            return builder.group(Ingredient.CODEC.fieldOf("ingredient").forGetter((fertilizerRecipe) -> {
                return fertilizerRecipe.ingredient;
            }), Codec.INT.fieldOf("n").forGetter((fertilizerRecipe) -> {
                return fertilizerRecipe.n;
            }), Codec.INT.fieldOf("p").forGetter((fertilizerRecipe) -> {
                return fertilizerRecipe.p;
            }), Codec.INT.fieldOf("k").forGetter((fertilizerRecipe) -> {
                return fertilizerRecipe.k;
            })).apply(builder, FertilizerRecipe::new);
        });

//        @Override
//        public @NotNull FertilizerRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
//            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
//            int n = GsonHelper.getAsInt(json, "n");
//            int p = GsonHelper.getAsInt(json, "p");
//            int k = GsonHelper.getAsInt(json, "k");
//
//            return new FertilizerRecipe(id, ingredient, n, p, k);
//        }

//        @Override
//        public @Nullable FertilizerRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf pBuffer) {
//            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
//            int[] results = pBuffer.readVarIntArray();
//            int n = results[0];
//            int p = results[1];
//            int k = results[2];
//            return new FertilizerRecipe(id, ingredient, n, p, k);
//        }
//
//        @Override
//        public void toNetwork(@NotNull FriendlyByteBuf pBuffer, FertilizerRecipe pRecipe) {
//            pRecipe.getIngredient().toNetwork(pBuffer);
//            pBuffer.writeVarIntArray(new int[]{pRecipe.n, pRecipe.p, pRecipe.k});
//        }

        private static FertilizerRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            int[] results = buffer.readVarIntArray();
            int n = results[0];
            int p = results[1];
            int k = results[2];
            return new FertilizerRecipe(ingredient, n, p, k);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, FertilizerRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
            buffer.writeVarIntArray(new int[]{recipe.n, recipe.p, recipe.k});
        }

        @Override
        public @NotNull MapCodec<FertilizerRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, FertilizerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
