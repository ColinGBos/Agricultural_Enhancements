package vapourdrive.agricultural_enhancements.modules.irrigation.irrigation_controller;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.modules.base.AbstractBaseMachineContainer;
import vapourdrive.agricultural_enhancements.modules.slots.AbstractMachineSlot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class IrrigationControllerScreen extends AbstractContainerScreen<IrrigationControllerContainer> {
    private final AbstractBaseMachineContainer container;

    private final ResourceLocation GUI = new ResourceLocation(AgriculturalEnhancements.MODID, "textures/gui/irrigation_controller_gui.png");

    final static int FUEL_XPOS = 43;
    final static int FUEL_YPOS = 7;
    final static int FUEL_ICONX = 176;   // texture position of flame icon [u,v]
    final static int FUEL_ICONY = 0;
    final static int FUEL_HEIGHT = 47;
    final static int FUEL_WIDTH = 8;

    DecimalFormat df = new DecimalFormat("#,###");

    public IrrigationControllerScreen(IrrigationControllerContainer container, Inventory inv, Component name) {
        super(container, inv, name);
        this.container = container;
        this.titleLabelX = 36;
        this.titleLabelY = -10;
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrixStack, int mouseX, int mouseY) {
        if (AgriculturalEnhancements.debugMode) {
            int horStart = -100;
//            drawString(matrixStack, Minecraft.getInstance().font, "Cook: " + menu.getFurnaceData(0), horStart, 30, 0xffffff);
//            drawString(matrixStack, Minecraft.getInstance().font, "MaxCook: " + menu.getFurnaceData(1), horStart, 40, 0xffffff);
//            drawString(matrixStack, Minecraft.getInstance().font, "Exp: " + menu.getFurnaceData(2), horStart, 50, 0xffffff);
//            drawString(matrixStack, Minecraft.getInstance().font, String.format("Cooking: %.2f", menu.getCookProgress()), horStart, 60, 0xffffff);
        }
        this.font.draw(matrixStack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, 16777215);
//        this.font.draw(matrixStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        int guiLeft = this.leftPos;
        int guiTop = this.topPos;

        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

        int m = (int) (container.getFuelPercentage() * (FUEL_HEIGHT));
        this.blit(matrixStack, guiLeft + FUEL_XPOS, guiTop + FUEL_YPOS + FUEL_HEIGHT - m, FUEL_ICONX, FUEL_ICONY + FUEL_HEIGHT - m, FUEL_WIDTH, m);

    }

    @Override
    protected void renderTooltip(@NotNull PoseStack matrixStack, int mouseX, int mouseY) {
        //if (!this.minecraft.player.inventory.getCarried().isEmpty()) return;  // no tooltip if the player is dragging something

        assert this.minecraft != null;
        assert this.minecraft.player != null;
        boolean notCarrying = this.minecraft.player.inventoryMenu.getCarried().isEmpty();

        List<Component> hoveringText = new ArrayList<>();

        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem() && this.hoveredSlot instanceof AbstractMachineSlot machineSlot) {
            String title = machineSlot.getTitle();
            if (title != null) {
                hoveringText.add(Component.translatable(machineSlot.getTitle()).withStyle(ChatFormatting.GREEN));
            }
        }

        // If the mouse is over the experience bar, add hovering text
        if (notCarrying && isInRect(this.leftPos + FUEL_XPOS, this.topPos + FUEL_YPOS, FUEL_WIDTH, FUEL_HEIGHT, mouseX, mouseY)) {
            int fuel = container.getFuelStored() / 100;
            hoveringText.add(Component.literal("Fuel: ").append(df.format(fuel) + "/" + df.format(container.getMaxFuel() / 100)));
        }

        // If hoveringText is not empty draw the hovering text.  Otherwise, use vanilla to render tooltip for the slots
        if (!hoveringText.isEmpty()) {
            renderComponentTooltip(matrixStack, hoveringText, mouseX, mouseY);
        } else {
            super.renderTooltip(matrixStack, mouseX, mouseY);
        }
    }

    // Returns true if the given x,y coordinates are within the given rectangle
    public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }

}
