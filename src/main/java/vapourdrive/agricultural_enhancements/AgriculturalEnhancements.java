package vapourdrive.agricultural_enhancements;


import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.setup.ModSetup;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.ArrayList;

@Mod(AgriculturalEnhancements.MODID)
public class AgriculturalEnhancements {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "agriculturalenhancements";
    public static final boolean debugMode = true;
    public static final ArrayList<ItemLike> seeds = new ArrayList<>();

    public AgriculturalEnhancements(ModContainer container) {
        IEventBus eventBus = container.getEventBus();
//        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigSettings.CLIENT_CONFIG);
        container.registerConfig(ModConfig.Type.SERVER, ConfigSettings.SERVER_CONFIG);

        Registration.init(eventBus);

        // Register the setup method for modloading
        assert eventBus != null;
//        eventBus.addListener((FMLCommonSetupEvent event) -> ModSetup.init());
        eventBus.addListener(ModSetup::init);
        eventBus.addListener(Registration::buildContents);
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
