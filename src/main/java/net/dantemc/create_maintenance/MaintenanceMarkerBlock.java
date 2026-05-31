package net.dantemc.create_maintenance;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MaintenanceMarkerBlock extends Block{

    public MaintenanceMarkerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        for (Direction dir : Direction.values()) {
            BlockPos checkPos = pos.relative(dir);
            BlockEntity be = level.getBlockEntity(checkPos);

            if (be instanceof StationBlockEntity stationBlock) {
                GlobalStation gs = stationBlock.getStation();
                System.out.println("\nName: " + gs.name + "\nId: " + gs.id + "\nBE Pos: " + gs.blockEntityPos);
            }
        }

    }
}
