package net.mehvahdjukaar.snowyspirit.common.ai;

import net.mehvahdjukaar.snowyspirit.common.entity.GingyEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class GingySitWhenOrderedToGoal extends Goal {
    private final GingyEntity mob;

    public GingySitWhenOrderedToGoal(GingyEntity tamableAnimal) {
        this.mob = tamableAnimal;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isOrderedToSit();
    }

    @Override
    public boolean canUse() {
        if (this.mob.getOwnerUUID() == null) {
            return false;
        } else if (this.mob.isInWaterOrBubble()) {
            return false;
        } else if (!this.mob.onGround()) {
            return false;
        } else {
            LivingEntity owner = this.mob.getOwner();
            if (owner == null) {
                return this.mob.isOrderedToSit();
            } else {
                return ((this.mob.distanceToSqr(owner) >= 144.0) || owner.getLastHurtByMob() == null) && this.mob.isOrderedToSit();
            }
        }
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
    }
}
