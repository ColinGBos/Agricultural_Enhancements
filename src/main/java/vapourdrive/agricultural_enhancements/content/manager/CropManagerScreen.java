package vapourdrive.agricultural_enhancements.content.manager;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.vapourware.shared.base.AbstractBaseMachineScreen;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

import java.util.ArrayList;
import java.util.List;

public class CropManagerScreen extends AbstractBaseMachineScreen<CropManagerMenu> {
    protected final CropManagerMenu machineContainer;

    public CropManagerScreen(CropManagerMenu container, Inventory inv, Component name) {
        super(container, inv, name, new DeferredComponent(AgriculturalEnhancements.MODID, "crop_manager"), 12, 8, 46,
                158, 6, 1, true);
        machineContainer = container;
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        if (AgriculturalEnhancements.isDebugMode()) {
            int horStart = -100;
            graphics.drawString(this.font, "N: " + menu.getFertilizerStored(CropManagerData.Data.FERTILIZER), horStart, 10, 0xffffff);

        }
        super.renderLabels(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);
        int m = (int) (this.machineContainer.getFertilizerPercentage() * (46));
        graphics.blit(this.GUI, this.leftPos + 32, this.topPos + 8 + 46 - m, 176, 46 + 46 - m, 16, m);

    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);

        boolean notCarrying = this.menu.getCarried().isEmpty();

        List<Component> hoveringText = new ArrayList<>();

        if (notCarrying && isInRect(this.leftPos + 32, this.topPos + 8, 16, 46, mouseX, mouseY)) {
            int m = this.machineContainer.getFertilizerStored(CropManagerData.Data.FERTILIZER);
            hoveringText.add(Component.translatable("item.agriculturalenhancements.fertilizer").append(": ").append(df.format(m) + "/" + df.format(this.machineContainer.getMaxFertilizer())));
        }


        // If the mouse is over the experience bar, add hovering text

        // If hoveringText is not empty draw the hovering text.  Otherwise, use vanilla to render tooltip for the slots
        if (!hoveringText.isEmpty()) {
            graphics.renderComponentTooltip(this.font, hoveringText, mouseX, mouseY);
        }
    }

    @Override
    protected void getAdditionalInfoHover(List<Component> hoveringText) {
        super.getAdditionalInfoHover(hoveringText);
        hoveringText.add(Component.translatable("agriculturalenhancements.crop_manager.wrench").withStyle(ChatFormatting.GOLD));
    }
}
