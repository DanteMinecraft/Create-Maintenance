package net.dantemc.create_maintenance_control.maintenance_box;

import com.simibubi.create.content.trains.station.GlobalStation;
import net.dantemc.create_maintenance_control.CreateMaintenance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MaintenanceBoxBlockEntity extends BlockEntity {

    public MaintenanceBoxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void registerStation() {
        CreateMaintenance.debug("REGISTER STATION CALLED");

        Level level = getLevel();

        if (level == null) {
            CreateMaintenance.debug("LEVEL NULL");
            return;
        }

        GlobalStation gs = StationUtils.findNearbyStation(level, getBlockPos());

        if (gs == null) {
            CreateMaintenance.debug("STATION NULL");
            return;
        }

        CreateMaintenance.debug("FOUND STATION: " + gs.name);

        OfflineStationManager.setOffline(gs.getId());
    }

    /*public void unregisterStation() {
        Level level = getLevel();

        if (level == null) {
            return;
        }

        GlobalStation gs = findNearbyStation(getLevel(), getBlockPos());

        if (gs == null) {
            return;
        }

        OfflineStationManager.setOnline(gs.getId());
    }*/

    @Override
    public void onLoad() {
        super.onLoad();

        Level level = getLevel();

        if (level == null || level.isClientSide)
            return;

        CreateMaintenance.debug("Scheduling station registration");

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
        CreateMaintenance.debug("MAINTENANCE MARKER UNREGISTERED STATION (DOES NOT WORK YET DUE TO UNCOMMENTED unregisterStation();");
    }
}
