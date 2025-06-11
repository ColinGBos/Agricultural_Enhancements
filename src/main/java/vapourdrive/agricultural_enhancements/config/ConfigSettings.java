package vapourdrive.agricultural_enhancements.config;

import net.neoforged.neoforge.common.ModConfigSpec;


public class ConfigSettings {
    public static final String CATEGORY_MOD = "agricultural_enhancements";

    public static final ModConfigSpec SERVER_CONFIG;
    //    public static ModConfigSpec CLIENT_CONFIG;
    public static final String SUBCATEGORY_FERTILIZER_PRODUCER = "fertilizer_producer";
    public static ModConfigSpec.IntValue FERTILIZER_PRODUCER_FUEL_STORAGE;
    public static ModConfigSpec.IntValue FERTILIZER_PRODUCER_NUTRIENTS_PER_FERTILIZER;
    public static ModConfigSpec.IntValue FERTILIZER_PRODUCER_MAX_NUTRIENTS;
    public static ModConfigSpec.IntValue FERTILIZER_PRODUCER_FUEL_TO_WORK;
    public static ModConfigSpec.IntValue FERTILIZER_PRODUCER_INGREDIENT_TIME;
    public static ModConfigSpec.IntValue FERTILIZER_PRODUCER_PRODUCE_TIME;

    public static final String SUBCATEGORY_HARVESTER = "harvester";
    public static ModConfigSpec.IntValue HARVESTER_FUEL_STORAGE;
    public static ModConfigSpec.IntValue HARVESTER_FUEL_TO_WORK;
    public static ModConfigSpec.IntValue HARVESTER_PROCESS_TIME;
    public static ModConfigSpec.BooleanValue HARVESTER_NON_DESTRUCTIVE_HARVESTING;

    public static final String SUBCATEGORY_IRRIGATION_CONTROLLER = "irrigation_controller";
    public static ModConfigSpec.IntValue IRRIGATION_CONTROLLER_FUEL_STORAGE;
    public static ModConfigSpec.IntValue IRRIGATION_CONTROLLER_FUEL_TO_WORK;
    public static ModConfigSpec.IntValue IRRIGATION_CONTROLLER_PROCESS_TIME;

    public static final String SUBCATEGORY_CROP_MANAGER = "crop_manager";
    public static ModConfigSpec.IntValue CROP_MANAGER_FUEL_STORAGE;
    public static ModConfigSpec.IntValue CROP_MANAGER_FERTILIZER_STORAGE;
    public static ModConfigSpec.IntValue CROP_MANAGER_FUEL_TO_WORK;
    public static ModConfigSpec.IntValue CROP_MANAGER_SOIL_PROCESS_TIME;
    public static ModConfigSpec.IntValue CROP_MANAGER_CROP_PROCESS_TIME;

    public static final String SUBCATEGORY_SOIL = "soil";
    public static ModConfigSpec.BooleanValue SOIL_SOFT_TRAMPLE;
    public static ModConfigSpec.BooleanValue SOIL_REQUIRES_FERTILIZER;
    public static ModConfigSpec.DoubleValue SOIL_CHANCE_TO_BOOST_CROP_GROWTH;
    public static ModConfigSpec.DoubleValue SOIL_CHANCE_TO_LOSE_NUTRIENTS;
    public static ModConfigSpec.BooleanValue SOIL_BOOST_CROP_DROPS;
    public static ModConfigSpec.DoubleValue SOIL_CHANCE_PER_NUTRIENT_LEVEL_TO_BOOST_DROPS;
    public static ModConfigSpec.IntValue SOIL_MAX_ADDITIONAL_DROPS;

    public static final String SUBCATEGORY_SPRAYER = "sprayer";
    public static ModConfigSpec.DoubleValue SPRAYER_CHANCE_TO_BOOST_CROP_GROWTH;
    public static ModConfigSpec.DoubleValue SPRAYER_CHANCE_TO_ANIMATE;
    public static ModConfigSpec.IntValue SPRAYER_CROP_TICK_COUNT;
    public static ModConfigSpec.IntValue SPRAYER_VERTICAL_RANGE;

    public static final String SUBCATEGORY_MIXINS = "mixins";
    public static ModConfigSpec.BooleanValue REPLACE_CROP_BLOCK_SHAPE;
    public static ModConfigSpec.BooleanValue REPLACE_BEETROOT_SHAPE;
    public static ModConfigSpec.BooleanValue REPLACE_POTATO_SHAPE;
    public static ModConfigSpec.BooleanValue REPLACE_CARROT_SHAPE;

    static {
        ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
//        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

        SERVER_BUILDER.comment("Agricultural Enhancements Settings").push(CATEGORY_MOD);

        setupFirstBlockConfig(SERVER_BUILDER);
//        setupFirstBlockConfig(SERVER_BUILDER, CLIENT_BUILDER);

        SERVER_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
//        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupFirstBlockConfig(ModConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Fertilizer Producer Settings").push(SUBCATEGORY_FERTILIZER_PRODUCER);
        FERTILIZER_PRODUCER_FUEL_STORAGE = SERVER_BUILDER.comment("Fuel Storage for the Fertilizer Producer").defineInRange("fertilizerProducerFuelStorage", 128000, 5, 1000000);
        FERTILIZER_PRODUCER_FUEL_TO_WORK = SERVER_BUILDER.comment("Fuel consumed to break down one ingredient").defineInRange("fertilizerProducerFuelConsumption", 2400, 100, 10000);
        FERTILIZER_PRODUCER_NUTRIENTS_PER_FERTILIZER = SERVER_BUILDER.comment("Amount of each nutrient for making one fertilizer").defineInRange("nutrientPerFertilizer", 250, 10, 5000);
        FERTILIZER_PRODUCER_MAX_NUTRIENTS = SERVER_BUILDER.comment("Amount of each nutrient for making one fertilizer").defineInRange("fertilizerProducerNutrientStorage", 25600, 1000, 10000000);
        FERTILIZER_PRODUCER_INGREDIENT_TIME = SERVER_BUILDER.comment("Ticks to break down one ingredient").defineInRange("fertilizerProducerTimeToConsumeIngredient", 40, 20, 320);
        FERTILIZER_PRODUCER_PRODUCE_TIME = SERVER_BUILDER.comment("Ticks to make one fertilizer").defineInRange("fertilizerProducerTimeToProduceFertilizer", 40, 20, 320);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Harvester Settings").push(SUBCATEGORY_HARVESTER);
        HARVESTER_FUEL_STORAGE = SERVER_BUILDER.comment("Fuel Storage for the Harvester").defineInRange("harvesterFuelStorage", 64000, 5, 1000000);
        HARVESTER_FUEL_TO_WORK = SERVER_BUILDER.comment("Fuel consumed to harvest one crop").defineInRange("harvesterFuelConsumption", 4800, 100, 10000);
        HARVESTER_PROCESS_TIME = SERVER_BUILDER.comment("Ticks between harvests").defineInRange("harvesterTicksBetweenHarvests", 20, 20, 320);
        HARVESTER_NON_DESTRUCTIVE_HARVESTING = SERVER_BUILDER.comment("Enables the non-destructing harvesting of the Harvester - no need to replant crops").define("harvesterNonDestructive", true);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Irrigation Controller Settings").push(SUBCATEGORY_IRRIGATION_CONTROLLER);
        IRRIGATION_CONTROLLER_FUEL_STORAGE = SERVER_BUILDER.comment("Fuel Storage for the Irrigation Controller").defineInRange("irrigationControllerFuelStorage", 32000, 5, 1000000);
        IRRIGATION_CONTROLLER_FUEL_TO_WORK = SERVER_BUILDER.comment("Fuel consumed while working, consumed once per second").defineInRange("irrigationControllerFuelConsumption", 500, 10, 10000);
        IRRIGATION_CONTROLLER_PROCESS_TIME = SERVER_BUILDER.comment("Ticks between pipe pressurizing events").defineInRange("irrigationControllerTicksBetweenWork", 20, 20, 320);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Crop Manager Settings").push(SUBCATEGORY_CROP_MANAGER);
        CROP_MANAGER_FUEL_STORAGE = SERVER_BUILDER.comment("Fuel Storage for the Crop Manager").defineInRange("cropManagerFuelStorage", 128000, 5, 1000000);
        CROP_MANAGER_FERTILIZER_STORAGE = SERVER_BUILDER.comment("Fertilizer Storage for the Crop Manager").defineInRange("cropManagerFertilizerStorage", 256000, 8000, 10000000);
        CROP_MANAGER_FUEL_TO_WORK = SERVER_BUILDER.comment("Fuel to do one unit of work, planting or managing soil").defineInRange("cropManagerFuelConsumption", 2400, 10, 10000);
        CROP_MANAGER_SOIL_PROCESS_TIME = SERVER_BUILDER.comment("Ticks between fertilizing soil, works all 9 blocks").defineInRange("cropManagerTicksBetweenFertilizingSoil", 40, 20, 320);
        CROP_MANAGER_CROP_PROCESS_TIME = SERVER_BUILDER.comment("Ticks between planting crops, works one block at a time").defineInRange("cropManagerTicksBetweenPlanting", 5, 5, 160);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Soil Settings").push(SUBCATEGORY_SOIL);
        SOIL_SOFT_TRAMPLE = SERVER_BUILDER.comment("Enables the soft trample feature (plants drop to minimum age on trample)").define("soilEnableSoftTrample", true);
        SOIL_REQUIRES_FERTILIZER = SERVER_BUILDER.comment("When tilling dirt with a hoe, will only turn into soil with use of fertilizer in off-hand").define("soilTillsWithFertilizer", true);
        SOIL_CHANCE_TO_BOOST_CROP_GROWTH = SERVER_BUILDER.comment("Chance per random tick to tick crops planted above; every random tick").defineInRange("soilChanceToBoostCropTicks", 0.15f, 0f, 1f);
        SOIL_CHANCE_TO_LOSE_NUTRIENTS = SERVER_BUILDER.comment("Chance per crop boost that the soil loses nutrients").defineInRange("soilChanceToLoseNutrients", 0.1f, 0f, 1f);
        SOIL_BOOST_CROP_DROPS = SERVER_BUILDER.comment("Enables the drop additions for the tilled soil").define("soilEnableAdditionalDrops", true);
        SOIL_CHANCE_PER_NUTRIENT_LEVEL_TO_BOOST_DROPS = SERVER_BUILDER.comment("Chance per nutrient level to boost crop drops").defineInRange("soilChancePerNutrientLevelToBoostDrops", 0.2f, 0f, 1f);
        SOIL_MAX_ADDITIONAL_DROPS = SERVER_BUILDER.comment("Max additional drops for each different item that can be added").defineInRange("soilMaxAdditionalDrops", 5, 0, 5);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Irrigation Sprayer Settings").push(SUBCATEGORY_SPRAYER);
        SPRAYER_CHANCE_TO_BOOST_CROP_GROWTH = SERVER_BUILDER.comment("Chance per random tick to tick crops within radius; every random tick").defineInRange("sprayerChanceToBoostCropTicks", 0.2f, 0f, 1f);
        SPRAYER_CROP_TICK_COUNT = SERVER_BUILDER.comment("Number of ticks for each crop to potentially be updated during").defineInRange("sprayerCropUpdateTickCount", 4, 1, 15);
        SPRAYER_VERTICAL_RANGE = SERVER_BUILDER.comment("Max number of blocks down from the sprayer the crop can be").defineInRange("sprayerMaxVerticalRange", 5, 2, 10);
        SPRAYER_CHANCE_TO_ANIMATE = SERVER_BUILDER.comment("Chance per animation tick for the sprayer to spawn particles").defineInRange("sprayerChanceToAnimate", 0.2f, 0f, 1f);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Mixin Settings").push(SUBCATEGORY_MIXINS);
        REPLACE_CROP_BLOCK_SHAPE = SERVER_BUILDER.comment("Replace the hitbox of Crop Block with a tighter shape (allows access to soil and follows texture better)").define("replaceCropBlockShape", false);
        REPLACE_BEETROOT_SHAPE = SERVER_BUILDER.comment("Replace the hitbox of BeetRoot Block with a tighter shape (allows access to soil and follows texture better)").define("replaceBeetRootBlockShape", false);
        REPLACE_POTATO_SHAPE = SERVER_BUILDER.comment("Replace the hitbox of Potato Block with a tighter shape (allows access to soil and follows texture better)").define("replacePotatoBlockShape", false);
        REPLACE_CARROT_SHAPE = SERVER_BUILDER.comment("Replace the hitbox of Carrot Block with a tighter shape (allows access to soil and follows texture better)").define("replaceCarrotBlockShape", false);
        SERVER_BUILDER.pop();

    }

}
