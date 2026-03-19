package net.sq.lockpicking;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class LockpickingSounds {

    public static final Identifier LOCK_TURN_ID =
            Identifier.of("lockpicking", "lock_turn");

    public static final SoundEvent LOCK_TURN =
            SoundEvent.of(LOCK_TURN_ID);

    public static final Identifier LOCK_ON_ID =
            Identifier.of("lockpicking", "lock_on");

    public static final SoundEvent LOCK_ON =
            SoundEvent.of(LOCK_ON_ID);

    public static final Identifier PICKING_LOCK_ID =
            Identifier.of("lockpicking", "picking_lock");

    public static final SoundEvent PICKING_LOCK =
            SoundEvent.of(PICKING_LOCK_ID);

    public static final Identifier LOCK_OFF_ID =
            Identifier.of("lockpicking", "lock_off");

    public static final SoundEvent LOCK_OFF =
            SoundEvent.of(LOCK_OFF_ID);

    public static void register() {
        Registry.register(
                Registries.SOUND_EVENT,
                LOCK_TURN_ID,
                LOCK_TURN
        );
        Registry.register(
                Registries.SOUND_EVENT,
                LOCK_ON_ID,
                LOCK_ON
        );
        Registry.register(
                Registries.SOUND_EVENT,
                PICKING_LOCK_ID,
                PICKING_LOCK
        );
        Registry.register(
                Registries.SOUND_EVENT,
                LOCK_OFF_ID,
                LOCK_OFF
        );
    }
}
