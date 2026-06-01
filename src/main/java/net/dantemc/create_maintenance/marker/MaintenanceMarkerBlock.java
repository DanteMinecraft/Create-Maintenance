package net.dantemc.create_maintenance.marker;

import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MaintenanceMarkerBlock extends Block implements EntityBlock {

    public MaintenanceMarkerBlock(Properties properties) {
        super(properties);
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

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (level.isClientSide)
            return;

        System.out.println("Marker placed");
        GlobalStation gs = findNearbyStation(level, pos);

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
        super.onRemove(state, level, pos, newState, movedByPiston);

        if (level.isClientSide)
            return;

        System.out.println("Marker removed");
        GlobalStation gs = findNearbyStation(level, pos);

        if (gs == null) {
            System.out.println(OfflineStationManager.OFFLINE_STATIONS);
            return;
        }

        OfflineStationManager.setOnline(gs.getId());
        System.out.println(OfflineStationManager.OFFLINE_STATIONS);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state) {

        return new MaintenanceMarkerBlockEntity(
                pos,
                state
        );
    }

    @Override
    protected void tick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random) {

        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof MaintenanceMarkerBlockEntity marker) {
            marker.registerStation();
        }
    }


}
