package net.dantemc.create_maintenance.maintenance_box;

import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class StationUtils {

    public static GlobalStation findNearbyStation(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(pos.relative(dir));

            if (be instanceof StationBlockEntity stationBlock) {
                return stationBlock.getStation();
            }
        }
        return null;
    }
}
