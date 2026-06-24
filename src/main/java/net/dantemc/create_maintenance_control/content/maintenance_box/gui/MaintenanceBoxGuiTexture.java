package net.dantemc.create_maintenance_control.content.maintenance_box.gui;

import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.element.ScreenElement;
import net.dantemc.create_maintenance_control.CreateMaintenance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public enum MaintenanceBoxGuiTexture implements ScreenElement, TextureSheetSegment {
    MAINTENANCE_BOX_INTERFACE("maintenance_box", 235, 162);

    public final ResourceLocation location;
    private final int width;
    private final int height;
    private final int startX;
    private final int startY;

    MaintenanceBoxGuiTexture(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    MaintenanceBoxGuiTexture(String location, int startX, int startY, int width, int height) {
        this(CreateMaintenance.MODID, location, startX, startY, width, height);
    }

    MaintenanceBoxGuiTexture(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = ResourceLocation.fromNamespaceAndPath(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    public ResourceLocation getLocation() {
        return location;
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height);
    }

    @Override
    public int getStartX() {
        return startX;
    }

    @Override
    public int getStartY() {
        return startY;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
