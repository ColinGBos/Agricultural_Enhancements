package vapourdrive.agricultural_enhancements.setup;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vapourdrive.agricultural_enhancements.modules.harvester.*;
import vapourdrive.agricultural_enhancements.modules.irrigation.IrrigationPipeBlock;
import vapourdrive.agricultural_enhancements.modules.irrigation.SprayerPipeBlock;
import vapourdrive.agricultural_enhancements.modules.irrigation.WateringCan;
import vapourdrive.agricultural_enhancements.modules.irrigation.irrigation_controller.*;
import vapourdrive.agricultural_enhancements.modules.soil.TilledSoilBlock;

import static vapourdrive.agricultural_enhancements.AgriculturalEnhancements.MODID;

public class Registration {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<HarvesterBlock> HARVESTER_BLOCK = BLOCKS.register("harvester", HarvesterBlock::new);
    public static final RegistryObject<TilledSoilBlock> TILLED_SOIL_BLOCK = BLOCKS.register("tilled_soil", TilledSoilBlock::new);
    public static final RegistryObject<IrrigationPipeBlock> IRRIGATION_PIPE_BLOCK = BLOCKS.register("irrigation_pipe", IrrigationPipeBlock::new);
    public static final RegistryObject<SprayerPipeBlock> SPRAYER_PIPE_BLOCK = BLOCKS.register("sprayer_pipe", SprayerPipeBlock::new);
    public static final RegistryObject<IrrigationControllerBlock> IRRIGATION_CONTROLLER_BLOCK = BLOCKS.register("irrigation_controller", IrrigationControllerBlock::new);
    public static final RegistryObject<Item> HARVESTER_ITEM = ITEMS.register("harvester", () -> new HarvesterItem(HARVESTER_BLOCK.get(), new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    public static final RegistryObject<Item> TILLED_SOIL_ITEM = ITEMS.register("tilled_soil", () -> new BlockItem(TILLED_SOIL_BLOCK.get(), new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    @SuppressWarnings("unused")
    public static final RegistryObject<WateringCan> WATERING_CAN = ITEMS.register("watering_can", () -> new WateringCan(new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> IRRIGATION_CONTROLLER_ITEM = ITEMS.register("irrigation_controller", () -> new IrrigationControllerItem(IRRIGATION_CONTROLLER_BLOCK.get(), new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> IRRIGATION_PIPE_ITEM = ITEMS.register("irrigation_pipe", () -> new BlockItem(IRRIGATION_PIPE_BLOCK.get(), new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    @SuppressWarnings("unused")
    public static final RegistryObject<Item> SPRAYER_PIPE_ITEM = ITEMS.register("sprayer_pipe", () -> new BlockItem(SPRAYER_PIPE_BLOCK.get(), new Item.Properties().tab(ModSetup.ITEM_GROUP)));
    public static final RegistryObject<BlockEntityType<HarvesterTile>> HARVESTER_TILE = TILES.register("harvester", () -> BlockEntityType.Builder.of(HarvesterTile::new, HARVESTER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<IrrigationControllerTile>> IRRIGATION_CONTROLLER_TILE = TILES.register("irrigation_controller", () -> BlockEntityType.Builder.of(IrrigationControllerTile::new, IRRIGATION_CONTROLLER_BLOCK.get()).build(null));

    public static final RegistryObject<MenuType<HarvesterContainer>> HARVESTER_CONTAINER = CONTAINERS.register("harvester", () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new HarvesterContainer(windowId, world, pos, inv, inv.player, new HarvesterData());
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

}
