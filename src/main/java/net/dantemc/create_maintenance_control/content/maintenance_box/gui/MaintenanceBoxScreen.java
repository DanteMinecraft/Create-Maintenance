package net.dantemc.create_maintenance_control.content.maintenance_box.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.dantemc.create_maintenance_control.CreateMaintenance;
import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceBoxBlockEntity;
import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceRedstoneMode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class MaintenanceBoxScreen extends AbstractSimiScreen {

    private final MaintenanceBoxGuiTexture background = MaintenanceBoxGuiTexture.MAINTENANCE_BOX_INTERFACE;
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
        new ModularGuiLineBuilder(font, stationFilterLine, x + 24, y + 32)
                .addTextInput(0, 160, (editBox, tooltip) -> {
                    editBox.setMaxLength(64);
                    tooltip.withTooltip(ImmutableList.of(Component.translatable("gui.maintenance_box.station_filter.tooltip")
                                    .withStyle(s -> s.withColor(0x5391E1)),
                            CreateLang.translateDirect("gui.schedule.lmb_edit")
                                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)));
                }, "StationFilter");

        // redstone mode selector
        redstoneModeLine = new ModularGuiLine();
        new ModularGuiLineBuilder(font, redstoneModeLine, x + 24, y + 72)
                .addSelectionScrollInput(0, 160, (input, label) -> {
                    input.forOptions(List.of(
                            Component.translatable("gui.maintenance_box.redstone_mode.default"),//Unpowered = Maintenance
                            Component.translatable("gui.maintenance_box.redstone_mode.inverted") //Powered = Maintenance
                    ));
                }, "RedstoneMode");

        // Skip downstream stations selector
        skipDownstreamLine = new ModularGuiLine();
        new ModularGuiLineBuilder(font, skipDownstreamLine, x + 24, y + 112)
                .addSelectionScrollInput(0, 160, (input, label) -> {
                    input.forOptions(List.of(
                            Component.translatable("gui.maintenance_box.skip_downstream_stations.disabled"),
                            Component.translatable("gui.maintenance_box.skip_downstream_stations.enabled")
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

        graphics.drawString(font, Component.translatable("gui.maintenance_box.station_filter"), guiLeft + 24, guiTop + 18, 0xB8B8B8, false);
        graphics.drawString(font, Component.translatable("gui.maintenance_box.redstone_mode"), guiLeft + 24, guiTop + 58, 0xB8B8B8, false);
        graphics.drawString(font, Component.translatable("gui.maintenance_box.skip_downstream_stations"), guiLeft + 24, guiTop + 98, 0xB8B8B8, false);

        PoseStack ms = graphics.pose();
        ms.pushPose();
        TransformStack.of(ms)
                .pushPose()
                .translate(x + background.getWidth() + 4, y + background.getHeight() + 4, 100)
                .scale(40)
                .rotateXDegrees(-22)
                .rotateYDegrees(63);
        GuiGameElement.of(blockEntity.getBlockState()
                        .setValue(DisplayLinkBlock.FACING, Direction.UP))
                .render(graphics);
        ms.popPose();
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