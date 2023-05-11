package vapourdrive.agricultural_enhancements.setup;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vapourdrive.agricultural_enhancements.modules.fertilizer.Fertilizer;
import vapourdrive.agricultural_enhancements.modules.fertilizer.FertilizerRecipe;
import vapourdrive.agricultural_enhancements.modules.fertilizer.producer.*;
import vapourdrive.agricultural_enhancements.modules.harvester.*;
import vapourdrive.agricultural_enhancements.modules.irrigation.IrrigationPipeBlock;
import vapourdrive.agricultural_enhancements.modules.irrigation.SprayerPipeBlock;
import vapourdrive.agricultural_enhancements.modules.irrigation.WateringCan;
import vapourdrive.agricultural_enhancements.modules.irrigation.irrigation_controller.*;
import vapourdrive.agricultural_enhancements.modules.manager.*;
import vapourdrive.agricultural_enhancements.modules.soil.TilledSoilBlock;

import static vapourdrive.agricultural_enhancements.AgriculturalEnhancements.MODID;

public class Registration {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        RECIPE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<HarvesterBlock> HARVESTER_BLOCK = BLOCKS.register("harvester", HarvesterBlock::new);
    public static final RegistryObject<FertilizerProducerBlock> FERTILIZER_PRODUCER_BLOCK = BLOCKS.register("fertilizer_producer", FertilizerProducerBlock::new);
    public static final RegistryObject<CropManagerBlock> CROP_MANAGER_BLOCK = BLOCKS.register("crop_manager", CropManagerBlock::new);
    public static final RegistryObject<TilledSoilBlock> TILLED_SOIL_BLOCK = BLOCKS.register("tilled_soil", TilledSoilBlock::new);
    public static final RegistryObject<IrrigationPipeBlock> IRRIGATION_PIPE_BLOCK = BLOCKS.register("irrigation_pipe", IrrigationPipeBlock::new);
    public static final RegistryObject<SprayerPipeBlock> SPRAYER_PIPE_BLOCK = BLOCKS.register("sprayer_pipe", SprayerPipeBlock::new);
    public static final RegistryObject<IrrigationControllerBlock> IRRIGATION_CONTROLLER_BLOCK = BLOCKS.register("irrigation_controller", IrrigationControllerBlock::new);
    public static final RegistryObject<Item> HARVESTER_ITEM = ITEMS.register("harvester", () -> new HarvesterItem(HARVESTER_BLOCK.get(), new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    public static final RegistryObject<Item> FERTILIZER_PRODUCER_ITEM = ITEMS.register("fertilizer_producer", () -> new FertilizerProducerItem(FERTILIZER_PRODUCER_BLOCK.get(), new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    public static final RegistryObject<Item> CROP_MANAGER_ITEM = ITEMS.register("crop_manager", () -> new CropManagerItem(CROP_MANAGER_BLOCK.get(), new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    public static final RegistryObject<Item> TILLED_SOIL_ITEM = ITEMS.register("tilled_soil", () -> new BlockItem(TILLED_SOIL_BLOCK.get(), new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    @SuppressWarnings("unused")
    public static final RegistryObject<WateringCan> WATERING_CAN = ITEMS.register("watering_can", () -> new WateringCan(new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    public static final RegistryObject<Fertilizer> FERTILISER = ITEMS.register("fertilizer", () -> new Fertilizer(new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> IRRIGATION_CONTROLLER_ITEM = ITEMS.register("irrigation_controller", () -> new IrrigationControllerItem(IRRIGATION_CONTROLLER_BLOCK.get(), new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> IRRIGATION_PIPE_ITEM = ITEMS.register("irrigation_pipe", () -> new BlockItem(IRRIGATION_PIPE_BLOCK.get(), new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> SPRAYER_PIPE_ITEM = ITEMS.register("sprayer_pipe", () -> new BlockItem(SPRAYER_PIPE_BLOCK.get(), new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    public static final RegistryObject<BlockEntityType<HarvesterTile>> HARVESTER_TILE = TILES.register("harvester", () -> BlockEntityType.Builder.of(HarvesterTile::new, HARVESTER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<FertilizerProducerTile>> FERTILIZER_PRODUCER_TILE = TILES.register("fertilizer_producer", () -> BlockEntityType.Builder.of(FertilizerProducerTile::new, FERTILIZER_PRODUCER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<CropManagerTile>> CROP_MANAGER_TILE = TILES.register("crop_manager", () -> BlockEntityType.Builder.of(CropManagerTile::new, CROP_MANAGER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<IrrigationControllerTile>> IRRIGATION_CONTROLLER_TILE = TILES.register("irrigation_controller", () -> BlockEntityType.Builder.of(IrrigationControllerTile::new, IRRIGATION_CONTROLLER_BLOCK.get()).build(null));

    public static final RegistryObject<MenuType<HarvesterContainer>> HARVESTER_CONTAINER = CONTAINERS.register("harvester", () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new HarvesterContainer(windowId, world, pos, inv, inv.player, new HarvesterData());
    }));

    public static final RegistryObject<MenuType<FertilizerProducerContainer>> FERTILIZER_PRODUCER_CONTAINER = CONTAINERS.register("fertilizer_producer", () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new FertilizerProducerContainer(windowId, world, pos, inv, inv.player, new FertilizerProducerData());
    }));
    public static final RegistryObject<MenuType<CropManagerContainer>> CROP_MANAGER_CONTAINER = CONTAINERS.register("crop_manager", () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new CropManagerContainer(windowId, world, pos, inv, inv.player, new CropManagerData());
    }));

    public static final RegistryObject<MenuType<IrrigationControllerContainer>> IRRIGATION_CONTROLLER_CONTAINER = CONTAINERS.register("irrigation_controller", () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new IrrigationControllerContainer(windowId, world, pos, inv, inv.player, new IrrigationControllerData());
    }));

//    public static final RegistryObject<ItemFurnaceCore> EXPERIENCE_CORE_ITEM = ITEMS.register("experience_core", () -> new ItemFurnaceCore("experience"));
//    public static final RegistryObject<ItemFurnaceCore> INSULATION_CORE_ITEM = ITEMS.register("insulation_core", () -> new ItemFurnaceCore("efficiency"));
//    public static final RegistryObject<ItemFurnaceCore> THERMAL_CORE_ITEM = ITEMS.register("thermal_core", () -> new ItemFurnaceCore("speed"));
//    public static final RegistryObject<ItemCrystal> CRYSTAL_GEM_ITEM = ITEMS.register("crystal_gem_item", ItemCrystal::new);

    public static final RegistryObject<RecipeSerializer<FertilizerRecipe>> FERTILIZER_SERIALIZER = RECIPE_SERIALIZERS.register("fertilizer", () -> FertilizerRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<FertilizerRecipe>> FERTILIZER_TYPE = RECIPE_TYPES.register("fertilizer", () -> FertilizerRecipe.Type.INSTANCE);

}
