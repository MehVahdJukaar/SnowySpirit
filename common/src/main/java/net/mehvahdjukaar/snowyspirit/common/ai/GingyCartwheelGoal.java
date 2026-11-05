package net.mehvahdjukaar.snowyspirit.common.ai;

import net.mehvahdjukaar.snowyspirit.common.entity.GingyEntity;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GingyCartwheelGoal extends Goal {

    private final GingyEntity mob;
    private Vec3 rollDirection = Vec3.ZERO;
    private boolean rollRight;
    private int spins;
    private double rollDistance;
    private int ticks;

    public GingyCartwheelGoal(GingyEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (mob.isOrderedToSit() || mob.isPassenger() || mob.isVehicle() || !mob.onGround() || mob.isInWaterOrBubble()) {
            return false;
        }
        if (mob.getRandom().nextInt(reducedTickDelay(400)) != 0) {
            return false;
        }
        boolean right = mob.getRandom().nextBoolean();
        this.spins = mob.getRandom().nextInt(3) == 0 ? 2 : 1;
        return trySide(right) || trySide(!right);
    }

    private boolean trySide(boolean right) {
        float yaw = mob.yBodyRot * Mth.DEG_TO_RAD;
        Vec3 left = new Vec3(Mth.cos(yaw), 0, Mth.sin(yaw));
        Vec3 dir = right ? left.reverse() : left;
        double distance = Mth.TWO_PI * GingyEntity.CARTWHEEL_RADIUS * mob.getBbHeight() * spins;

        Level level = mob.level();
        AABB box = mob.getBoundingBox();
        AABB landing = box.move(dir.scale(distance));
        boolean pathFree = level.noCollision(mob, box.move(dir.scale(distance * 0.5))) && level.noCollision(mob, landing);
        boolean hasGround = !level.noCollision(mob, landing.move(0, -1, 0));
        if (pathFree && hasGround && !level.containsAnyLiquid(landing)) {
            this.rollDirection = dir;
            this.rollRight = right;
            this.rollDistance = distance;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        this.ticks = 0;
        mob.getNavigation().stop();
        mob.setYRot(mob.yBodyRot);
        mob.setYHeadRot(mob.yBodyRot);
        mob.startCartwheel(rollRight, spins);
        playStepSound(1.3f);
    }

    @Override
    public void stop() {
        playStepSound(1f);
    }

    private void playStepSound(float pitch) {
        mob.playSound(ModRegistry.GINGERBREAD_BLOCK.get().defaultBlockState().getSoundType().getStepSound(), 0.6f, pitch);
    }

    @Override
    public boolean canContinueToUse() {
        return mob.isCartwheeling();
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
        int duration = mob.getCartwheelDuration();
        // same easing as spin
        double speed = rollDistance * Mth.PI / (2 * duration) * Mth.sin(Mth.PI * (ticks + 0.5f) / duration);
        Vec3 v = mob.getDeltaMovement();
        mob.setDeltaMovement(rollDirection.x * speed, v.y, rollDirection.z * speed);
        ticks++;
    }


}
