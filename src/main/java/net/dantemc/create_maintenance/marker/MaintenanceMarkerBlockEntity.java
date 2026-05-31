package net.dantemc.create_maintenance.marker;

import net.dantemc.create_maintenance.CreateMaintenance;
import net.minecraft.core.BlockPos;
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

    @Override
    public void onLoad() {
        super.onLoad();

        System.out.println("MAINTENANCE MARKER BLOCK ENTITY LOADED");
    }
}
