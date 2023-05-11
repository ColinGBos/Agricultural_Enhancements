package vapourdrive.agricultural_enhancements.modules.soil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.utils.MachineUtils;

import java.util.List;

@Mod.EventBusSubscriber
public class CropDropSoilHandler {
    @SubscribeEvent
    public static void cropDropsEvent(BlockEvent.BreakEvent event) {
        if (!ConfigSettings.SOIL_BOOST_CROP_DROPS.get()) {
            return;
        }
        BlockPos pos = event.getPos();
        if (event.getLevel() instanceof Level level) {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof CropBlock) {
                BlockState soilState = level.getBlockState(pos.below());
                if (!soilState.hasProperty(TilledSoilBlock.SOIL_NUTRIENTS)) {
                    return;
                }
                RandomSource rand = level.getRandom();
                int nutrients = soilState.getValue(TilledSoilBlock.SOIL_NUTRIENTS);
                if (nutrients <= 0) {
                    return;
                }
                LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) level)).withRandom(level.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY);
                List<ItemStack> drops = state.getDrops(lootcontext$builder);
//                        AgriculturalEnhancements.debugLog("Drops pre-clean: " + drops);
                drops = MachineUtils.cleanItemStacks(drops);
                for (ItemStack stack : drops) {
                    if (rand.nextFloat() > ConfigSettings.SOIL_CHANCE_PER_NUTRIENT_LEVEL_TO_BOOST_DROPS.get() * nutrients) {
                        continue;
                    }
                    int count = rand.nextInt(nutrients);
                    if (count <= 0) {
                        continue;
                    }
                    ItemStack drop = stack.copy();
                    drop.setCount(Math.max(count, ConfigSettings.SOIL_MAX_ADDITIONAL_DROPS.get()));
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), drop);
                }
            }
        }
    }
}
