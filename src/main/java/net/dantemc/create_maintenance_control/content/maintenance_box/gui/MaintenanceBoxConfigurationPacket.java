package net.dantemc.create_maintenance_control.content.maintenance_box.gui;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.dantemc.create_maintenance_control.ModPackets;
import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceBoxBlockEntity;
import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceRedstoneMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class MaintenanceBoxConfigurationPacket extends BlockEntityConfigurationPacket<MaintenanceBoxBlockEntity> {

    public static final StreamCodec<ByteBuf, MaintenanceBoxConfigurationPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.pos,
            ByteBufCodecs.STRING_UTF8, packet -> packet.stationFilter,
            ByteBufCodecs.VAR_INT, packet -> packet.redstoneMode.ordinal(),
            ByteBufCodecs.BOOL, packet -> packet.skipDownstream,
            (pos, stationFilter, redstoneModeOrdinal, skipDownstream) ->
                    new MaintenanceBoxConfigurationPacket(pos, stationFilter, MaintenanceRedstoneMode.values()[redstoneModeOrdinal], skipDownstream)
    );

    private final String stationFilter;
    private final MaintenanceRedstoneMode redstoneMode;
    private final boolean skipDownstream;

    public MaintenanceBoxConfigurationPacket(BlockPos pos, String stationFilter, MaintenanceRedstoneMode redstoneMode, boolean skipDownstream) {
        super(pos);
        this.stationFilter = stationFilter;
        this.redstoneMode = redstoneMode;
        this.skipDownstream = skipDownstream;
    }

    @Override
    protected void applySettings(ServerPlayer player, MaintenanceBoxBlockEntity be) {
        be.applySettings(stationFilter, redstoneMode, skipDownstream);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPackets.CONFIGURE_MAINTENANCE_BOX;
    }
}
