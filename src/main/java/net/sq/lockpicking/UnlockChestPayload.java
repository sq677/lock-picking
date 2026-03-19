package net.sq.lockpicking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record UnlockChestPayload(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<UnlockChestPayload> ID = new CustomPayload.Id<>(Lockpicking.UNLOCK_PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, UnlockChestPayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, UnlockChestPayload::pos,
            UnlockChestPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}