package net.dantemc.create_maintenance.marker;

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
}
