package net.dantemc.create_maintenance_control.maintenance_box;

import com.simibubi.create.content.trains.station.GlobalStation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OfflineStationManager {
    public static final Set<UUID> OFFLINE_STATIONS = new HashSet<>();

    public static boolean isOffline(UUID stationId) {
        return OFFLINE_STATIONS.contains(stationId);
    }

    public static void setOffline(UUID stationId) {
        OFFLINE_STATIONS.add(stationId);
    }

    public static void setOnline(UUID stationId) {
        OFFLINE_STATIONS.remove(stationId);
    }

    public static boolean shouldSkip(Level level, GlobalStation station) {

        if (!isOffline(station.getId()))
            return false;

        BlockPos pos = StationUtils.findNearbyMaintenanceBox(level, station);

        if (pos == null)
            return false;

        BlockState state = level.getBlockState(pos);

        return !state.getValue(MaintenanceBoxBlock.POWERED);
    }
}
