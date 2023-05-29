package vapourdrive.agricultural_enhancements.content.soil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber
public class HoeTilledToSoilHandler {

    @SubscribeEvent
    public static void hoeTillEvent(BlockEvent.BlockToolModificationEvent event) {
        AgriculturalEnhancements.debugLog("Hoe use " + event.getFinalState());
        ItemStack offhandStack = Objects.requireNonNull(event.getPlayer()).getOffhandItem();
        int nutrients = 0;

        BlockState state = event.getFinalState();

        Block block = state.getBlock();
        if (cannotTill(block, event.getPos(), event.getLevel())) {
            return;
        }

        if (ConfigSettings.SOIL_REQUIRES_FERTILIZER.get() && !offhandStack.is(Registration.FERTILISER.get()) && !state.is(Registration.SOIL_BLOCK.get())) {
            return;
        }

        boolean consume = false;
        if (offhandStack.is(Registration.FERTILISER.get())) {
            nutrients = TilledSoilBlock.MAX_NUTRIENTS;
            consume = true;
        }

        int baseMoisture = Math.max(0, TilledSoilBlock.getEnvMoisture(event.getLevel(), event.getPos())-1);
        if (state.hasProperty(TilledSoilBlock.SOIL_NUTRIENTS) && state.hasProperty(TilledSoilBlock.SOIL_MOISTURE)) {
            nutrients = Math.max(nutrients, state.getValue(TilledSoilBlock.SOIL_NUTRIENTS));
            baseMoisture = Math.max(baseMoisture, state.getValue(TilledSoilBlock.SOIL_MOISTURE));
        }
        event.setFinalState(Registration.TILLED_SOIL_BLOCK.get().defaultBlockState().setValue(TilledSoilBlock.SOIL_MOISTURE, baseMoisture).setValue(TilledSoilBlock.SOIL_NUTRIENTS, nutrients));
        if (consume) {
            offhandStack.shrink(1);
        }
    }

    public static boolean cannotTill(Block block, BlockPos pos, LevelAccessor level) {
        List<Block> blocks = List.of(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.DIRT_PATH, Blocks.FARMLAND, Registration.SOIL_BLOCK.get());
        if (!blocks.contains(block)) {
            return true;
        } else return !level.getBlockState(pos.above()).getMaterial().isReplaceable();
    }
}
