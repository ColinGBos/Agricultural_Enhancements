package vapourdrive.agricultural_enhancements;


import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.setup.ClientSetup;
import vapourdrive.agricultural_enhancements.setup.ModSetup;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.ArrayList;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AgriculturalEnhancements.MODID)
public class AgriculturalEnhancements {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "agriculturalenhancements";
    public static final boolean debugMode = false;
    public static final ArrayList<ItemLike> seeds = new ArrayList<>();

    public AgriculturalEnhancements() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
//        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigSettings.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigSettings.SERVER_CONFIG);

        Registration.init(eventBus);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent event) -> ModSetup.init());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::setup);
    }

    public static void debugLog(String toLog) {
        if (isDebugMode()) {
            LOGGER.log(Level.DEBUG, toLog);
        }
    }

    public static boolean isDebugMode() {
        return java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("jdwp") && debugMode;
    }

}
