package net.mehvahdjukaar.snowyspirit.common.ai;

import net.mehvahdjukaar.snowyspirit.common.entity.GingyEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class GingyCartwheelAttackGoal extends GingyCartwheelGoal {

    private final Set<LivingEntity> alreadyHit = new HashSet<>();

    public GingyCartwheelAttackGoal(GingyEntity mob) {
        super(mob, true);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive() || !canCartwheel()) return false;
        if (mob.getRandom().nextInt(reducedTickDelay(80)) != 0) {
            return false;
        }
        randomizeSpinsAndSide();

        Vec3 v = target.position().subtract(mob.position());
        if (Math.abs(v.y) > mob.getBbHeight()) {
            return false;
        }
        float yaw = (float) (Mth.atan2(v.z, v.x) * Mth.RAD_TO_DEG) - 90;
        return tryCartwheelTowards(yaw) && v.horizontalDistance() < rollDistance;
    }

    @Override
    public void start() {
        super.start();
        alreadyHit.clear();
    }

    @Override
    public void tick() {
        super.tick();
        if (!rolling) return;

        LivingEntity target = mob.getTarget();
        float reach = mob.getBbHeight() / 4;
        AABB box = mob.getBoundingBox().inflate(reach, 0, reach);

        for(LivingEntity le : mob.level().getEntitiesOfClass(LivingEntity.class, box)) {
            boolean enemy = le == target || GingyEntity.TARGET_SELECTOR.test(le);
            if(enemy && alreadyHit.add(le)) {
                mob.doCartwheelHurtTarget(le);
            }
        }
    }
}
