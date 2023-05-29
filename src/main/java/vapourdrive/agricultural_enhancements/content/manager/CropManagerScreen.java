package vapourdrive.agricultural_enhancements.content.manager;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.content.base.AbstractBaseMachineScreen;

import java.util.ArrayList;
import java.util.List;

public class CropManagerScreen extends AbstractBaseMachineScreen<CropManagerContainer> {
    protected final CropManagerContainer machineContainer;

    public CropManagerScreen(CropManagerContainer container, Inventory inv, Component name) {
        super(container, inv, name, "crop_manager", 12, 8, 158, 6, 1, true);
        machineContainer = container;
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrixStack, int mouseX, int mouseY) {
        if (AgriculturalEnhancements.isDebugMode()) {
            int horStart = -100;
            drawString(matrixStack, Minecraft.getInstance().font, "N: " + menu.getFertilizerStored(CropManagerData.Data.FERTILIZER), horStart, 10, 0xffffff);

        }
        super.renderLabels(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
        int m = (int) (this.machineContainer.getFertilizerPercentage() * (46));
        this.blit(matrixStack, this.leftPos + 32, this.topPos + 8 + 46 - m, 176, 46 + 46 - m, 16, m);

    }

    @Override
    protected void renderTooltip(@NotNull PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack, mouseX, mouseY);

        boolean notCarrying = this.menu.getCarried().isEmpty();

        List<Component> hoveringText = new ArrayList<>();

        if (notCarrying && isInRect(this.leftPos + 32, this.topPos + 8, 16, 46, mouseX, mouseY)) {
            int m = this.machineContainer.getFertilizerStored(CropManagerData.Data.FERTILIZER);
            hoveringText.add(Component.translatable("item.agriculturalenhancements.fertilizer").append(": ").append(df.format(m) + "/" + df.format(this.machineContainer.getMaxFertilizer())));
        }


        // If the mouse is over the experience bar, add hovering text

        // If hoveringText is not empty draw the hovering text.  Otherwise, use vanilla to render tooltip for the slots
        if (!hoveringText.isEmpty()) {
            renderComponentTooltip(matrixStack, hoveringText, mouseX, mouseY);
        }
    }

    @Override
    protected void getAdditionalInfoHover(List<Component> hoveringText) {
        super.getAdditionalInfoHover(hoveringText);
        hoveringText.add(Component.translatable("agriculturalenhancements.crop_manager.wrench").withStyle(ChatFormatting.GOLD));
    }
}
