package net.skittle.lockpicking.UI.notice;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NoticeScreen extends Screen {
    private static final Identifier NOTICE_TEXTURE = Identifier.of("lockpicking", "notice/notice_preview.png");
    private final Screen parent;
    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 144;
    private ButtonWidget okButton;
    private CheckboxWidget dontShowAgainCheckbox;
    private int fadeInTicks = 0;
    private int fadeOutTicks = 0;
    private boolean closing = false;
    private static final int FADE_DURATION = 20;

    public NoticeScreen(Screen parent) {
        super(Text.literal("Notice"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        this.dontShowAgainCheckbox = this.addDrawableChild(
            CheckboxWidget.builder(Text.literal("Don't show this again"), this.textRenderer)
                .pos(this.width / 2 - 70, this.height - 50)
                .build()
        );

        this.okButton = this.addDrawableChild(ButtonWidget.builder(
            Text.literal("OK"),
            button -> this.startClosing()
        ).dimensions(this.width / 2 - 40, this.height - 25, 60, 20).build());
    }

    @Override
    public void tick() {
        super.tick();

        if (closing) {
            fadeOutTicks++;
            if (fadeOutTicks >= FADE_DURATION) {
                if (this.client != null) {
                    this.client.setScreen(parent);
                }
            }
        } else {
            if (fadeInTicks < FADE_DURATION) {
                fadeInTicks++;
            }
        }
    }

    private void startClosing() {
        if (this.dontShowAgainCheckbox != null && this.dontShowAgainCheckbox.isChecked()) {
            NoticeConfig.setDontShowAgain(true);
        }
        closing = true;
        fadeOutTicks = 0;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        float fadeProgress;
        if (closing) {
            fadeProgress = 1.0f - ((float) fadeOutTicks / FADE_DURATION);
        } else {
            fadeProgress = Math.min(1.0f, (float) fadeInTicks / FADE_DURATION);
        }

        int centerX = this.width / 2;
        int startY = 30;

        int titleAlpha = (int)(fadeProgress * 255);
        context.getMatrices().push();
        context.getMatrices().scale(1.5f, 1.5f, 1.0f);
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal("Notice"),
            (int)(centerX / 1.5f),
            (int)(startY / 1.5f),
            (titleAlpha << 24) | 0xAAAAAA
        );
        context.getMatrices().pop();

        float imageScale = 0.75f;
        int imageY = startY + 35;
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, fadeProgress);
        context.getMatrices().push();
        context.getMatrices().translate(centerX, imageY, 0);
        context.getMatrices().scale(imageScale, imageScale, 1.0f);
        context.drawTexture(
            NOTICE_TEXTURE,
            -IMAGE_WIDTH / 2,
            0,
            0,
            0,
            IMAGE_WIDTH,
            IMAGE_HEIGHT,
            IMAGE_WIDTH,
            IMAGE_HEIGHT
        );
        context.getMatrices().pop();
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();

        int scaledHeight = (int)(IMAGE_HEIGHT * imageScale);

        int textY = imageY + scaledHeight + 10;
        int textAlpha = (int)(fadeProgress * 255);
        context.getMatrices().push();
        context.getMatrices().scale(0.85f, 0.85f, 1.0f);
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal("This mod is designed to work with Guard Villagers mod."),
            (int)(centerX / 0.85f),
            (int)(textY / 0.85f),
            (textAlpha << 24) | 0xAAAAAA
        );

        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal("Some gameplay features may not be available without it."),
            (int)(centerX / 0.85f),
            (int)((textY + 10) / 0.85f),
            (textAlpha << 24) | 0xAAAAAA
        );

        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal("Download Guard Villagers for the full experience!"),
            (int)(centerX / 0.85f),
            (int)((textY + 20) / 0.85f),
            (textAlpha << 24) | 0xFFD700
        );
        context.getMatrices().pop();

        if (fadeProgress > 0) {
            if (this.dontShowAgainCheckbox != null) {
                this.dontShowAgainCheckbox.render(context, mouseX, mouseY, delta);
            }
            if (this.okButton != null) {
                this.okButton.render(context, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }
}
