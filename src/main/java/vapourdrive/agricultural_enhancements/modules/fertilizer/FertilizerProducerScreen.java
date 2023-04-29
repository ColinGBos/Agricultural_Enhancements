package vapourdrive.agricultural_enhancements.modules.fertilizer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import vapourdrive.agricultural_enhancements.modules.base.AbstractBaseMachineScreen;

public class FertilizerProducerScreen extends AbstractBaseMachineScreen<FertilizerProducerContainer> {
    public FertilizerProducerScreen(FertilizerProducerContainer container, Inventory inv, Component name) {
        super(container, inv, name, "harvester", 12, 8, 158, 6, 1);
    }
}
