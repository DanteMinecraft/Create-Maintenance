package net.dantemc.create_maintenance_control.content.maintenance_box.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.Force;
import net.createmod.catnip.animation.PhysicalFloat;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.dantemc.create_maintenance_control.CreateMaintenance;
import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceBoxBlockEntity;
import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceRedstoneMode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

import static net.createmod.catnip.config.ui.ConfigScreen.shadowState;

public class MaintenanceBoxScreen extends AbstractSimiScreen {

    private final MaintenanceBoxGuiTexture background = MaintenanceBoxGuiTexture.MAINTENANCE_BOX_INTERFACE;
    private final MaintenanceBoxBlockEntity blockEntity;

    private IconButton confirmButton;

    private ModularGuiLine stationFilterLine;

    private SelectionScrollInput redstoneModeSelector;
    private Label redstoneModeLabel;

    private SelectionScrollInput skipDownstreamSelector;
    private Label skipDownstreamLabel;

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

        // station filter textbox
        stationFilterLine = new ModularGuiLine();
        new ModularGuiLineBuilder(font, stationFilterLine, x + 24, y + 60)
                .addTextInput(0, 160, (editBox, tooltip) -> {
                    editBox.setMaxLength(64);
                    tooltip.withTooltip(ImmutableList.of(Component.translatable("gui.maintenance_box.station_filter.tooltip")
                                    .withStyle(s -> s.withColor(0x5391E1)),
                            CreateLang.translateDirect("gui.schedule.lmb_edit")
                                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)));
                }, "StationFilter");

        // redstone mode selector
        redstoneModeLabel = new Label(x + 29, y + 85, Component.empty()).withShadow();

        redstoneModeSelector = (SelectionScrollInput) new SelectionScrollInput(x + 24, y + 85, 160, 18)
                .forOptions(List.of(
                        Component.translatable("gui.maintenance_box.redstone_mode.default"),
                        Component.translatable("gui.maintenance_box.redstone_mode.inverted")
                ))
                .writingTo(redstoneModeLabel)
                .titled(Component.translatable("gui.maintenance_box.redstone_mode.tooltip"))
                .setState(blockEntity.getRedstoneMode().ordinal());

        addRenderableWidget(redstoneModeSelector);
        addRenderableWidget(redstoneModeLabel);

        // Skip downstream stations selector
        skipDownstreamLabel = new Label(x + 29, y + 110, Component.empty()).withShadow();

        skipDownstreamSelector = (SelectionScrollInput) new SelectionScrollInput(x + 24, y + 110, 160, 18)
                .forOptions(List.of(
                        Component.translatable("gui.maintenance_box.skip_downstream_stations.false"),
                        Component.translatable("gui.maintenance_box.skip_downstream_stations.true")
                ))
                .writingTo(skipDownstreamLabel)
                .titled(Component.translatable("gui.maintenance_box.skip_downstream_stations.tooltip"))
                .setState(blockEntity.shouldSkipDownstream() ? 1 : 0);

        addRenderableWidget(skipDownstreamSelector);
        addRenderableWidget(skipDownstreamLabel);

        // push initial value from configData into the widget
        stationFilterLine.loadValues(configData, this::addRenderableWidget, this::addRenderableOnly);

        // Confirm button
        confirmButton = new IconButton(x + background.getWidth() - 33, y + background.getHeight() - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onConfirm);
        addRenderableWidget(confirmButton);
    }

    public static final PhysicalFloat cogSpin = PhysicalFloat.create().withLimit(10f).withDrag(0.3).addForce(new Force.Static(.2f));

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        background.render(graphics, guiLeft, guiTop);

        int x = guiLeft;
        int y = guiTop;

        MutableComponent header = Component.translatable("gui.maintenance_box.title");
        graphics.drawString(font, header, (x + background.getWidth() / 2 - font.width(header) / 2) - 2, y + 4, 0x592424, false);

        //cog
        partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();

        poseStack.translate(x + background.getWidth() / 2f - 18, y + 50, 0);
        poseStack.scale(28, 28, 1);
        GuiGameElement.of(shadowState)
                .rotateBlock(22.5, cogSpin.getValue(partialTicks), 22.5)
                .render(graphics);

        poseStack.popPose();

        //block model
        PoseStack ms2 = graphics.pose();
        ms2.pushPose();
        TransformStack.of(ms2)
                .pushPose()
                .translate(x + background.getWidth() + 4, y + background.getHeight() + 4, 100)
                .scale(40)
                .rotateXDegrees(-22)
                .rotateYDegrees(63);
        GuiGameElement.of(blockEntity.getBlockState()
                        .setValue(DisplayLinkBlock.FACING, Direction.UP))
                .render(graphics);
        ms2.popPose();
    }

    @Override
    public void tick() {
        cogSpin.tick();
    }

    private void onConfirm() {

        CompoundTag tag = new CompoundTag();
        stationFilterLine.saveValues(tag);

        String stationFilter = tag.getString("StationFilter");
        MaintenanceRedstoneMode redstoneMode = MaintenanceRedstoneMode.values()[redstoneModeSelector.getState()];
        boolean skipDownstream = skipDownstreamSelector.getState() == 1;

        CatnipServices.NETWORK.sendToServer(new MaintenanceBoxConfigurationPacket(blockEntity.getBlockPos(), stationFilter,
                redstoneMode, skipDownstream));

        CreateMaintenance.debug("Station filter: " + stationFilter);
        CreateMaintenance.debug("Redstone mode: " + redstoneMode);
        CreateMaintenance.debug("Skip downstream: " + skipDownstream);

        onClose();
    }
}