package net.skittle.lockpicking.mixin;

import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AdvancementWidget.class)
public class AdvancementWidgetMixin {

    @Shadow
    @Final
    private AdvancementDisplay display;

    @Shadow
    @Final
    private OrderedText title;

    @Redirect(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextRenderer;trimToWidth(Lnet/minecraft/text/StringVisitable;I)Lnet/minecraft/text/StringVisitable;"
        )
    )
    private net.minecraft.text.StringVisitable preventTrimForLongTitle(TextRenderer textRenderer, net.minecraft.text.StringVisitable text, int width) {
        String textString = net.minecraft.text.Text.of(text.getString()).getString();
        if (textString.equals("Sleight of hand, nothing in my pockets!")) {
            return text;
        }
        return textRenderer.trimToWidth(text, width);
    }

    @Redirect(
        method = "drawTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)I"
        )
    )
    private int scaleDownLongTitle(DrawContext context, net.minecraft.client.font.TextRenderer textRenderer, OrderedText text, int x, int y, int color) {
        String displayTitle = this.display.getTitle().getString();

        if (displayTitle.equals("Sleight of hand, nothing in my pockets!")) {
            context.getMatrices().push();
            context.getMatrices().scale(1.01f, 1.01f, 1.0f);

            int scaledX = (int)(x / 1.01f);
            int scaledY = (int)(y / 1.01f);

            int result = context.drawTextWithShadow(textRenderer, text, scaledX, scaledY, color);

            context.getMatrices().pop();
            return result;
        }

        return context.drawTextWithShadow(textRenderer, text, x, y, color);
    }
}
