package vapourdrive.agricultural_enhancements.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigSettings {
    public static final String CATEGORY_MOD = "agricultural_enhancements";

    public static ForgeConfigSpec SERVER_CONFIG;
    //    public static ForgeConfigSpec CLIENT_CONFIG;
    public static final String SUBCATEGORY_FERTILIZER_PRODUCER = "fertilizer_producer";
    public static ForgeConfigSpec.IntValue FERTILIZER_PRODUCER_FUEL_STORAGE;
    public static ForgeConfigSpec.IntValue FERTILIZER_PRODUCER_NUTRIENTS_PER_FERTILIZER;
    public static ForgeConfigSpec.IntValue FERTILIZER_PRODUCER_MAX_NUTRIENTS;
    public static ForgeConfigSpec.IntValue FERTILIZER_PRODUCER_FUEL_TO_WORK;
    public static ForgeConfigSpec.IntValue FERTILIZER_PRODUCER_INGREDIENT_TIME;
    public static ForgeConfigSpec.IntValue FERTILIZER_PRODUCER_PRODUCE_TIME;

    public static final String SUBCATEGORY_HARVESTER = "harvester";
    public static ForgeConfigSpec.IntValue HARVESTER_FUEL_STORAGE;
    public static ForgeConfigSpec.IntValue HARVESTER_FUEL_TO_WORK;
    public static ForgeConfigSpec.IntValue HARVESTER_PROCESS_TIME;
    public static ForgeConfigSpec.BooleanValue HARVESTER_NON_DESTRUCTIVE_HARVESTING;

    public static final String SUBCATEGORY_IRRIGATION_CONTROLLER = "irrigation_controller";
    public static ForgeConfigSpec.IntValue IRRIGATION_CONTROLLER_FUEL_STORAGE;
    public static ForgeConfigSpec.IntValue IRRIGATION_CONTROLLER_FUEL_TO_WORK;
    public static ForgeConfigSpec.IntValue IRRIGATION_CONTROLLER_PROCESS_TIME;

    public static final String SUBCATEGORY_CROP_MANAGER = "crop_manager";
    public static ForgeConfigSpec.IntValue CROP_MANAGER_FUEL_STORAGE;
    public static ForgeConfigSpec.IntValue CROP_MANAGER_FERTILIZER_STORAGE;
    public static ForgeConfigSpec.IntValue CROP_MANAGER_FUEL_TO_WORK;
    public static ForgeConfigSpec.IntValue CROP_MANAGER_SOIL_PROCESS_TIME;
    public static ForgeConfigSpec.IntValue CROP_MANAGER_CROP_PROCESS_TIME;

    public static final String SUBCATEGORY_SOIL = "soil";
    public static ForgeConfigSpec.BooleanValue SOIL_SOFT_TRAMPLE;
    public static ForgeConfigSpec.BooleanValue SOIL_REQUIRES_FERTILIZER;
    public static ForgeConfigSpec.DoubleValue SOIL_CHANCE_TO_BOOST_CROP_GROWTH;
    public static ForgeConfigSpec.DoubleValue SOIL_CHANCE_TO_LOSE_NUTRIENTS;
    public static ForgeConfigSpec.BooleanValue SOIL_BOOST_CROP_DROPS;
    public static ForgeConfigSpec.DoubleValue SOIL_CHANCE_PER_NUTRIENT_LEVEL_TO_BOOST_DROPS;
    public static ForgeConfigSpec.IntValue SOIL_MAX_ADDITIONAL_DROPS;

    public static final String SUBCATEGORY_SPRAYER = "sprayer";
    public static ForgeConfigSpec.DoubleValue SPRAYER_CHANCE_TO_BOOST_CROP_GROWTH;
    public static ForgeConfigSpec.DoubleValue SPRAYER_CHANCE_TO_ANIMATE;
    public static ForgeConfigSpec.IntValue SPRAYER_CROP_TICK_COUNT;
    public static ForgeConfigSpec.IntValue SPRAYER_VERTICAL_RANGE;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
//        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("Agricultural Enhancements Settings").push(CATEGORY_MOD);

        setupFirstBlockConfig(SERVER_BUILDER);
//        setupFirstBlockConfig(SERVER_BUILDER, CLIENT_BUILDER);

        SERVER_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
//        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupFirstBlockConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Fertilizer Producer Settings").push(SUBCATEGORY_FERTILIZER_PRODUCER);
        FERTILIZER_PRODUCER_FUEL_STORAGE = SERVER_BUILDER.comment("Fuel Storage for the Fertilizer Producer").defineInRange("fertilizerProducerFuelStorage", 128000, 5, 1000000);
        FERTILIZER_PRODUCER_FUEL_TO_WORK = SERVER_BUILDER.comment("Fuel consumed to break down one ingredient").defineInRange("fertilizerProducerFuelConsumption", 1000, 100, 10000);
        FERTILIZER_PRODUCER_NUTRIENTS_PER_FERTILIZER = SERVER_BUILDER.comment("Amount of each nutrient for making one fertilizer").defineInRange("nutrientPerFertilizer", 5, 1, 25);
        FERTILIZER_PRODUCER_MAX_NUTRIENTS = SERVER_BUILDER.comment("Amount of each nutrient for making one fertilizer").defineInRange("fertilizerProducerNutrientStorage", 256, 10, 10000);
        FERTILIZER_PRODUCER_INGREDIENT_TIME = SERVER_BUILDER.comment("Ticks to break down one ingredient").defineInRange("fertilizerProducerTimeToConsumeIngredient", 40, 20, 320);
        FERTILIZER_PRODUCER_PRODUCE_TIME = SERVER_BUILDER.comment("Ticks to make one fertilizer").defineInRange("fertilizerProducerTimeToProduceFertilizer", 40, 20, 320);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Harvester Settings").push(SUBCATEGORY_HARVESTER);
        HARVESTER_FUEL_STORAGE = SERVER_BUILDER.comment("Fuel Storage for the Harvester").defineInRange("harvesterFuelStorage", 64000, 5, 1000000);
        HARVESTER_FUEL_TO_WORK = SERVER_BUILDER.comment("Fuel consumed to harvest one crop").defineInRange("harvesterFuelConsumption", 2400, 100, 10000);
        HARVESTER_PROCESS_TIME = SERVER_BUILDER.comment("Ticks between harvests").defineInRange("harvesterTicksBetweenHarvests", 20, 20, 320);
        HARVESTER_NON_DESTRUCTIVE_HARVESTING = SERVER_BUILDER.comment("Enables the non-destructing harvesting of the Harvester - no need to replant crops").define("harvesterNonDestructive", true);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Irrigation Controller Settings").push(SUBCATEGORY_IRRIGATION_CONTROLLER);
        IRRIGATION_CONTROLLER_FUEL_STORAGE = SERVER_BUILDER.comment("Fuel Storage for the Irrigation Controller").defineInRange("irrigationControllerFuelStorage", 32000, 5, 1000000);
        IRRIGATION_CONTROLLER_FUEL_TO_WORK = SERVER_BUILDER.comment("Fuel consumed while working, consumed once per second").defineInRange("irrigationControllerFuelConsumption", 100, 10, 10000);
        IRRIGATION_CONTROLLER_PROCESS_TIME = SERVER_BUILDER.comment("Ticks between pipe pressurizing events").defineInRange("irrigationControllerTicksBetweenWork", 20, 20, 320);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Crop Manager Settings").push(SUBCATEGORY_CROP_MANAGER);
        CROP_MANAGER_FUEL_STORAGE = SERVER_BUILDER.comment("Fuel Storage for the Crop Manager").defineInRange("cropManagerFuelStorage", 128000, 5, 1000000);
        CROP_MANAGER_FERTILIZER_STORAGE = SERVER_BUILDER.comment("Fertilizer Storage for the Crop Manager").defineInRange("cropManagerFertilizerStorage", 25600, 800, 1000000);
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
        SPRAYER_CHANCE_TO_BOOST_CROP_GROWTH = SERVER_BUILDER.comment("Chance per random tick to tick crops within radius; every random tick").defineInRange("sprayerChanceToBoostCropTicks", 1f, 0f, 1f);
        SPRAYER_CROP_TICK_COUNT = SERVER_BUILDER.comment("Number of ticks for each crop to potentially be updated during").defineInRange("sprayerCropUpdateTickCount", 10, 1, 25);
        SPRAYER_VERTICAL_RANGE = SERVER_BUILDER.comment("Max number of blocks down from the sprayer the crop can be").defineInRange("sprayerMaxVerticalRange", 8, 2, 15);
        SPRAYER_CHANCE_TO_ANIMATE = SERVER_BUILDER.comment("Chance per animation tick for the sprayer to spawn particles").defineInRange("sprayerChanceToAnimate", 0.2f, 0f, 1f);
        SERVER_BUILDER.pop();

    }

}
