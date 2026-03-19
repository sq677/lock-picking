package net.sq.lockpicking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DamageLockpickPayload() implements CustomPayload {
    public static final Id<DamageLockpickPayload> ID = new Id<>(Identifier.of(Lockpicking.MOD_ID, "damage_lockpick"));
    public static final PacketCodec<RegistryByteBuf, DamageLockpickPayload> CODEC = PacketCodec.unit(new DamageLockpickPayload());

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}