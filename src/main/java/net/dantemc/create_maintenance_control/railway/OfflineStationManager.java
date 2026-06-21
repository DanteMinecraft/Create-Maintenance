package net.dantemc.create_maintenance_control.railway;

import com.simibubi.create.content.trains.station.GlobalStation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Objects;


public class OfflineStationManager {

    private static MaintenanceSavedData getData(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            throw new IllegalStateException("Tried to access maintenance data on the client");
        }

        ServerLevel overworld = serverLevel.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(MaintenanceSavedData.factory(), MaintenanceSavedData.dataName());
    }

    public static void registerBox(Level level, BlockPos boxPos, String stationFilter, boolean shouldSkip) {
        MaintenanceEntry entry = new MaintenanceEntry(stationFilter, shouldSkip);
        getData(level).setEntry(boxPos, entry);
    }

    public static void unregisterBox(Level level, BlockPos boxPos) {
        getData(level).removeEntry(boxPos);
    }

    public static boolean isStationSkipped(Level level, GlobalStation station) {

        String stationName = station.name;

        Map<BlockPos, MaintenanceEntry> allData = getData(level).getEntries();

        for (BlockPos boxPos : allData.keySet()) {
            MaintenanceEntry entry = allData.get(boxPos);

            if (entry.shouldSkip()) {

                if (Objects.equals(entry.stationFilter(), stationName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
