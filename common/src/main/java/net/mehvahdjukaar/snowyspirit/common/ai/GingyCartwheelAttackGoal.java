package net.mehvahdjukaar.snowyspirit.common.ai;

import net.mehvahdjukaar.moonlight.api.util.math.MthUtils;
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
        if (mob.getRandom().nextInt(reducedTickDelay(40)) != 0) {
            return false;
        }
        randomizeSpinsAndSide();

        Vec3 v = target.position().subtract(mob.position());
        if (Math.abs(v.y) > mob.getBbHeight()) {
            return false;
        }
        //single one is slower than walking there
        if (v.horizontalDistance() > rollDistance(1)) {
            this.spins = 2;
        }

        float facing = (float) MthUtils.getYaw(v);
        boolean rightSideAlreadyTurnedToIt = Mth.wrapDegrees(mob.yBodyRot - facing) < 0;
        this.rollRight = rightSideAlreadyTurnedToIt;
        return tryCartwheelTowards(facing);
    }

    @Override
    public void start() {
        super.start();
        alreadyHit.clear();
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target != null) {
            aimRollAt(target.position(), 4);
        }
        super.tick();
        if (!rolling) return;

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
