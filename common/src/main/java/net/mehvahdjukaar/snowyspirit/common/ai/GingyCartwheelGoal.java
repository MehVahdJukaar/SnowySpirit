package net.mehvahdjukaar.snowyspirit.common.ai;

import net.mehvahdjukaar.snowyspirit.common.entity.GingyEntity;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GingyCartwheelGoal extends Goal {
    protected final GingyEntity mob;
    private final boolean fast;
    private float targetYaw;
    private Vec3 rollDirection = Vec3.ZERO;
    private boolean rollRight;
    private int spins;
    protected double rollDistance;
    private int settleTicks;
    protected boolean rolling;
    private int ticks;

    public GingyCartwheelGoal(GingyEntity mob) {
        this(mob, false);
    }

    protected GingyCartwheelGoal(GingyEntity mob, boolean fast) {
        this.mob = mob;
        this.fast = fast;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!canCartwheel()) {
            return false;
        }
        if (mob.getRandom().nextInt(reducedTickDelay(400)) != 0) return false;
        randomizeSpinsAndSide();
        float bodyYaw = mob.yBodyRot;

        if(mob.getRandom().nextInt(3) == 0 && tryCartwheelTowards(bodyYaw)) {
            return true;
        }
        if (tryCartwheel(bodyYaw)) return true;
        this.rollRight = !rollRight;
        return tryCartwheel(bodyYaw);
    }

    protected boolean canCartwheel() {
        return !mob.isOrderedToSit() && !mob.isPassenger() && !mob.isVehicle() && mob.onGround() && !mob.isInWaterOrBubble();
    }

    protected void randomizeSpinsAndSide() {
        RandomSource random = mob.getRandom();
        this.spins = random.nextInt(5) == 0 ? 2 : 1;
        this.rollRight = random.nextBoolean();
    }

    //turn sideways first
    protected boolean tryCartwheelTowards(float yaw) {
        return tryCartwheel(yaw + (rollRight ? -90 : 90));
    }

    private boolean tryCartwheel(float yaw) {
        float rad = yaw * Mth.DEG_TO_RAD;
        Vec3 left = new Vec3(Mth.cos(rad), 0, Mth.sin(rad));
        Vec3 dir = rollRight ? left.reverse() : left;
        double distance = Mth.TWO_PI * GingyEntity.CARTWHEEL_RADIUS * mob.getBbHeight() * spins;

        AABB box = mob.getBoundingBox();

        if (mob.level().noCollision(mob, box.move(dir.scale(distance * 0.5))) && canStandAt(box.move(dir.scale(distance)))) {
            this.targetYaw = yaw;
            this.rollDirection = dir;
            this.rollDistance = distance;
            return true;
        }
        return false;
    }

    private boolean canStandAt(AABB box) {
        Level level = mob.level();
        return !level.noCollision(mob, box.move(0, -1, 0)) && level.noCollision(mob, box) && !level.containsAnyLiquid(box);
    }

    @Override
    public void start() {
        this.ticks = 0;
        this.settleTicks = 0;
        this.rolling = false;
        mob.getNavigation().stop();
        if (targetYaw == mob.yBodyRot) {
            mob.setYRot(targetYaw);
            mob.setYHeadRot(targetYaw);
            this.settleTicks = 3;
        } else {
            mob.startCartwheelTurn();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !rolling || mob.isCartwheeling();
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (!rolling) {
            float yaw = Mth.approachDegrees(mob.getYRot(), targetYaw, 20);
            mob.setYRot(yaw);
            mob.setYHeadRot(yaw);
            mob.setYBodyRot(yaw);
            //let client catch up
            if (Mth.degreesDifferenceAbs(yaw, targetYaw) < 1 && ++settleTicks > 3) {
                startRolling();
            }
            return;
        }
        int duration = mob.getCartwheelDuration();
        // same easing as spin
        double speed = rollDistance * Mth.PI / (2 * duration) * Mth.sin(Mth.PI * (ticks + 0.5f) / duration);
        Vec3 v = mob.getDeltaMovement();
        mob.setDeltaMovement(rollDirection.x * speed, v.y, rollDirection.z * speed);
        ticks++;
    }

    private void startRolling() {
        this.rolling = true;
        mob.startCartwheel(rollRight, spins, fast);
    }

}