package vapourdrive.agricultural_enhancements.modules.fertilizer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.modules.base.AbstractBaseMachineContainer;
import vapourdrive.agricultural_enhancements.modules.base.AbstractBaseMachineScreen;
import vapourdrive.agricultural_enhancements.modules.base.slots.AbstractMachineSlot;

import java.util.ArrayList;
import java.util.List;

public class FertilizerProducerScreen extends AbstractBaseMachineScreen<FertilizerProducerContainer> {

    protected final FertilizerProducerContainer machineContainer;

    public FertilizerProducerScreen(FertilizerProducerContainer container, Inventory inv, Component name) {
        super(container, inv, name, "fertilizer_processor", 12, 8, 158, 6, 1, true);
        machineContainer = container;
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrixStack, int mouseX, int mouseY) {
        if(AgriculturalEnhancements.debugMode) {
            int horStart = -100;
            drawString(matrixStack, Minecraft.getInstance().font, "N: " + menu.getElementStored(FertilizerProducerData.Data.N), horStart, 10, 0xffffff);
            drawString(matrixStack, Minecraft.getInstance().font, "P: " + menu.getElementStored(FertilizerProducerData.Data.P), horStart, 20, 0xffffff);
            drawString(matrixStack, Minecraft.getInstance().font, "K: " + menu.getElementStored(FertilizerProducerData.Data.K), horStart, 30, 0xffffff);

        }
        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);

        FertilizerProducerData.Data[] elements = {FertilizerProducerData.Data.N, FertilizerProducerData.Data.P, FertilizerProducerData.Data.K};
        int i = 0;
        for(FertilizerProducerData.Data element:elements) {
            int m = (int) (this.machineContainer.getElementPercentage(element) * (46));
            this.blit(matrixStack, this.leftPos + 67+(10*i), this.topPos + 19 + 46 - m, 176+(6*i), 46 + 46 - m, 6, m);
            i++;
        }
    }

    @Override
    protected void renderTooltip(@NotNull PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack, mouseX, mouseY);

        boolean notCarrying = this.menu.getCarried().isEmpty();

        List<Component> hoveringText = new ArrayList<>();

        FertilizerProducerData.Data[] elements = {FertilizerProducerData.Data.N, FertilizerProducerData.Data.P, FertilizerProducerData.Data.K};
        int i = 0;
        for(FertilizerProducerData.Data element:elements) {
            if (notCarrying && isInRect(this.leftPos + 67+(10*i), this.topPos + 19, 6, 46, mouseX, mouseY)) {
                int elementValue = machineContainer.getElementStored(element) / 80;
                hoveringText.add(Component.literal(element.name()+": ").append(df.format(elementValue) + "/" + df.format(this.machineContainer.getMaxElement() / 80)));
            }
            i++;
        }

        // If the mouse is over the experience bar, add hovering text

        // If hoveringText is not empty draw the hovering text.  Otherwise, use vanilla to render tooltip for the slots
        if (!hoveringText.isEmpty()) {
            renderComponentTooltip(matrixStack, hoveringText, mouseX, mouseY);
        }
    }



}
