package net.sq.lockpicking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record StartLockpickingPayload(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<StartLockpickingPayload> ID =
            new CustomPayload.Id<>(Identifier.of("lockpicking", "start_lockpicking"));

    public static final PacketCodec<RegistryByteBuf, StartLockpickingPayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, StartLockpickingPayload::pos,
                    StartLockpickingPayload::new
            );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
