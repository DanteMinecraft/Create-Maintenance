package net.dantemc.create_maintenance_control.content.maintenance_box.gui;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.platform.CatnipServices;
import net.dantemc.create_maintenance_control.CreateMaintenance;
import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceBoxBlockEntity;
import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceRedstoneMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class MaintenanceBoxScreen extends AbstractSimiScreen {

    private final AllGuiTextures background = AllGuiTextures.DATA_GATHERER;
    private final MaintenanceBoxBlockEntity blockEntity;

    private IconButton confirmButton;

    private ModularGuiLine stationFilterLine;
    private ModularGuiLine redstoneModeLine;
    private ModularGuiLine skipDownstreamLine;

    private CompoundTag configData;

    public MaintenanceBoxScreen(MaintenanceBoxBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    protected void init() {
        setWindowSize(background.getWidth(), background.getHeight());
        super.init();

        int x = guiLeft;
        int y = guiTop;

        configData = new CompoundTag();
        configData.putString("StationFilter", blockEntity.getStationFilter());
        configData.putInt("RedstoneMode", blockEntity.getRedstoneMode().ordinal());
        configData.putBoolean("SkipDownstream", blockEntity.shouldSkipDownstream());

        // station filter textbox
        stationFilterLine = new ModularGuiLine();
        new ModularGuiLineBuilder(font, stationFilterLine, x + 24, y + 28)
                .addTextInput(0, 160, (editBox, tooltip) -> {
                    editBox.setMaxLength(64);
                    tooltip.withTooltip(List.of(
                            Component.literal("Station Name Filter")
                    ));
                }, "StationFilter");

        // redstone mode selector
        redstoneModeLine = new ModularGuiLine();
        new ModularGuiLineBuilder(font, redstoneModeLine, x + 24, y + 68)
                .addSelectionScrollInput(0, 160, (input, label) -> {
                    input.forOptions(List.of(
                            Component.literal("Unpowered = Maintenance"),
                            Component.literal("Powered = Maintenance")
                    ));
                }, "RedstoneMode");

        // Skip downstream stations selector
        skipDownstreamLine = new ModularGuiLine();
        new ModularGuiLineBuilder(font, skipDownstreamLine, x + 24, y + 108)
                .addSelectionScrollInput(0, 160, (input, label) -> {
                    input.forOptions(List.of(
                            Component.literal("Disabled"),
                            Component.literal("Enabled")
                    ));
                }, "SkipDownstream");

        // push initial values from configData into the widgets
        stationFilterLine.loadValues(configData, this::addRenderableWidget, this::addRenderableOnly);
        redstoneModeLine.loadValues(configData, this::addRenderableWidget, this::addRenderableOnly);
        skipDownstreamLine.loadValues(configData, this::addRenderableWidget, this::addRenderableOnly);

        // Confirm button
        confirmButton = new IconButton(x + background.getWidth() - 33, y + background.getHeight() - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onConfirm);
        addRenderableWidget(confirmButton);
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        background.render(graphics, guiLeft, guiTop);

        int x = guiLeft;
        int y = guiTop;

        MutableComponent header = Component.translatable("gui.maintenance_box.title");
        graphics.drawString(font, header, x + background.getWidth() / 2 - font.width(header) / 2, y + 4, 0x592424, false);

        graphics.drawString(font, "Station Filter", guiLeft + 24, guiTop + 18, 0x575F7A, false);
        graphics.drawString(font, "Redstone Mode", guiLeft + 24, guiTop + 58, 0x575F7A, false);
        graphics.drawString(font, "Skip Downstream Stations", guiLeft + 24, guiTop + 98, 0x575F7A, false);
    }

    private void onConfirm() {

        CompoundTag tag = new CompoundTag();

        stationFilterLine.saveValues(tag);
        redstoneModeLine.saveValues(tag);
        skipDownstreamLine.saveValues(tag);

        String stationFilter = tag.getString("StationFilter");

        int redstoneModeIndex = tag.getInt("RedstoneMode");
        MaintenanceRedstoneMode redstoneMode = MaintenanceRedstoneMode.values()[redstoneModeIndex];

        boolean skipDownstream = tag.getBoolean("SkipDownstream");

        CatnipServices.NETWORK.sendToServer(new MaintenanceBoxConfigurationPacket(blockEntity.getBlockPos(), stationFilter,
                redstoneMode, skipDownstream));

        CreateMaintenance.debug("Station filter: " + stationFilter);
        CreateMaintenance.debug("Redstone mode: " + redstoneMode);
        CreateMaintenance.debug("Skip downstream: " + skipDownstream);

        onClose();
    }
}