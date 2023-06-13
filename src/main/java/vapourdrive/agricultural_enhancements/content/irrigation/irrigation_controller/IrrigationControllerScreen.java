package vapourdrive.agricultural_enhancements.content.irrigation.irrigation_controller;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.vapourware.shared.base.AbstractBaseMachineScreen;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

public class IrrigationControllerScreen extends AbstractBaseMachineScreen<IrrigationControllerContainer> {

    public IrrigationControllerScreen(IrrigationControllerContainer container, Inventory inv, Component name) {
        super(container, inv, name, new DeferredComponent(AgriculturalEnhancements.MODID, "irrigation_controller"), 43, 7, 46, 126, 6, 32, false);
    }
}
