package net.dantemc.create_maintenance.marker;

import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import net.dantemc.create_maintenance.CreateMaintenance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MaintenanceMarkerBlockEntity extends BlockEntity {

    public MaintenanceMarkerBlockEntity(
            BlockPos pos,
            BlockState state) {

        super(
                CreateMaintenance.MAINTENANCE_MARKER_BE.get(),
                pos,
                state
        );
    }

    private GlobalStation findNearbyStation(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(pos.relative(dir));

            if (be instanceof StationBlockEntity stationBlock) {
                return stationBlock.getStation();
            }
        }
        return null;
    }

    public void registerStation() {
        Level level = getLevel();

        if (level == null) {
            return;
        }

        GlobalStation gs = findNearbyStation(getLevel(), getBlockPos());

        if (gs == null) {
            return;
        }

        OfflineStationManager.setOffline(gs.getId());
    }

    public void unregisterStation() {
        Level level = getLevel();

        if (level == null) {
            return;
        }

        GlobalStation gs = findNearbyStation(getLevel(), getBlockPos());

        if (gs == null) {
            return;
        }

        OfflineStationManager.setOnline(gs.getId());
    }

    @Override
    public void onLoad() {
        super.onLoad();

        registerStation();
        System.out.println("MAINTENANCE MARKER BLOCK ENTITY LOADED");
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        unregisterStation();
        System.out.println("MAINTENANCE MARKER BLOCK ENTITY LOADED");
    }
}
