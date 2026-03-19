package net.skittle.lockpicking.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AdvancementToast.class)
public class AdvancementToastMixin {

    @Redirect(
        method = "draw",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I",
            ordinal = 0
        )
    )
    private int scaleDownLongTitle(DrawContext context, net.minecraft.client.font.TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow) {
        String titleText = text.getString();

        if (titleText.equals("Sleight of hand, nothing in my pockets!")) {
            context.getMatrices().push();
            context.getMatrices().scale(0.5f, 0.5f, 1.0f);

            int scaledX = (int)(x / 0.5f);
            int scaledY = (int)(y / 0.5f);

            int result = context.drawText(textRenderer, text, scaledX, scaledY, color, shadow);
            context.getMatrices().pop();
            return result;
        }

        return context.drawText(textRenderer, text, x, y, color, shadow);
    }
}
