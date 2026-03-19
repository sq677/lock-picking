package net.skittle.lockpicking.entity.locks.iron_lock;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.skittle.lockpicking.entity.locks.iron_lock.animations.OpenAnimation;

public class IronLockModel extends SinglePartEntityModel<IronLockEntity> {
	private final ModelPart root;
	private final ModelPart bone;
	private final ModelPart bone2;
	private final ModelPart bone3;

	public IronLockModel(ModelPart root) {
		this.root = root;
		this.bone = root.getChild("bone");
		this.bone2 = root.getChild("bone2");
		this.bone3 = this.bone2.getChild("bone3");
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(30, 0).cuboid(1.0F, -16.0F, 0.0F, 2.0F, 5.0F, 1.0F, new Dilation(0.0F))
				.uv(30, 32).cuboid(3.0F, -16.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(8, 33).cuboid(2.0F, -17.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(16, 25).cuboid(3.0F, -18.0F, 0.0F, 6.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(30, 28).cuboid(9.0F, -17.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(16, 28).cuboid(9.0F, -16.0F, 0.0F, 2.0F, 7.0F, 1.0F, new Dilation(0.0F))
				.uv(30, 30).cuboid(8.0F, -16.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(22, 33).cuboid(3.0F, -11.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(12, 33).cuboid(2.0F, -11.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, 24.0F, 0.0F));

		ModelPartData bone2 = modelPartData.addChild("bone2", ModelPartBuilder.create().uv(22, 28).cuboid(4.0F, -9.0F, 2.0F, 1.0F, 2.0F, 3.0F, new Dilation(0.0F))
				.uv(30, 25).cuboid(2.0F, -9.0F, 2.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(30, 10).cuboid(2.0F, -9.0F, 4.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 25).cuboid(-3.0F, -9.0F, 2.0F, 5.0F, 2.0F, 3.0F, new Dilation(0.0F))
				.uv(0, 30).cuboid(-7.0F, -9.0F, 2.0F, 1.0F, 2.0F, 3.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(-7.0F, -7.0F, 2.0F, 12.0F, 10.0F, 3.0F, new Dilation(0.0F))
				.uv(30, 7).cuboid(-6.0F, -9.0F, 4.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(8, 30).cuboid(-6.0F, -9.0F, 2.0F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 22.0F, -3.0F));

		ModelPartData cube_r1 = bone2.addChild("cube_r1", ModelPartBuilder.create().uv(22, 13).cuboid(-6.0F, -14.0F, 1.0F, 10.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, 5.0F, 3.0F, 0.0F, 3.1416F, 0.0F));

		ModelPartData bone3 = bone2.addChild("bone3", ModelPartBuilder.create().uv(0, 13).cuboid(-4.0F, -8.5F, 0.5F, 10.0F, 11.0F, 1.0F, new Dilation(0.0F))
				.uv(48, 2).cuboid(-1.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
				.uv(48, 2).cuboid(-2.0F, -6.0F, 0.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
				.uv(48, 2).cuboid(0.0F, -7.0F, 0.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(48, 2).cuboid(3.0F, -6.0F, 0.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
				.uv(48, 2).cuboid(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(37, 2).cuboid(3.0F, -3.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(37, 2).cuboid(-2.0F, -3.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(37, 2).cuboid(-1.0F, -7.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(37, 2).cuboid(2.0F, -7.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
				.uv(48, 2).cuboid(2.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.5F, 6.5F, 0.0F, 3.1416F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(IronLockEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.updateAnimation(entity.openAnimationState, OpenAnimation.iron_lock_open_animation, ageInTicks);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		bone.render(matrices, vertices, light, overlay, color);
		bone2.render(matrices, vertices, light, overlay, color);
	}
}
