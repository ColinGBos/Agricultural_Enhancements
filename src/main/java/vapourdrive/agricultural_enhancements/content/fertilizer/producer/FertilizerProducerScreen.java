package vapourdrive.agricultural_enhancements.content.fertilizer.producer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.vapourware.shared.base.AbstractBaseMachineScreen;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

import java.util.ArrayList;
import java.util.List;

public class FertilizerProducerScreen extends AbstractBaseMachineScreen<FertilizerProducerContainer> {

    protected final FertilizerProducerContainer machineContainer;

    public FertilizerProducerScreen(FertilizerProducerContainer container, Inventory inv, Component name) {
        super(container, inv, name, new DeferredComponent(AgriculturalEnhancements.MODID, "fertilizer_producer"), true);
        machineContainer = container;
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);

        FertilizerProducerData.Data[] elements = {FertilizerProducerData.Data.N, FertilizerProducerData.Data.P, FertilizerProducerData.Data.K};
        int i = 0;
        for (FertilizerProducerData.Data element : elements) {
            int m = (int) (this.machineContainer.getElementPercentage(element) * (46));
            this.blit(matrixStack, this.leftPos + 67 + (10 * i), this.topPos + 19 + 46 - m, 176 + (6 * i), 46 + 46 - m, 6, m);
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
        for (FertilizerProducerData.Data element : elements) {
            if (notCarrying && isInRect(this.leftPos + 67 + (10 * i), this.topPos + 19, 6, 46, mouseX, mouseY)) {
                int elementValue = machineContainer.getElementStored(element);
                hoveringText.add(Component.translatable("agriculturalenhancements." + element.name().toLowerCase(), df.format(elementValue) + "/" + df.format(this.machineContainer.getMaxElement())));
            }
            i++;
        }

        // If hoveringText is not empty draw the hovering text.  Otherwise, use vanilla to render tooltip for the slots
        if (!hoveringText.isEmpty()) {
            renderComponentTooltip(matrixStack, hoveringText, mouseX, mouseY);
        }
    }

    @Override
    protected void getAdditionalInfoHover(List<Component> hoveringText) {
        super.getAdditionalInfoHover(hoveringText);
        hoveringText.add(Component.translatable("agriculturalenhancements.fertilizer_producer.wrench").withStyle(ChatFormatting.GOLD));
    }


}
