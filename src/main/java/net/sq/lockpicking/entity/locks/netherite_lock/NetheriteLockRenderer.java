package net.sq.lockpicking.entity.locks.netherite_lock;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class NetheriteLockRenderer extends EntityRenderer<NetheriteLockEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(Identifier.of("lockpicking", "netherite_lock"), "main");
    private static final Identifier TEXTURE = Identifier.of("lockpicking", "textures/entity/netherite_lock.png");
    private final NetheriteLockModel model;

    public NetheriteLockRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new NetheriteLockModel(ctx.getPart(LAYER));
    }

    @Override
    public void render(NetheriteLockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float lidAngle = 0f;
        if (entity.getChestPos() != null) {
            net.minecraft.block.entity.BlockEntity blockEntity = entity.getWorld().getBlockEntity(entity.getChestPos());
            if (blockEntity instanceof net.minecraft.block.entity.ChestBlockEntity chestEntity) {
                lidAngle = chestEntity.getAnimationProgress(tickDelta);
            }
        }

        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getYaw()));

        matrices.translate(0, 0, 0.4375);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-lidAngle * 90f));
        matrices.translate(0, 0, -0.4375);

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90f));
        matrices.scale(0.4f, 0.4f, 0.4f);

        matrices.translate(0.0, -1.3, 0.01);

        float ageInTicks = entity.age + tickDelta;
        model.setAngles(entity, 0, 0, ageInTicks, 0, 0);

        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
        model.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(NetheriteLockEntity entity) {
        return TEXTURE;
    }
}
