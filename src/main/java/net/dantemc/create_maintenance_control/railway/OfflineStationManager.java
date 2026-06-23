package net.dantemc.create_maintenance_control.railway;

import com.simibubi.create.content.trains.station.GlobalStation;
import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceBoxBlock;
import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceRedstoneMode;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Objects;


public class OfflineStationManager {

    private static MaintenanceSavedData getData(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            throw new IllegalStateException("Tried to access maintenance data on the client");
        }

        ServerLevel overworld = serverLevel.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(MaintenanceSavedData.factory(), MaintenanceSavedData.dataName());
    }

    public static MaintenanceEntry getEntry(Level level, BlockPos boxPos) {
        return getData(level).getEntries().get(boxPos);
    }
    public static void setEntry(Level level, BlockPos boxPos, MaintenanceEntry entry) {
        getData(level).setEntry(boxPos, entry);
    }

    public static void refreshBox(Level level, BlockPos boxPos, String defaultStationFilter, boolean powered) {
        MaintenanceEntry entry = getEntry(level, boxPos);

        if (entry == null) {
            entry = new MaintenanceEntry(defaultStationFilter,
                    false, MaintenanceRedstoneMode.UNPOWERED_ACTIVE, false
            );
        }

        boolean shouldSkip = entry.redstoneMode().isMaintenanceActive(powered);

        MaintenanceEntry updated = new MaintenanceEntry(entry.stationFilter(), shouldSkip, entry.redstoneMode(), entry.skipDownstream());

        setEntry(level, boxPos, updated);
    }

    public static void updateBoxSettings(Level level, BlockPos boxPos, String stationFilter, MaintenanceRedstoneMode redstoneMode) {
        MaintenanceEntry current = getEntry(level, boxPos);
        if (current == null)
            return;

        boolean powered = level.getBlockState(boxPos).getValue(MaintenanceBoxBlock.POWERED);
        boolean shouldSkip = redstoneMode.isMaintenanceActive(powered);

        MaintenanceEntry updated = new MaintenanceEntry(stationFilter, shouldSkip, redstoneMode, current.skipDownstream());

        setEntry(level, boxPos, updated);
    }

    public static void unregisterBox(Level level, BlockPos boxPos) {
        getData(level).removeEntry(boxPos);
    }

    public static boolean isStationSkipped(Level level, GlobalStation station) {
        String stationName = station.name;

        for (MaintenanceEntry entry : getData(level).getEntries().values()) {
            if (entry.shouldSkip() && Objects.equals(entry.stationFilter(), stationName)) {
                return true;
            }
        }
        return false;
    }
}
