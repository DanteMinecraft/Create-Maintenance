package net.dantemc.create_maintenance_control.railway;

import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceRedstoneMode;

public record MaintenanceEntry(String stationFilter, boolean shouldSkip, MaintenanceRedstoneMode redstoneMode, boolean skipDownstream) {
}