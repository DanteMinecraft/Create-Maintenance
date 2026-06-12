package net.dantemc.create_maintenance.maintenance_box;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import net.dantemc.create_maintenance.CreateMaintenanceShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.dantemc.create_maintenance.CreateMaintenance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MaintenanceBoxBlock extends WrenchableDirectionalBlock implements IBE<MaintenanceBoxBlockEntity> {

    public MaintenanceBoxBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (level.isClientSide)
            return;

        CreateMaintenance.debug("Marker placed");
        GlobalStation gs = StationUtils.findNearbyStation(level, pos);

        if (gs == null) {
            System.out.println(OfflineStationManager.OFFLINE_STATIONS);
            return;
        }

        OfflineStationManager.setOffline(gs.getId());
        //System.out.println("\nName: " + gs.name + "\nId: " + gs.id + "\nBE Pos: " + gs.blockEntityPos + "\nIs offline?: " + OfflineStationManager.isOffline(gs.getId()));
        System.out.println(OfflineStationManager.OFFLINE_STATIONS);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);

        if (level.isClientSide)
            return;

        CreateMaintenance.debug("Marker removed");
        GlobalStation gs = StationUtils.findNearbyStation(level, pos);

        if (gs == null) {
            System.out.println(OfflineStationManager.OFFLINE_STATIONS);
            return;
        }

        OfflineStationManager.setOnline(gs.getId());
        System.out.println(OfflineStationManager.OFFLINE_STATIONS);
    }

    @Override
    public Class<MaintenanceBoxBlockEntity> getBlockEntityClass() {
        return MaintenanceBoxBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MaintenanceBoxBlockEntity> getBlockEntityType() {
        return CreateMaintenance.MAINTENANCE_BOX_BE.get();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState placed = super.getStateForPlacement(context);
        return placed.setValue(FACING, context.getClickedFace());
    }

    @Override
    public VoxelShape getShape(BlockState State, BlockGetter Level, BlockPos Pos, CollisionContext Context) {
        return CreateMaintenanceShapes.MAINTENANCE_BOX.get(State.getValue(FACING));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {

        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof MaintenanceBoxBlockEntity marker) {
            marker.registerStation();
        }
    }
}
