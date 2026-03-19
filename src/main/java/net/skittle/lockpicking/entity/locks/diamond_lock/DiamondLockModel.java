package net.skittle.lockpicking.entity.locks.diamond_lock;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.skittle.lockpicking.entity.locks.diamond_lock.animations.OpenAnimation;

public class DiamondLockModel extends SinglePartEntityModel<DiamondLockEntity> {
    private final ModelPart root;

    public DiamondLockModel(ModelPart root) {
        this.root = root;
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone3 = modelPartData.addChild("bone3", ModelPartBuilder.create().uv(0, 26).cuboid(0.0F, -4.0F, -3.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 19).cuboid(0.0F, -5.0F, -3.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(4, 26).cuboid(0.0F, -4.0F, 2.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 26).cuboid(0.0F, 0.0F, -2.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 19.0F, 0.0F));

        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 11).cuboid(-1.0F, -1.0F, -3.0F, 3.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-1.0F, -4.0F, -4.0F, 3.0F, 3.0F, 8.0F, new Dilation(0.0F))
                .uv(22, 0).cuboid(0.0F, 0.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 18).cuboid(-1.0F, -6.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(18, 11).cuboid(1.0F, -6.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(22, 5).cuboid(-1.0F, -6.0F, 3.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 8).cuboid(-1.0F, -6.0F, -4.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(8, 26).cuboid(0.0F, -6.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-1.5F, -1.1F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(3, 1).cuboid(-1.5F, -5.0F, -2.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(12, 12).cuboid(-1.5F, -5.0F, 1.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(23, 21).cuboid(-1.5F, -5.9F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(DiamondLockEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.updateAnimation(entity.openAnimationState, OpenAnimation.animation, ageInTicks);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}
