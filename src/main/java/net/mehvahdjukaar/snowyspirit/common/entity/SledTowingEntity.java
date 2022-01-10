package net.mehvahdjukaar.snowyspirit.common.entity;

import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.entity.PartEntity;

public class SledTowingEntity extends PartEntity<SledEntity> {

    private final EntityDimensions size;

    public SledTowingEntity(SledEntity parent) {
        super(parent);
        this.size = EntityDimensions.scalable(1, 1);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public Packet<?> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    public EntityDimensions getDimensions(Pose pPose) {
        return this.size;
    }

    public void specialTick() {
        this.tick();
    }
}
