package vapourdrive.agricultural_enhancements.content.fertilizer.producer;

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

public class FertilizerProducerScreen extends AbstractBaseMachineScreen<FertilizerProducerMenu> {

    protected final FertilizerProducerMenu machineContainer;

    public FertilizerProducerScreen(FertilizerProducerMenu container, Inventory inv, Component name) {
        super(container, inv, name, new DeferredComponent(AgriculturalEnhancements.MODID, "fertilizer_producer"), 12, 8,
                46, 158, 6, 1,
                true);
        machineContainer = container;
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        if (AgriculturalEnhancements.isDebugMode()) {
            int horStart = -100;
            graphics.drawString(this.font, "N: " + menu.getElementStored(FertilizerProducerData.Data.N), horStart, 10, 0xffffff);
            graphics.drawString(this.font, "P: " + menu.getElementStored(FertilizerProducerData.Data.P), horStart, 20, 0xffffff);
            graphics.drawString(this.font, "K: " + menu.getElementStored(FertilizerProducerData.Data.K), horStart, 30, 0xffffff);

        }
        super.renderLabels(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);

        FertilizerProducerData.Data[] elements = {FertilizerProducerData.Data.N, FertilizerProducerData.Data.P, FertilizerProducerData.Data.K};
        int i = 0;
        for (FertilizerProducerData.Data element : elements) {
            int m = (int) (this.machineContainer.getElementPercentage(element) * (46));
            graphics.blit(this.GUI, this.leftPos + 67 + (10 * i), this.topPos + 19 + 46 - m, 176 + (6 * i), 46 + 46 - m
                    , 6, m);
            i++;
        }
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);

        boolean notCarrying = this.menu.getCarried().isEmpty();

        List<Component> hoveringText = new ArrayList<>();

        FertilizerProducerData.Data[] elements = {FertilizerProducerData.Data.N, FertilizerProducerData.Data.P, FertilizerProducerData.Data.K};
        int i = 0;
        for (FertilizerProducerData.Data element : elements) {
            if (notCarrying && isInRect(this.leftPos + 67 + (10 * i), this.topPos + 19, 6, 46, mouseX, mouseY)) {
                int elementValue = machineContainer.getElementStored(element);
                hoveringText.add(Component.translatable("agriculturalenhancements." + element.name().toLowerCase(), df.format(elementValue) + "/" + df.format(this.machineContainer.getMaxElement())));
            }
            i++;
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
        hoveringText.add(Component.translatable("agriculturalenhancements.fertilizer_producer.wrench").withStyle(ChatFormatting.GOLD));
    }


}
