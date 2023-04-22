package vapourdrive.agricultural_enhancements.modules.irrigation.irrigation_controller;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import vapourdrive.agricultural_enhancements.modules.base.AbstractBaseMachineScreen;

public class IrrigationControllerScreen extends AbstractBaseMachineScreen<IrrigationControllerContainer> {

    public IrrigationControllerScreen(IrrigationControllerContainer container, Inventory inv, Component name) {
        super(container, inv, name, "irrigation_controller", 43, 7, 126, 6, 32);
    }
}
