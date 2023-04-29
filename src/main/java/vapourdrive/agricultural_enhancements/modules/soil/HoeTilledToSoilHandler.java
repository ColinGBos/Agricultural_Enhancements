package vapourdrive.agricultural_enhancements.modules.soil;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.Objects;

@Mod.EventBusSubscriber
public class HoeTilledToSoilHandler {
    @SubscribeEvent
    public static void hoeTillEvent(BlockEvent.BlockToolModificationEvent event) {
        AgriculturalEnhancements.debugLog("Hoe use "+event.getFinalState());
        ItemStack offhandStack = Objects.requireNonNull(event.getPlayer()).getOffhandItem();
        if(Objects.requireNonNull(event.getPlayer()).getOffhandItem().is(Registration.FERTILISER.get())){
            Block block = event.getFinalState().getBlock();
            if ((block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT|| block == Blocks.FARMLAND) && event.getLevel().getBlockState(event.getPos().above()).isAir()) {
                int baseMoisture = Math.max((int)((event.getLevel().getBiome(event.getPos()).get().getDownfall()-0.1f)/0.2f),0);
                event.setFinalState(Registration.TILLED_SOIL_BLOCK.get().defaultBlockState().setValue(TilledSoilBlock.SOIL_MOISTURE, baseMoisture).setValue(TilledSoilBlock.SOIL_NUTRIENTS, TilledSoilBlock.MAX_NUTRIENTS));
                offhandStack.shrink(1);
            }
        }
    }
}
