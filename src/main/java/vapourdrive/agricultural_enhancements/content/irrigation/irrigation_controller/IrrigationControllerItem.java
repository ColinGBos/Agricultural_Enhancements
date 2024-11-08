package vapourdrive.agricultural_enhancements.content.irrigation.irrigation_controller;

import net.minecraft.world.level.block.Block;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.vapourware.shared.base.BaseMachineItem;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

public class IrrigationControllerItem extends BaseMachineItem {
    public IrrigationControllerItem(Block block, Properties properties) {
        super(block, properties, new DeferredComponent(AgriculturalEnhancements.MODID, "irrigation_controller.info_1"));
    }
}
