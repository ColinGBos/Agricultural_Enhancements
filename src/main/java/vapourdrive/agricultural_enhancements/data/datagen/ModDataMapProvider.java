package vapourdrive.agricultural_enhancements.data.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {
    protected ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(Registration.DUSKBLOOM_GLOB.get().builtInRegistryHolder(), new FurnaceFuel(400), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(Registration.DUSKBLOOM_GLOB_BLOCK_ITEM.get().builtInRegistryHolder(), new FurnaceFuel(400*9), false);
    }
}
