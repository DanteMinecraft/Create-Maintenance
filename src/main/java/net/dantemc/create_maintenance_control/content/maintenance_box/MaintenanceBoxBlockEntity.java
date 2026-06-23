package net.dantemc.create_maintenance_control.content.maintenance_box;

import com.simibubi.create.content.trains.station.GlobalStation;
import net.dantemc.create_maintenance_control.CreateMaintenance;
import net.dantemc.create_maintenance_control.railway.OfflineStationManager;
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
        Level level = getLevel();

        if (level == null || level.isClientSide)
            return;

        GlobalStation station = StationUtils.findNearbyStation(level, getBlockPos());

        if (station == null)
            return;

        boolean powered = getBlockState().getValue(MaintenanceBoxBlock.POWERED);

        OfflineStationManager.refreshBox(level, getBlockPos(), station.name, powered);
    }

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
}
