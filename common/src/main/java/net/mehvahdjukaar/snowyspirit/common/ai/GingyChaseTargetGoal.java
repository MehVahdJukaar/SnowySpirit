package net.mehvahdjukaar.snowyspirit.common.ai;

import net.mehvahdjukaar.moonlight.api.util.math.MthUtils;
import net.mehvahdjukaar.snowyspirit.common.entity.GingyEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GingyChaseTargetGoal extends Goal {
    private final GingyEntity mob;
    private final double speedModifier;
    private int timeToRecalcPath;
    private boolean clockwise;
    private int circlingTime;

    public GingyChaseTargetGoal(GingyEntity mob, double speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = mob.getTarget();
        return t != null && t.isAlive();
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.clockwise = mob.getRandom().nextBoolean();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        mob.getLookControl().setLookAt(target, 30, 30);

        float radius = 2 * mob.getBbHeight() ;
        float dist = mob.distanceTo(target);
        if(dist < radius + 1) {
            circleAround(target, dist - radius);
        } else if(--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            mob.getNavigation().moveTo(target, speedModifier);
        }
    }

    // skeleton strafing but side on, so its already turned for a cartwheel
    private void circleAround(LivingEntity target,float tooFar) {
        mob.getNavigation().stop();
        this.timeToRecalcPath = 0;

        boolean wantsToTurnBack = circlingTime > 10 && (mob.horizontalCollision || mob.getRandom().nextInt(60) == 0);
        if(wantsToTurnBack) {
            this.clockwise = !clockwise;
            this.circlingTime = 0;
        }
        this.circlingTime++;

        Vec3 v = target.position().subtract(mob.position());
        float sideOn = (float) MthUtils.getYaw(v) + (clockwise ? 90 : -90);
        mob.setYRot(Mth.approachDegrees(mob.getYRot(), sideOn, 20));

        float inwards = Mth.clamp(tooFar, -0.5f, 0.5f);
        mob.getMoveControl().strafe(1, clockwise ? inwards : -inwards);
    }
}
