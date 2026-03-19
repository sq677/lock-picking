package net.skittle.lockpicking.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;
import net.skittle.lockpicking.item.LockItem;
import net.skittle.lockpicking.item.LockpickItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void restrictEnchantments(CallbackInfo ci) {
        AnvilScreenHandler handler = (AnvilScreenHandler) (Object) this;

        ItemStack targetStack = handler.getSlot(0).getStack();

        ItemStack materialStack = handler.getSlot(1).getStack();

        if (targetStack.getItem() instanceof LockItem) {
            var enchantments = EnchantmentHelper.getEnchantments(materialStack);
            if (!enchantments.getEnchantments().isEmpty()) {
                ci.cancel();
                return;
            }
        }

        if (targetStack.getItem() instanceof LockpickItem) {
            var enchantments = EnchantmentHelper.getEnchantments(materialStack);

            for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
                if (!enchantment.matchesKey(Enchantments.UNBREAKING) &&
                    !enchantment.matchesKey(Enchantments.MENDING)) {
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
