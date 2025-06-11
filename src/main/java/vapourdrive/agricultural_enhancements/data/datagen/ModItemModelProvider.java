package vapourdrive.agricultural_enhancements.data.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.setup.Registration;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AgriculturalEnhancements.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        itemToolModel(Registration.DUSKBLOOM_PICKAXE.get());
        itemToolModel(Registration.DUSKBLOOM_AXE.get());
        itemToolModel(Registration.DUSKBLOOM_SWORD.get());
        itemToolModel(Registration.DUSKBLOOM_SHOVEL.get());
        itemToolModel(Registration.DUSKBLOOM_HOE.get());
        itemModelBase(Registration.DUSKBLOOM_HELMET.get());
        itemModelBase(Registration.DUSKBLOOM_CHESTPLATE.get());
        itemModelBase(Registration.DUSKBLOOM_LEGGINGS.get());
        itemModelBase(Registration.DUSKBLOOM_BOOTS.get());

    }

    public void itemToolModel(Item item) {
        String name = itemName(item);
        withExistingParent(name, ResourceLocation.withDefaultNamespace("item/handheld")).texture("layer0", "item/"+name);
    }

    public void itemModelBase(Item item) {
        String name = itemName(item);
        withExistingParent(name, ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0", "item/"+name);
    }

    private String itemName(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }
}
