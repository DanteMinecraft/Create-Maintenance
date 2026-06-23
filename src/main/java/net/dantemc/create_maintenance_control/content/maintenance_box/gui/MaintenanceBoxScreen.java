package net.dantemc.create_maintenance_control.content.maintenance_box.gui;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceBoxBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.List;

public class MaintenanceBoxScreen extends AbstractSimiScreen {

    private final AllGuiTextures background = AllGuiTextures.DATA_GATHERER;
    private final MaintenanceBoxBlockEntity blockEntity;

    private IconButton confirmButton;
    private EditBox stationFilterBox;
    private SelectionScrollInput redstoneModeSelector;

    public MaintenanceBoxScreen(MaintenanceBoxBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    protected void init() {
        setWindowSize(background.getWidth(), background.getHeight());
        super.init();

        int x = guiLeft;
        int y = guiTop;

        // placeholders
        String stationFilter = "";
        int redstoneModeIndex = 0;

        // station filter textbox
        stationFilterBox = new EditBox(font, x + 24, y + 28, 120, 18, Component.literal("Station Filter"));
        stationFilterBox.setValue(stationFilter);
        stationFilterBox.setMaxLength(64);
        addRenderableWidget(stationFilterBox);

        // redstone mode selector
        redstoneModeSelector = (SelectionScrollInput) new SelectionScrollInput(x + 24, y + 70, 120, 18)
                .forOptions(List.of(
                        Component.literal("Unpowered = Maintenance"),
                        Component.literal("Powered = Maintenance")
                ));
        redstoneModeSelector.setState(redstoneModeIndex);
        addRenderableWidget(redstoneModeSelector);

        // Confirm button
        confirmButton = new IconButton(x + background.getWidth() - 33, y + background.getHeight() - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onConfirm);
        addRenderableWidget(confirmButton);
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        background.render(graphics, guiLeft, guiTop);

        graphics.drawString(font, "Maintenance Box", guiLeft + 24, guiTop + 10, 0x404040, false);
        graphics.drawString(font, "Station Filter", guiLeft + 24, guiTop + 18, 0x575F7A, false);
        graphics.drawString(font, "Redstone Mode", guiLeft + 24, guiTop + 58, 0x575F7A, false);
    }

    private void onConfirm() {
        onClose();
    }
}