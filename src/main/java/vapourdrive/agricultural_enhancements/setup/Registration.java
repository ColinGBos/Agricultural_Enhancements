package vapourdrive.agricultural_enhancements.setup;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.content.duskbloom.*;
import vapourdrive.agricultural_enhancements.content.fertilizer.Fertilizer;
import vapourdrive.agricultural_enhancements.content.fertilizer.FertilizerRecipe;
import vapourdrive.agricultural_enhancements.content.fertilizer.producer.*;
import vapourdrive.agricultural_enhancements.content.harvester.*;
import vapourdrive.agricultural_enhancements.content.irrigation.IrrigationPipeBlock;
import vapourdrive.agricultural_enhancements.content.irrigation.SprayerPipeBlock;
import vapourdrive.agricultural_enhancements.content.irrigation.WateringCan;
import vapourdrive.agricultural_enhancements.content.irrigation.irrigation_controller.*;
import vapourdrive.agricultural_enhancements.content.manager.*;
import vapourdrive.agricultural_enhancements.content.soil.SoilBlock;
import vapourdrive.agricultural_enhancements.content.soil.TilledSoilBlock;
import vapourdrive.vapourware.VapourWare;
import vapourdrive.vapourware.shared.base.BaseInfoItem;
import vapourdrive.vapourware.shared.base.BaseInfoItemBlock;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

import java.util.function.Supplier;

import static vapourdrive.agricultural_enhancements.AgriculturalEnhancements.MODID;

public class Registration {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, VapourWare.MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, MODID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MODID);

//    DATA FOR TILES
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> WATER_DATA = REGISTRAR.registerComponentType(
    "water", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FERTILIZER_DATA = REGISTRAR.registerComponentType(
    "fertilizer", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> NITROGEN_DATA = REGISTRAR.registerComponentType(
            "nitrogen", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PHOSPHORUS_DATA = REGISTRAR.registerComponentType(
            "phosphorus", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> POTASSIUM_DATA = REGISTRAR.registerComponentType(
            "potassium", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DESTRUCTIVE_DATA = REGISTRAR.registerComponentType(
            "destructive", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
    );

//    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MODID);
//    public static final RecipeType<FertilizerRecipe> FERTILIZER_RECIPE = register("fertilizer");
//
//    static <T extends Recipe<?>> RecipeType<T> register(final String identifier) {
//        return (RecipeType) Registry.register(BuiltInRegistries.RECIPE_TYPE, ResourceLocation.withDefaultNamespace(identifier), new RecipeType<T>() {
//            public String toString() {
//                return identifier;
//            }
//        });
//    }

//    HARVESTER
    public static final Supplier<HarvesterBlock> HARVESTER_BLOCK = BLOCKS.register("harvester", () -> new HarvesterBlock());
    public static final Supplier<Item> HARVESTER_ITEM = ITEMS.register("harvester", () -> new HarvesterItem(HARVESTER_BLOCK.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<HarvesterTile>> HARVESTER_TILE = TILES.register("harvester", () -> BlockEntityType.Builder.of(HarvesterTile::new, HARVESTER_BLOCK.get()).build(null));
    public static final Supplier<MenuType<HarvesterMenu>> HARVESTER_MENU = MENUS.register("harvester",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                Level world = inv.player.getCommandSenderWorld();
                return new HarvesterMenu(windowId, world, pos, inv, inv.player, new HarvesterData());
            }));
    @SuppressWarnings("all")

//    FERTILIZER PRODUCER
    public static final Supplier<FertilizerProducerBlock> FERTILIZER_PRODUCER_BLOCK = BLOCKS.register("fertilizer_producer", () -> new FertilizerProducerBlock());
    public static final Supplier<Item> FERTILIZER_PRODUCER_ITEM = ITEMS.register("fertilizer_producer", () -> new FertilizerProducerItem(FERTILIZER_PRODUCER_BLOCK.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<FertilizerProducerTile>> FERTILIZER_PRODUCER_TILE = TILES.register("fertilizer_producer", () -> BlockEntityType.Builder.of(FertilizerProducerTile::new, FERTILIZER_PRODUCER_BLOCK.get()).build(null));
    public static final Supplier<MenuType<FertilizerProducerMenu>> FERTILIZER_PRODUCER_MENU = MENUS.register("fertilizer_producer",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                Level world = inv.player.getCommandSenderWorld();
                return new FertilizerProducerMenu(windowId, world, pos, inv, inv.player, new FertilizerProducerData());
            }));
    public static final Supplier<Fertilizer> FERTILIZER = ITEMS.register("fertilizer", () -> new Fertilizer(new Item.Properties()));
    public static final Supplier<RecipeType<FertilizerRecipe>> FERTILIZER_TYPE = RECIPE_TYPES.register("fertilizer", () -> FertilizerRecipe.Type.INSTANCE);
    public static final Supplier<RecipeSerializer<FertilizerRecipe>> FERTILIZER_SERIALIZER = RECIPE_SERIALIZERS.register("fertilizer", () -> FertilizerRecipe.Serializer.INSTANCE);

    //     CROP MANAGER
    public static final Supplier<CropManagerBlock> CROP_MANAGER_BLOCK = BLOCKS.register("crop_manager", () -> new CropManagerBlock());
    public static final Supplier<Item> CROP_MANAGER_ITEM = ITEMS.register("crop_manager", () -> new CropManagerItem(CROP_MANAGER_BLOCK.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<CropManagerTile>> CROP_MANAGER_TILE = TILES.register("crop_manager", () -> BlockEntityType.Builder.of(CropManagerTile::new, CROP_MANAGER_BLOCK.get()).build(null));
    public static final Supplier<MenuType<CropManagerMenu>> CROP_MANAGER_MENU = MENUS.register("crop_manager",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                Level world = inv.player.getCommandSenderWorld();
                return new CropManagerMenu(windowId, world, pos, inv, inv.player, new CropManagerData());
            }));

//    SOIL
    public static final Supplier<TilledSoilBlock> TILLED_SOIL_BLOCK = BLOCKS.register("tilled_soil", TilledSoilBlock::new);
    public static final Supplier<Item> TILLED_SOIL_ITEM = ITEMS.register("tilled_soil", () -> new BaseInfoItemBlock(TILLED_SOIL_BLOCK.get(), new Item.Properties(), new DeferredComponent(MODID, "tilled_soil")));
    public static final Supplier<SoilBlock> SOIL_BLOCK = BLOCKS.register("soil", SoilBlock::new);
    public static final Supplier<Item> SOIL_ITEM = ITEMS.register("soil", () -> new BaseInfoItemBlock(SOIL_BLOCK.get(), new Item.Properties(), new DeferredComponent(MODID, "soil")));

//    IRRIGATION CONTROLLER
    public static final Supplier<IrrigationControllerBlock> IRRIGATION_CONTROLLER_BLOCK = BLOCKS.register("irrigation_controller", () -> new IrrigationControllerBlock());
    public static final Supplier<Item> IRRIGATION_CONTROLLER_ITEM = ITEMS.register("irrigation_controller", () -> new IrrigationControllerItem(IRRIGATION_CONTROLLER_BLOCK.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<IrrigationControllerTile>> IRRIGATION_CONTROLLER_TILE = TILES.register("irrigation_controller", () -> BlockEntityType.Builder.of(IrrigationControllerTile::new, IRRIGATION_CONTROLLER_BLOCK.get()).build(null));
    public static final Supplier<MenuType<IrrigationControllerMenu>> IRRIGATION_CONTROLLER_MENU = MENUS.register("irrigation_controller",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                Level world = inv.player.getCommandSenderWorld();
                return new IrrigationControllerMenu(windowId, world, pos, inv, inv.player, new IrrigationControllerData());
            }));

//    IRRIGATION PIPES
    public static final Supplier<IrrigationPipeBlock> IRRIGATION_PIPE_BLOCK = BLOCKS.register("irrigation_pipe", () -> new IrrigationPipeBlock());
    public static final Supplier<Item> IRRIGATION_PIPE_ITEM = ITEMS.register("irrigation_pipe", () -> new BaseInfoItemBlock(IRRIGATION_PIPE_BLOCK.get(), new Item.Properties(), new DeferredComponent(MODID, "irrigation_pipe")));

    public static final Supplier<SprayerPipeBlock> SPRAYER_PIPE_BLOCK = BLOCKS.register("sprayer_pipe", SprayerPipeBlock::new);
    public static final Supplier<Item> SPRAYER_PIPE_ITEM = ITEMS.register("sprayer_pipe", () -> new BaseInfoItemBlock(SPRAYER_PIPE_BLOCK.get(), new Item.Properties(), new DeferredComponent(MODID, "sprayer_pipe", ConfigSettings.SPRAYER_VERTICAL_RANGE)));

//    DUSKBLOOM PLANT
    public static final Supplier<DuskBloomBlock> DUSKBLOOM_BLOCK = BLOCKS.register("duskbloom", () -> new DuskBloomBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Item> DUSKBLOOM_SEEDS = ITEMS.register("duskbloom_seeds", () -> new BaseInfoItemBlock(DUSKBLOOM_BLOCK.get(), new Item.Properties(),new DeferredComponent(MODID, "duskbloom_seed")));

//    DUSKBLOOK MATERIAL
    public static final Supplier<Item> DUSKBLOOM_GLOB = ITEMS.register("duskbloom_glob",
            () -> new BaseInfoItem(new Item.Properties().food((new FoodProperties.Builder()).nutrition(4).saturationModifier(0.8F).build()) ,new DeferredComponent(MODID, "duskbloom_glob")));
    public static final Supplier<Item> DUSKBLOOM_SHARD = ITEMS.register("duskbloom_shard",
            () -> new BaseInfoItem(new Item.Properties() ,new DeferredComponent(MODID, "duskbloom_shard")));

    public static final Supplier<Block> DUSKBLOOM_GLOB_BLOCK = BLOCKS.register("duskbloom_glob_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).instrument(NoteBlockInstrument.BASS).sound(SoundType.TUFF).requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)));
    public static final Supplier<Block> DUSKBLOOM_SHARD_BLOCK = BLOCKS.register("duskbloom_shard_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).instrument(NoteBlockInstrument.SNARE).sound(SoundType.METAL).requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)));
    public static final Supplier<Item> DUSKBLOOM_GLOB_BLOCK_ITEM = ITEMS.register("duskbloom_glob_block", () -> new BlockItem(DUSKBLOOM_GLOB_BLOCK.get(), new Item.Properties()));
    public static final Supplier<Item> DUSKBLOOM_SHARD_BLOCK_ITEM = ITEMS.register("duskbloom_shard_block", () -> new BlockItem(DUSKBLOOM_SHARD_BLOCK.get(), new Item.Properties()));

    //    DUSKBLOOK TOOLS
    public static final Supplier<Item> DUSKBLOOM_PICKAXE = ITEMS.register(
            "duskbloom_pickaxe", () -> new DuskBloomPickaxe(DuskBloomToolTier.DUSKBLOOM, new Item.Properties().attributes(PickaxeItem.createAttributes(DuskBloomToolTier.DUSKBLOOM, 1.25F, -2.8F)))
    );
    public static final Supplier<Item> DUSKBLOOM_AXE = ITEMS.register(
            "duskbloom_axe", () -> new DuskBloomAxe(DuskBloomToolTier.DUSKBLOOM, new Item.Properties().attributes(AxeItem.createAttributes(DuskBloomToolTier.DUSKBLOOM, 6.5F, -3.1F)))
    );
    public static final Supplier<Item> DUSKBLOOM_SWORD = ITEMS.register(
            "duskbloom_sword", () -> new DuskBloomSword(DuskBloomToolTier.DUSKBLOOM, new Item.Properties().attributes(SwordItem.createAttributes(DuskBloomToolTier.DUSKBLOOM, 3.25F, -2.4F)))
    );
    public static final Supplier<Item> DUSKBLOOM_SHOVEL = ITEMS.register(
            "duskbloom_shovel", () -> new DuskBloomShovel(DuskBloomToolTier.DUSKBLOOM, new Item.Properties().attributes(ShovelItem.createAttributes(DuskBloomToolTier.DUSKBLOOM, 1.75F, -3.0F)))
    );
    public static final Supplier<Item> DUSKBLOOM_HOE = ITEMS.register(
            "duskbloom_hoe", () -> new DuskBloomHoe(DuskBloomToolTier.DUSKBLOOM, new Item.Properties().attributes(HoeItem.createAttributes(DuskBloomToolTier.DUSKBLOOM, -1.75F, -1.0F)))
    );

    public static final Supplier<WateringCan> WATERING_CAN = ITEMS.register("watering_can", () -> new WateringCan(new Item.Properties()));









    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, AgriculturalEnhancements.MODID);
    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_ITEM = LOOT_MODIFIERS.register("add_item", AddItemModifier.CODEC);

    public static void init(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        TILES.register(eventBus);
        MENUS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
        REGISTRAR.register(eventBus);
        LOOT_MODIFIERS.register(eventBus);
    }

    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        // Add to ingredients tab
        if (event.getTab() == vapourdrive.vapourware.setup.Registration.VAPOUR_GROUP.get()) {
            event.accept(HARVESTER_ITEM.get());
            event.accept(FERTILIZER_PRODUCER_ITEM.get());
            event.accept(CROP_MANAGER_ITEM.get());
            event.accept(IRRIGATION_CONTROLLER_ITEM.get());
            event.accept(IRRIGATION_PIPE_ITEM.get());
            event.accept(SPRAYER_PIPE_ITEM.get());
            event.accept(TILLED_SOIL_ITEM.get());
            event.accept(SOIL_ITEM.get());
            event.accept(WATERING_CAN.get());
            event.accept(FERTILIZER.get());
            event.accept(DUSKBLOOM_SEEDS.get());
            event.accept(DUSKBLOOM_GLOB.get());
            event.accept(DUSKBLOOM_GLOB_BLOCK_ITEM.get());
            event.accept(DUSKBLOOM_SHARD.get());
            event.accept(DUSKBLOOM_SHARD_BLOCK_ITEM.get());
            event.accept(DUSKBLOOM_PICKAXE.get());
            event.accept(DUSKBLOOM_AXE.get());
            event.accept(DUSKBLOOM_SWORD.get());
            event.accept(DUSKBLOOM_SHOVEL.get());
            event.accept(DUSKBLOOM_HOE.get());
        }
    }
}
