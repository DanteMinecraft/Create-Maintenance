package net.dantemc.create_maintenance_control;

import java.util.Locale;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.dantemc.create_maintenance_control.content.maintenance_box.gui.MaintenanceBoxConfigurationPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public enum ModPackets implements BasePacketPayload.PacketTypeProvider {
    CONFIGURE_MAINTENANCE_BOX(
            MaintenanceBoxConfigurationPacket.class,
            MaintenanceBoxConfigurationPacket.STREAM_CODEC
    );

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> ModPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(CreateMaintenance.asResource(name)),
                clazz, codec
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry =
                new CatnipPacketRegistry(CreateMaintenance.MODID, "1.0.0");
        for (ModPackets packet : ModPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
}