package net.dantemc.create_maintenance.marker;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import net.dantemc.create_maintenance.CreateMaintenance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

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
        CreateMaintenance.LOGGER.info("[CM] REGISTER STATION CALLED");

        Level level = getLevel();

        if (level == null) {
            CreateMaintenance.LOGGER.info("[CM] LEVEL NULL");
            return;
        }

        GlobalStation gs = findNearbyStation(level, getBlockPos());

        if (gs == null) {
            CreateMaintenance.LOGGER.info("[CM] STATION NULL");
            return;
        }

        CreateMaintenance.LOGGER.info("[CM] FOUND STATION: " + gs.name);

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

        Level level = getLevel();

        if (level == null || level.isClientSide)
            return;

        CreateMaintenance.LOGGER.info("[CM] Scheduling station registration");

        level.scheduleTick(
                getBlockPos(),
                getBlockState().getBlock(),
                100
        );
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        //unregisterStation();
        CreateMaintenance.LOGGER.info("MAINTENANCE MARKER UNREGISTERED STATION (DOES NOT WORK YET DUE TO UNCOMMENTED unregisterStation();");
    }
}
