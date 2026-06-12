package net.dantemc.create_maintenance;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;

public class CreateMaintenanceShapes {
    public static final VoxelShaper MAINTENANCE_BOX =
            VoxelShaper.forDirectional(
                    Shapes.or(
                            Block.box(1, -1, 1, 15, 5, 15),
                            Block.box(3, 5, 3, 13, 7, 13)
                    ),
                    Direction.UP
            );
}
