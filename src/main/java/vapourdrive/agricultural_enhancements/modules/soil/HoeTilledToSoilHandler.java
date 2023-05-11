package vapourdrive.agricultural_enhancements.modules.soil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.Objects;

@Mod.EventBusSubscriber
public class HoeTilledToSoilHandler {
    @SubscribeEvent
    public static void hoeTillEvent(BlockEvent.BlockToolModificationEvent event) {
        AgriculturalEnhancements.debugLog("Hoe use " + event.getFinalState());
        ItemStack offhandStack = Objects.requireNonNull(event.getPlayer()).getOffhandItem();
        int nutrients = 0;
        if (ConfigSettings.SOIL_REQUIRES_FERTILIZER.get()) {
            if (!offhandStack.is(Registration.FERTILISER.get())) {
                return;
            }
            nutrients = TilledSoilBlock.MAX_NUTRIENTS;
        }
        Block block = event.getFinalState().getBlock();
        if (!canTill(block, event.getPos(), event.getLevel())) {
            return;
        }
        int baseMoisture = Math.max((int) ((event.getLevel().getBiome(event.getPos()).get().getDownfall() - 0.1f) / 0.2f), 0);
        event.setFinalState(Registration.TILLED_SOIL_BLOCK.get().defaultBlockState().setValue(TilledSoilBlock.SOIL_MOISTURE, baseMoisture).setValue(TilledSoilBlock.SOIL_NUTRIENTS, nutrients));
        if (ConfigSettings.SOIL_REQUIRES_FERTILIZER.get()) {
            offhandStack.shrink(1);
        }
    }

    public static boolean canTill(Block block, BlockPos pos, LevelAccessor level) {
        return (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT || block == Blocks.FARMLAND) && level.getBlockState(pos.above()).isAir();
    }
}
