package net.skittle.lockpicking.entity.locks.copper_lock;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.skittle.lockpicking.entity.locks.copper_lock.animations.OpenAnimation;

public class CopperLockModel extends SinglePartEntityModel<CopperLockEntity> {
	private final ModelPart root;
	private final ModelPart bone2;
	private final ModelPart bone;

	public CopperLockModel(ModelPart root) {
		this.root = root;
		this.bone2 = root.getChild("bone2");
		this.bone = root.getChild("bone");
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone2 = modelPartData.addChild("bone2", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -4.0F, -1.0F, 4.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(6, 6).cuboid(-1.999F, -2.0F, -1.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 6).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 8).cuboid(1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 20.0F, 0.5F));
		return TexturedModelData.of(modelData, 16, 16);
	}

	@Override
	public void setAngles(CopperLockEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.updateAnimation(entity.openAnimationState, OpenAnimation.animation, ageInTicks);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		bone2.render(matrices, vertices, light, overlay, color);
		bone.render(matrices, vertices, light, overlay, color);
	}
}
