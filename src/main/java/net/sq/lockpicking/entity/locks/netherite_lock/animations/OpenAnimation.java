package net.sq.lockpicking.entity.locks.netherite_lock.animations;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

public class OpenAnimation {
    public static final Animation netherite_lock_open_animation = Animation.Builder.create(0.5F)
            .addBoneAnimation("bone", new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("bone2", new Transformation(
                    Transformation.Targets.TRANSLATE,

                    new Keyframe(0.0F,
                            AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F),
                            Transformation.Interpolations.CUBIC
                    ),

                    new Keyframe(0.25F,
                            AnimationHelper.createTranslationalVector(0.0F, -1.2F, 0.0F),
                            Transformation.Interpolations.CUBIC
                    ),

                    new Keyframe(0.4F,
                            AnimationHelper.createTranslationalVector(0.0F, -0.9F, 0.0F),
                            Transformation.Interpolations.CUBIC
                    ),

                    new Keyframe(0.6F,
                            AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F),
                            Transformation.Interpolations.CUBIC
                    )
            ))
            .build();
}
