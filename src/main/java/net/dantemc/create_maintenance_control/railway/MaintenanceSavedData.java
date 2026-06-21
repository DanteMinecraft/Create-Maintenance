package net.dantemc.create_maintenance_control.railway;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class MaintenanceSavedData extends SavedData {

    private static final String DATA_NAME = "create_maintenance_rules";

    private final Map<BlockPos, MaintenanceEntry> entries = new HashMap<>();

    public static Factory<MaintenanceSavedData> factory() {
        return new Factory<>(
                MaintenanceSavedData::new,
                MaintenanceSavedData::load
        );
    }

    public static String dataName() {
        return DATA_NAME;
    }

    public void setEntry(BlockPos boxPos, MaintenanceEntry entry){
        entries.put(boxPos, entry);
        setDirty();
    }

    public void removeEntry(BlockPos boxPos){
        entries.remove(boxPos);
        setDirty();
    }

    public Map<BlockPos, MaintenanceEntry> getEntries() {
        return entries;
    }

    public static MaintenanceSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        MaintenanceSavedData data = new MaintenanceSavedData();

        ListTag entryList = tag.getList("Entries", Tag.TAG_COMPOUND);

        for (int i = 0; i < entryList.size(); i++) {
            CompoundTag entryTag = entryList.getCompound(i);

            int x = entryTag.getInt("BoxX");
            int y = entryTag.getInt("BoxY");
            int z = entryTag.getInt("BoxZ");

            BlockPos boxPos = new BlockPos(x, y, z);

            String stationFilter = entryTag.getString("StationFilter");
            boolean shouldSkip = entryTag.getBoolean("ShouldSkip");

            MaintenanceEntry entry = new MaintenanceEntry(stationFilter, shouldSkip);
            data.entries.put(boxPos, entry);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        return compoundTag;
    }
}
