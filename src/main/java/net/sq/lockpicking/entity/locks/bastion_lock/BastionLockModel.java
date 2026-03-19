package net.sq.lockpicking.entity.locks.bastion_lock;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.sq.lockpicking.entity.locks.bastion_lock.animations.OpenAnimation;

public class BastionLockModel extends SinglePartEntityModel<BastionLockEntity> {
	private final ModelPart root;
	private final ModelPart bone;
	private final ModelPart bone2;

	public BastionLockModel(ModelPart root) {
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

		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create()
			.uv(12, 18).cuboid(-1.0F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
			.uv(0, 18).cuboid(0.0F, -3.0F, -1.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
			.uv(8, 18).cuboid(3.0F, -3.0F, -1.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)),
			ModelTransform.pivot(-1.0F, 17.0F, 0.0F));

		ModelPartData bone2 = modelPartData.addChild("bone2", ModelPartBuilder.create()
			.uv(0, 10).cuboid(-2.0F, -2.0F, -1.0F, 5.0F, 1.0F, 3.0F, new Dilation(0.0F))
			.uv(16, 10).cuboid(-2.0F, -5.0F, -1.0F, 1.0F, 3.0F, 3.0F, new Dilation(0.0F))
			.uv(16, 16).cuboid(2.0F, -5.0F, -1.0F, 1.0F, 3.0F, 3.0F, new Dilation(0.0F))
			.uv(0, 14).cuboid(-2.0F, -6.0F, -1.0F, 5.0F, 1.0F, 3.0F, new Dilation(0.0F))
			.uv(0, 0).cuboid(-3.0F, -7.0F, -1.2F, 7.0F, 7.0F, 3.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, 24.0F, -0.8F));

		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void setAngles(BastionLockEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.updateAnimation(entity.openAnimationState, OpenAnimation.animation, ageInTicks);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		bone.render(matrices, vertices, light, overlay, color);
		bone2.render(matrices, vertices, light, overlay, color);
	}
}
