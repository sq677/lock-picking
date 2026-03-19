package net.skittle.lockpicking.entity.locks.netherite_lock;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.skittle.lockpicking.entity.locks.netherite_lock.animations.OpenAnimation;

public class NetheriteLockModel extends SinglePartEntityModel<NetheriteLockEntity> {
    private final ModelPart root;
    private final ModelPart bone;
    private final ModelPart bone2;

    public NetheriteLockModel(ModelPart root) {
        this.root = root;
        this.bone = root.getChild("bone");
        this.bone2 = root.getChild("bone2");
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 32).cuboid(-1.5F, -11.0F, 2.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(10, 32).cuboid(-1.5F, -11.0F, -4.0F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 22).cuboid(-1.5F, -13.0F, -4.0F, 3.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(8, 45).cuboid(-0.5F, -14.0F, 2.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(38, 41).cuboid(-0.5F, -14.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(12, 45).cuboid(-0.5F, -14.0F, -3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(44, 39).cuboid(-0.5F, -12.0F, -5.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(40, 44).cuboid(-0.5F, -12.0F, 4.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 22.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData bone2 = modelPartData.addChild("bone2", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -9.0F, -6.0F, 6.0F, 10.0F, 12.0F, new Dilation(0.0F))
                .uv(36, 23).cuboid(-3.0F, -9.0F, -7.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(26, 35).cuboid(-4.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(32, 35).cuboid(-4.0F, -1.0F, -6.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(36, 26).cuboid(-3.0F, -1.0F, -7.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(38, 35).cuboid(-3.0F, 1.0F, -6.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 45).cuboid(-2.0F, 1.0F, -6.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(36, 0).cuboid(3.0F, -1.0F, -6.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(38, 38).cuboid(1.0F, -1.0F, -7.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(4, 45).cuboid(1.0F, 1.0F, -6.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(18, 39).cuboid(2.0F, 1.0F, -6.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(32, 44).cuboid(-4.0F, -4.0F, 1.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(44, 32).cuboid(-4.0F, -4.0F, -2.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 39).cuboid(-4.0F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(36, 44).cuboid(-4.0F, -6.0F, 2.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(44, 36).cuboid(-4.0F, -6.0F, -3.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 30).cuboid(-4.0F, -7.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(30, 39).cuboid(-4.0F, -6.0F, -5.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(34, 39).cuboid(-2.0F, -6.0F, -7.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 40).cuboid(1.0F, -6.0F, -7.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(4, 40).cuboid(-4.0F, -6.0F, 4.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(8, 40).cuboid(3.0F, -6.0F, 4.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(12, 40).cuboid(3.0F, -6.0F, -5.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(36, 4).cuboid(-4.0F, -9.0F, 4.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(6, 36).cuboid(-4.0F, -1.0F, 4.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(42, 0).cuboid(-3.0F, -9.0F, 6.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(42, 6).cuboid(1.0F, -1.0F, 6.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(42, 9).cuboid(-3.0F, -1.0F, 6.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(12, 36).cuboid(3.0F, -1.0F, 4.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(42, 12).cuboid(2.0F, 1.0F, 4.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(42, 15).cuboid(-3.0F, 1.0F, 4.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(16, 45).cuboid(1.0F, 1.0F, 5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(20, 45).cuboid(-2.0F, 1.0F, 5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 47).cuboid(-2.5F, 1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(42, 21).cuboid(-2.5F, 1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(4, 47).cuboid(-2.5F, 1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(8, 47).cuboid(1.5F, 1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 42).cuboid(1.5F, 1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(12, 47).cuboid(1.5F, 1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(16, 47).cuboid(-0.5F, 1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(42, 24).cuboid(-0.5F, 1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(20, 47).cuboid(-0.5F, 1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 30).cuboid(3.0F, -3.5F, -1.0F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(36, 12).cuboid(3.0F, -7.5F, -3.5F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(36, 16).cuboid(3.0F, -7.5F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(20, 35).cuboid(3.0F, -7.5F, 1.5F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(22, 22).cuboid(3.0F, -5.5F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(24, 47).cuboid(3.0F, -3.5F, -2.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(36, 47).cuboid(3.0F, -3.5F, 1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(44, 27).cuboid(-2.0F, -6.0F, 6.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(28, 44).cuboid(1.0F, -6.0F, 6.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(36, 20).cuboid(-3.0F, -10.0F, -6.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(44, 42).cuboid(-2.0F, -10.0F, -6.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(38, 32).cuboid(1.0F, -9.0F, -7.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(38, 29).cuboid(2.0F, -10.0F, -6.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(44, 44).cuboid(1.0F, -10.0F, -6.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 36).cuboid(3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(42, 18).cuboid(2.0F, -10.0F, 4.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(36, 8).cuboid(3.0F, -9.0F, 4.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(42, 3).cuboid(1.0F, -9.0F, 6.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 45).cuboid(-2.0F, -10.0F, 5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(16, 42).cuboid(-3.0F, -10.0F, 4.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(44, 46).cuboid(1.0F, -10.0F, 5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 22.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(NetheriteLockEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.updateAnimation(entity.openAnimationState, OpenAnimation.netherite_lock_open_animation, ageInTicks);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        bone.render(matrices, vertices, light, overlay, color);
        bone2.render(matrices, vertices, light, overlay, color);
    }
}
