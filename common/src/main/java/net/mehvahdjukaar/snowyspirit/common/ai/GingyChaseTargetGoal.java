package net.mehvahdjukaar.snowyspirit.common.ai;

import net.mehvahdjukaar.snowyspirit.common.entity.GingyEntity;
import net.minecraft.core.BlockPos;
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
            circleAround(target, radius);
        } else if(--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            mob.getNavigation().moveTo(target, speedModifier);
        }
    }

    // walking around it keeps it side on. that way we are aalready turned for a cartwheel
    private void circleAround(LivingEntity target,float radius) {
        mob.getNavigation().stop();
        this.timeToRecalcPath = 0;

        Vec3 fromTarget = mob.position().subtract(target.position()).multiply(1, 0, 1).normalize().scale(radius);
        Vec3 ahead = target.position().add(fromTarget.yRot(clockwise ? 0.7f : -0.7f));
        boolean hasGround = mob.getNavigation().isStableDestination(BlockPos.containing(ahead.x, mob.getY(), ahead.z));

        boolean blocked = mob.horizontalCollision || !hasGround;
        if (blocked && circlingTime > 10) {
            this.clockwise = !clockwise;
            this.circlingTime = 0;
        }
        this.circlingTime++;

        if (hasGround) {
            mob.getMoveControl().setWantedPosition(ahead.x, mob.getY(), ahead.z, 1);
        }
    }
}
