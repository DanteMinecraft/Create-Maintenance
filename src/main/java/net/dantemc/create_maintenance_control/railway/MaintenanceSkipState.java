package net.dantemc.create_maintenance_control.railway;

public record MaintenanceSkipState(boolean anyMatch, boolean allSkipped, boolean skipDownstream) {
}
