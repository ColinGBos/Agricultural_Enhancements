package vapourdrive.agricultural_enhancements.content.harvester;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import vapourdrive.agricultural_enhancements.content.base.AbstractBaseMachineScreen;

public class HarvesterScreen extends AbstractBaseMachineScreen<HarvesterContainer> {
    public HarvesterScreen(HarvesterContainer container, Inventory inv, Component name) {
        super(container, inv, name, "harvester", 12, 8, 158, 6, 1);
    }
}
