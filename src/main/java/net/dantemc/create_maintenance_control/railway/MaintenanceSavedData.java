package net.dantemc.create_maintenance_control.railway;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class MaintenanceSavedData extends SavedData {

    private static final String DATA_NAME = "create_maintenance_control_station_data";

    private final Map<BlockPos, MaintenanceEntry> STATION_BOXES = new HashMap<>();

    public static String dataName() {
        return DATA_NAME;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        return null;
    }
}
