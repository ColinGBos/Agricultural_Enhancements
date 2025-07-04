package vapourdrive.agricultural_enhancements.content.soil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.List;

@EventBusSubscriber
public class HoeTilledToSoilHandler {

    @SubscribeEvent
    public static void hoeTillEvent(BlockEvent.BlockToolModificationEvent event) {
        AgriculturalEnhancements.debugLog("Hoe use " + event.getFinalState());

        ItemStack potentialFert = ItemStack.EMPTY;
        Player player = event.getPlayer();
        if (player != null) {
            potentialFert = player.getOffhandItem();
        }
        int nutrients = 0;

        BlockState state = event.getFinalState();

        Block block = state.getBlock();
        if (cannotTill(block, event.getPos(), event.getLevel())) {
            return;
        }

        if (ConfigSettings.SOIL_REQUIRES_FERTILIZER.get() && !potentialFert.is(Registration.FERTILIZER.get()) && !state.is(Registration.SOIL_BLOCK.get())) {
            return;
        }

        if (ConfigSettings.SOIL_SNEAK_PREVENTION.get()) {
            assert player != null;
            if (player.isCrouching()) {
                return;
            }
        }

        boolean consume = false;
        if (potentialFert.is(Registration.FERTILIZER.get())) {
            nutrients = TilledSoilBlock.MAX_NUTRIENTS;
            consume = true;
        }

        int baseMoisture = Math.max(0, TilledSoilBlock.getEnvMoisture(event.getLevel(), event.getPos()) - 1);
        if (state.hasProperty(TilledSoilBlock.SOIL_NUTRIENTS) && state.hasProperty(TilledSoilBlock.SOIL_MOISTURE)) {
            nutrients = Math.max(nutrients, state.getValue(TilledSoilBlock.SOIL_NUTRIENTS));
            baseMoisture = Math.max(baseMoisture, state.getValue(TilledSoilBlock.SOIL_MOISTURE));
        }
        event.setFinalState(Registration.TILLED_SOIL_BLOCK.get().defaultBlockState().setValue(TilledSoilBlock.SOIL_MOISTURE, baseMoisture).setValue(TilledSoilBlock.SOIL_NUTRIENTS, nutrients));
        if (consume) {
            potentialFert.shrink(1);
        }
    }

    public static boolean cannotTill(Block block, BlockPos pos, LevelAccessor level) {
        List<Block> blocks = List.of(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.DIRT_PATH, Blocks.FARMLAND, Registration.SOIL_BLOCK.get());
        if (!blocks.contains(block)) {
            return true;
        } else return !level.getBlockState(pos.above()).canBeReplaced();
    }
}
