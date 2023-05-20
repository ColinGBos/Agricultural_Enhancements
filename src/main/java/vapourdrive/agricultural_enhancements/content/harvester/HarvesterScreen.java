package vapourdrive.agricultural_enhancements.content.harvester;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.content.base.AbstractBaseMachineScreen;

import java.util.List;

public class HarvesterScreen extends AbstractBaseMachineScreen<HarvesterContainer> {
    protected final HarvesterContainer machineContainer;

    public HarvesterScreen(HarvesterContainer container, Inventory inv, Component name) {
        super(container, inv, name, "harvester", 12, 8, 158, 6, 1);
        this.machineContainer = container;
    }

    @Override
    protected void getAdditionalInfoHover(List<Component> hoveringText) {
        super.getAdditionalInfoHover(hoveringText);
        if(ConfigSettings.HARVESTER_NON_DESTRUCTIVE_HARVESTING.get()) {
            hoveringText.add(Component.translatable("agriculturalenhancements.harvester.wrench").withStyle(ChatFormatting.GOLD));
        }
        hoveringText.add(Component.translatable("agriculturalenhancements.harvester.nondestructive."+this.machineContainer.isNonDestructive()).withStyle(ChatFormatting.GREEN));
    }
}
