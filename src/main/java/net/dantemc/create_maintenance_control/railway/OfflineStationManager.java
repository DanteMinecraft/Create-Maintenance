package net.dantemc.create_maintenance_control.railway;

import com.simibubi.create.content.trains.station.GlobalStation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.*;

public class OfflineStationManager {
    public static final Map<UUID, BlockPos> STATION_BOXES = new HashMap<>();

    //temp
    public static boolean isStationSkipped(Level level, GlobalStation station) {
        return false;
    }
}
