package net.skittle.lockpicking.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.skittle.lockpicking.UI.notice.NoticeConfig;
import net.skittle.lockpicking.UI.notice.NoticeScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addNoticeButton(CallbackInfo ci) {
        if (!FabricLoader.getInstance().isModLoaded("guardvillagers") && NoticeConfig.shouldShowNotice()) {
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("!"),
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(new NoticeScreen((TitleScreen)(Object)this));
                    }
                }
            ).dimensions(this.width - 30, 10, 20, 20).build());
        }
    }
}
