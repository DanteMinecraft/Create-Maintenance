package net.dantemc.create_maintenance_control.railway;

import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceRedstoneMode;
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
            MaintenanceRedstoneMode redstoneMode;

            try {
                redstoneMode = MaintenanceRedstoneMode.valueOf(entryTag.getString("RedstoneMode"));
            } catch (IllegalArgumentException e) {
                redstoneMode = MaintenanceRedstoneMode.UNPOWERED_ACTIVE;
            }

            boolean skipDownstream = entryTag.getBoolean("SkipDownstream");

            MaintenanceEntry entry = new MaintenanceEntry(stationFilter, shouldSkip, redstoneMode, skipDownstream);
            data.entries.put(boxPos, entry);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag entryList = new ListTag();

        for (BlockPos boxPos : entries.keySet()) {
            MaintenanceEntry entry = entries.get(boxPos);

            CompoundTag entryTag = new CompoundTag();

            entryTag.putInt("BoxX", boxPos.getX());
            entryTag.putInt("BoxY", boxPos.getY());
            entryTag.putInt("BoxZ", boxPos.getZ());

            entryTag.putString("StationFilter", entry.stationFilter());
            entryTag.putBoolean("ShouldSkip", entry.shouldSkip());
            entryTag.putString("RedstoneMode", entry.redstoneMode().name());
            entryTag.putBoolean("SkipDownstream", entry.skipDownstream());


            entryList.add(entryTag);
        }

        tag.put("Entries", entryList);
        return tag;
    }
}
