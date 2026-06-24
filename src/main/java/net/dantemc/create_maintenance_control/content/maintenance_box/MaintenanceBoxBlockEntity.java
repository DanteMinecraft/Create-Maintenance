package net.dantemc.create_maintenance_control.content.maintenance_box;

import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import net.dantemc.create_maintenance_control.CreateMaintenance;
import net.dantemc.create_maintenance_control.railway.MaintenanceEntry;
import net.dantemc.create_maintenance_control.railway.OfflineStationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MaintenanceBoxBlockEntity extends SyncedBlockEntity {

    public MaintenanceBoxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private String stationFilter = "";
    private MaintenanceRedstoneMode redstoneMode = MaintenanceRedstoneMode.UNPOWERED_ACTIVE;

    public String getStationFilter() {
        return stationFilter;
    }

    public MaintenanceRedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void registerStation() {
        Level level = getLevel();

        if (level == null || level.isClientSide)
            return;

        GlobalStation station = StationUtils.findNearbyStation(level, getBlockPos());
        if (station == null)
            return;

        boolean powered = getBlockState().getValue(MaintenanceBoxBlock.POWERED);

        MaintenanceEntry entry = OfflineStationManager.ensureBoxEntry(level, getBlockPos(), station.name);

        this.stationFilter = entry.stationFilter();
        this.redstoneMode = entry.redstoneMode();

        OfflineStationManager.refreshBox(level, getBlockPos(), powered);

        setChanged();
        notifyUpdate();
    }

    public void applySettings(String stationFilter, MaintenanceRedstoneMode redstoneMode) {
        Level level = getLevel();
        if (level == null || level.isClientSide)
            return;

        this.stationFilter = stationFilter;
        this.redstoneMode = redstoneMode;

        OfflineStationManager.updateBoxSettings(level, getBlockPos(), stationFilter, redstoneMode);

        boolean powered = getBlockState().getValue(MaintenanceBoxBlock.POWERED);
        OfflineStationManager.refreshBox(level, getBlockPos(), powered);

        setChanged();
        notifyUpdate();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        Level level = getLevel();

        if (level == null || level.isClientSide)
            return;

        CreateMaintenance.debug("Scheduling station registration");

        level.scheduleTick(getBlockPos(), getBlockState().getBlock(), 100);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putString("StationFilter", stationFilter);
        tag.putString("RedstoneMode", redstoneMode.name());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        stationFilter = tag.getString("StationFilter");

        try {
            redstoneMode = MaintenanceRedstoneMode.valueOf(tag.getString("RedstoneMode"));
        } catch (IllegalArgumentException e) {
            redstoneMode = MaintenanceRedstoneMode.UNPOWERED_ACTIVE;
        }
    }
}
