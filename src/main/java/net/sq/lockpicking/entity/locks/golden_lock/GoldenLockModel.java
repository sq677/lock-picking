package net.sq.lockpicking.entity.locks.golden_lock;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.sq.lockpicking.entity.locks.golden_lock.animations.OpenAnimation;

public class GoldenLockModel extends SinglePartEntityModel<GoldenLockEntity> {
    private final ModelPart root;

    public GoldenLockModel(ModelPart root) {
        this.root = root;
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create().uv(22, 13).cuboid(-6.0F, -14.0F, 1.0F, 10.0F, 11.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-7.0F, -12.0F, -2.0F, 12.0F, 10.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 13).cuboid(-6.0F, -14.0F, -3.0F, 10.0F, 11.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 28).cuboid(4.0F, -14.0F, -2.0F, 1.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 30).cuboid(-7.0F, -14.0F, -2.0F, 1.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(30, 10).cuboid(2.0F, -14.0F, 0.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 7).cuboid(-6.0F, -14.0F, 0.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(8, 30).cuboid(-6.0F, -14.0F, -2.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 25).cuboid(-3.0F, -14.0F, -2.0F, 5.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(30, 25).cuboid(2.0F, -14.0F, -2.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 26.0F, 1.0F));

        ModelPartData shackle = modelPartData.addChild("shackle", ModelPartBuilder.create()
                .uv(16, 28).cuboid(2.0F, -19.0F, -1.0F, 2.0F, 7.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 28).cuboid(2.0F, -20.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 30).cuboid(1.0F, -19.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 32).cuboid(-4.0F, -19.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(8, 33).cuboid(-5.0F, -20.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(12, 33).cuboid(-5.0F, -13.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 33).cuboid(-4.0F, -13.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(16, 25).cuboid(-4.0F, -21.0F, -1.0F, 6.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 0).cuboid(-6.0F, -19.0F, -1.0F, 2.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 26.0F, 1.0F));

        ModelPartData lock = modelPartData.addChild("lock", ModelPartBuilder.create().uv(48, 2).cuboid(-1.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(48, 2).cuboid(1.0F, -3.0F, 0.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(48, 6).cuboid(4.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(48, 2).cuboid(1.0F, 4.0F, 0.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(37, 2).cuboid(4.0F, 1.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(37, 2).cuboid(-1.0F, 1.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(37, 2).cuboid(0.0F, -3.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(48, 7).cuboid(3.0F, -3.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(48, 2).cuboid(3.0F, 2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(48, 2).cuboid(0.0F, 2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 16.5F, 2.5F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(GoldenLockEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.updateAnimation(entity.openAnimationState, OpenAnimation.animation, ageInTicks);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}
