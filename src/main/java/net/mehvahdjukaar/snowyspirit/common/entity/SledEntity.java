package net.mehvahdjukaar.snowyspirit.common.entity;

import com.google.common.collect.Lists;
import net.mehvahdjukaar.snowyspirit.common.IInputListener;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class SledEntity extends Entity implements IInputListener {
    private static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(SledEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_HURT_DIR = SynchedEntityData.defineId(SledEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(SledEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(SledEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SEAT_TYPE = SynchedEntityData.defineId(SledEntity.class, EntityDataSerializers.INT);

    private float deltaRotation;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    private boolean inputLeft;
    private boolean inputRight;
    private boolean inputUp;
    private boolean inputDown;
    private float landFriction;
    private Status status;
    private Status oldStatus;

    public SledEntity(EntityType<? extends SledEntity> p_38290_, Level p_38291_) {
        super(p_38290_, p_38291_);
        this.blocksBuilding = true;
        this.maxUpStep = 1;
    }

    public SledEntity(Level level, double x, double y, double z) {
        this(ModRegistry.SLED.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("Type", this.getWoodType().getName());
        if(this.getSeatType() != null) {
            tag.putInt("Seat", this.getSeatType().getId());
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Type", 8)) {
            this.setWoodType(Boat.Type.byName(tag.getString("Type")));
        }
        if (tag.contains("Seat", 8)) {
            this.setSeatType(DyeColor.byId(tag.getInt("Seat")));
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_TYPE, Boat.Type.OAK.ordinal());
        this.entityData.define(DATA_SEAT_TYPE, 0);
        this.entityData.define(DATA_ID_HURT, 0);
        this.entityData.define(DATA_ID_HURT_DIR, 1);
        this.entityData.define(DATA_ID_DAMAGE, 0.0F);
    }


    //maybe if it can be controlled like a horse?
    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    public boolean canCollideWith(Entity entity) {
        return Boat.canVehicleCollide(this, entity);
    }

    //portal stuff
    @Override
    protected Vec3 getRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle rectangle) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(axis, rectangle));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.level.isClientSide && !this.isRemoved()) {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.setDamage(this.getDamage() + amount * 10.0F);
            this.markHurt();
            this.gameEvent(GameEvent.ENTITY_DAMAGED, source.getEntity());
            boolean isCreative = source.getEntity() instanceof Player player && player.getAbilities().instabuild;
            if (isCreative || this.getDamage() > 40.0F) {
                if (!isCreative && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.spawnAtLocation(this.getDropItem());
                    DyeColor seat = this.getSeatType();
                    if(seat != null){
                       this.spawnAtLocation(ModRegistry.CARPETS.get(seat));
                    }
                }
                this.discard();
            }
        }
        return true;
    }

    @Override
    public void animateHurt() {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() * 11.0F);
    }


    /**
     * Applies a velocity to the entities, to push them away from eachother.
     */
    @Override
    public void push(@NotNull Entity pEntity) {
        if (pEntity instanceof Boat) {
            if (pEntity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push(pEntity);
            }
        } else if (pEntity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(pEntity);
        }
    }


    /**
     * Sets a target for the client to interpolate towards over the next few ticks
     */
    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int posRotationIncrements, boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYRot = yRot;
        this.lerpXRot = xRot;
        //ticks it takes to lerp to
        this.lerpSteps = 10;
    }

    @Override
    protected AABB getBoundingBoxForPose(Pose pPose) {
        return super.getBoundingBoxForPose(pPose);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling();
    }

    /**
     * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into account.
     */
    @Override
    public Direction getMotionDirection() {
        return this.getDirection().getClockWise();
    }


    //relative
    public double additionalY = 0;
    public double prevAdditionalY = 0;
    public Vec3 projectedPos = Vec3.ZERO;
    public Vec3 prevProjectedPos = Vec3.ZERO;
    public Vec3 prevDeltaMovement = Vec3.ZERO;
    public boolean boost = false;
    public double misalignedFriction = 1;

    @Override
    public void move(MoverType pType, Vec3 pPos) {
        super.move(pType, pPos);
        this.prevProjectedPos = this.projectedPos;
        this.projectedPos = Vec3.ZERO;

        //considered on ground even when in air but with block below
        if (!this.onGround) {
            float belowCheck = -1.25f;
            Vec3 blockBelow = this.findNextPos(new Vec3(0, belowCheck, 0));
            if (blockBelow.y > belowCheck + 0.01) {
                this.onGround = true;
            }
        }

        if (this.onGround) {

            //this.projectedPos = this.findNextPos(this.getLookAngle().scale(this.getDeltaMovement().length()).scale(6));
            this.projectedPos = this.findNextPos(this.getDeltaMovement().scale(6));
            double y = Mth.clamp(this.projectedPos.y, -1, 1);
            if (y == 0) {
                //reset
                this.setXRot(this.getXRot() + -this.getXRot() * 0.3f);
            } else if (y > 0) {
                //up
                this.setXRot((float) Math.max(this.getXRot() - 6f, -40 * y));
            } else {
                //down
                this.setXRot((float) Math.min(this.getXRot() + 3f, -40 * y));
            }
        }

        this.prevAdditionalY = this.additionalY;
        if (this.projectedPos.y > 0) {
            double slopeIncrement = (projectedPos.y + 0.01) / 2.5d;
            this.additionalY = Math.min(projectedPos.y, this.additionalY + slopeIncrement);
        } else {
            //adjust bounding box
            if (this.additionalY > 0) {
                this.setBoundingBox(this.makeBoundingBox());
            }
            this.additionalY = 0;
        }
        //raise when on snow layer
        if (this.status == Status.ON_SNOW_LAYER && this.additionalY < 0.0625) {
            this.additionalY += 0.0625;
        }
    }

    @Override
    public void tick() {
        this.oldStatus = this.status;
        this.status = this.getStatusAndUpdateFriction();


        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        this.prevDeltaMovement = this.getDeltaMovement();

        super.tick();
        this.tickLerp();

        Vec3 movement = this.getDeltaMovement();
        double speed = movement.lengthSqr();

        if (this.level.isClientSide && this.status.onSnow()) {
            float horSpeed = (float) (speed - (movement.y * movement.y));
            if (this.random.nextFloat() * 0.16f < horSpeed) {
                float up = (float) Math.min(horSpeed * 0.6, 0.3);

                float xRot = this.getXRot();
                float yRot = this.getYRot();
                Vec3 a = this.calculateViewVector(xRot, yRot + 20);
                Vec3 b = this.calculateViewVector(xRot, yRot - 20);
                Vec3 left = a.scale(-1f).add(this.position());
                Vec3 right = b.scale(-1f).add(this.position());
                level.addParticle(ParticleTypes.SNOWFLAKE,
                        left.x + Mth.randomBetween(random, -0.2F, 0.2F),
                        left.y + 0.2,
                        left.z + Mth.randomBetween(random, -0.2F, 0.2F),
                        Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F,
                        0.015 + random.nextFloat(0.1f) + up,
                        Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F);
                level.addParticle(ParticleTypes.SNOWFLAKE,
                        right.x + Mth.randomBetween(random, -0.2F, 0.2F),
                        right.y + 0.2,
                        right.z + Mth.randomBetween(random, -0.2F, 0.2F),
                        Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F,
                        0.015 + random.nextFloat(0.1f) + up,
                        Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F);

            }
        }

        //decrese step height with low speed
        // this.maxUpStep = (float) Mth.clamp(speed * 8 + this.additionalY * 1, 0.5, 1);
        this.boost = false;
        //slope deceleration/ acceleration
        //check if on next pos is down or up and if has block relatively near below (ie on ground but with more leeway)
        if (this.projectedPos.y != 0 && this.onGround) {
            double k = Mth.clamp(this.projectedPos.y, -1, 1);
            if (k > 0) {
                //decelerate uphill
                this.setDeltaMovement(movement.scale(1 + -0.05 * k));
            } else {
                //boost downhill
                this.boost = true;
                //gives downward velocity to keep on the slope
                this.setDeltaMovement(movement.add(movement.normalize().scale(k * -0.01f)).add(0, -0.2, 0));
            }
        }

        //local player controlling code
        if (this.isControlledByLocalInstance()) {

            this.applyFriction();
            if (this.level.isClientSide) {
                this.controlSled();
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }

        this.checkInsideBlocks();

        List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(0.2F, (double) -0.01F, (double) 0.2F), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            boolean notLocalPlayerControlled = !this.level.isClientSide && !(this.getControllingPassenger() instanceof Player);

            for (Entity entity : list) {
                if (!entity.hasPassenger(this)) {
                    if (notLocalPlayerControlled && this.getPassengers().size() < 2 && !entity.isPassenger() && entity.getBbWidth() < this.getBbWidth() && entity instanceof LivingEntity && !(entity instanceof WaterAnimal) && !(entity instanceof Player)) {
                        entity.startRiding(this);
                    } else {
                        this.push(entity);
                    }
                }
            }
        }
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        if (this.additionalY > 0) {
            return super.makeBoundingBox().expandTowards(0, additionalY, 0);
        }
        return super.makeBoundingBox();
    }

    /**
     * Given a motion vector, return an updated vector that takes into account restrictions such as collisions (from all
     * directions) and step-up from stepHeight
     */
    private Vec3 findNextPos(Vec3 pVec) {
        AABB aabb = this.getBoundingBox();
        List<VoxelShape> list = this.level.getEntityCollisions(this, aabb.expandTowards(pVec));
        Vec3 vec3 = pVec.lengthSqr() == 0.0D ? pVec : collideBoundingBox(this, pVec, aabb, this.level, list);
        boolean changedX = pVec.x != vec3.x;
        boolean changedY = pVec.y != vec3.y;
        boolean changedZ = pVec.z != vec3.z;
        boolean ySomething = this.onGround || changedY && pVec.y < 0.0D;
        if (this.maxUpStep > 0.0F && ySomething && (changedX || changedZ)) {
            Vec3 vec31 = collideBoundingBox(this, new Vec3(pVec.x, this.maxUpStep, pVec.z), aabb, this.level, list);
            Vec3 vec32 = collideBoundingBox(this, new Vec3(0.0D, this.maxUpStep, 0.0D), aabb.expandTowards(pVec.x, 0.0D, pVec.z), this.level, list);
            if (vec32.y < (double) this.maxUpStep) {
                Vec3 vec33 = collideBoundingBox(this, new Vec3(pVec.x, 0.0D, pVec.z), aabb.move(vec32), this.level, list).add(vec32);
                if (vec33.horizontalDistanceSqr() > vec31.horizontalDistanceSqr()) {
                    vec31 = vec33;
                }
            }

            if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr()) {
                return vec31.add(collideBoundingBox(this, new Vec3(0.0D, -vec31.y + pVec.y, 0.0D), aabb.move(vec31), this.level, list));
            }
        }
        //hack to get down pos
        Vec3 cached = this.position();
        this.setPos(cached.add(vec3));
        Vec3 down = collideBoundingBox(this, new Vec3(0, -1, 0), this.getBoundingBox(), this.level, list);
        this.setPos(cached);
        return vec3.add(down);
    }


    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.setPacketCoordinates(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            double d3 = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
            this.setYRot(this.getYRot() + (float) d3 / (float) this.lerpSteps);
            this.setXRot(this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            this.setRot(this.getYRot(), this.getXRot());
        }
    }

    /**
     * Decides how much the boat should be gliding on the land (based on any slippery blocks)
     */
    public Status getStatusAndUpdateFriction() {
        if (this.isInWater()) {
            return Status.IN_WATER;
        } else {

            AABB aabb = this.getBoundingBox();
            AABB aabb1 = new AABB(aabb.minX, aabb.minY - 0.001D, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ);
            int i = Mth.floor(aabb1.minX) - 1;
            int j = Mth.ceil(aabb1.maxX) + 1;
            int k = Mth.floor(aabb1.minY) - 1;
            int l = Mth.ceil(aabb1.maxY) + 1;
            int i1 = Mth.floor(aabb1.minZ) - 1;
            int j1 = Mth.ceil(aabb1.maxZ) + 1;
            VoxelShape voxelshape = Shapes.create(aabb1);
            float f = 0.0F;
            int k1 = 0;
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            boolean onSnow = false;
            boolean onSnowLayer = false;
            for (int l1 = i; l1 < j; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);
                    if (j2 != 2) {
                        for (int k2 = k; k2 < l; ++k2) {
                            if (j2 <= 0 || k2 != k && k2 != l - 1) {
                                mutable.set(l1, k2, i2);
                                BlockState blockstate = this.level.getBlockState(mutable);
                                if (blockstate.is(BlockTags.SNOW)) {
                                    onSnow = true;
                                    f += 0.985F;
                                    ++k1;
                                } else if (this.level.getBlockState(mutable.above()).getBlock() instanceof SnowLayerBlock) {
                                    onSnowLayer = true;
                                    f += 0.985F;
                                    ++k1;
                                } else if (!(blockstate.getBlock() instanceof WaterlilyBlock) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level, mutable).move(l1, k2, i2), voxelshape, BooleanOp.AND)) {
                                    //decreases friction for blocks and ice in particular
                                    float fr = blockstate.getFriction(this.level, mutable, this);
                                    if(fr>0.9)fr*=0.97;
                                    f += fr;
                                    ++k1;
                                }
                            }
                        }
                    }
                }
            }
            if (f <= 0) {
                return Status.IN_AIR;
            }

            float friction = f / (float) k1;
            if (this.onGround) {
                //alters friction when on slope
                double slopeFriction = Mth.clamp(this.getXRot(), -45, 45) / 45f;
                friction += 0.06 * slopeFriction;
            }

            this.landFriction = Math.min(0.9995f, friction);
            if (onSnowLayer) return Status.ON_SNOW_LAYER;
            if (onSnow) return Status.ON_SNOW;
            return Status.ON_LAND;
        }
    }


    private void applyFriction() {
        double gravity = this.isNoGravity() ? 0.0D : (double) -0.04F;

        float invFriction = 0.05F;

        switch (this.status) {
            case IN_AIR -> invFriction = 0.9F;
            case IN_WATER -> invFriction = 0.45f;
            case ON_SNOW, ON_SNOW_LAYER, ON_LAND -> {
                invFriction = this.landFriction;
                if (this.getControllingPassenger() instanceof Player) {
                    this.landFriction /= 2.0F;
                }
            }
        }


        Vec3 movement = this.getDeltaMovement();

        //alters friction when not facing the right way. allows braking
        if(this.status.touchingGround()) {
            //max friction decrement cause by misaligned speed vector
            double inc = 0.825;
            if(this.inputUp || this.inputDown || movement.lengthSqr()>0.001) {
                Vec3 v = new Vec3(0, 0, 1);
                v = v.yRot((float) ((-this.getYRot()) / 180 * Math.PI));

                double dot = v.dot(new Vec3(movement.x, 0, movement.z).normalize());
                inc = Mth.clamp(((dot+3)/4f)+0.005, inc, 1);
            }
            this.misalignedFriction =  (inc*4 -3);
            invFriction *= inc;
        }

        this.setDeltaMovement(movement.x * (double) invFriction, movement.y + gravity, movement.z * (double) invFriction);
        //rotation friction
        //increase friction when steering
        this.deltaRotation *= Math.min(invFriction, (this.inputUp ? 0.7 : 0.9));
    }

    private void controlSled() {
        if (this.isVehicle()) {
            float powah = 0.0F;
            Vec3 movement = this.getDeltaMovement();

            boolean canSteer = !(this.inputRight && this.inputLeft) && this.inputUp;

            if (this.inputLeft) {
                --this.deltaRotation;
                //crappy steering
                if(this.status.touchingGround() && canSteer) {
                    Vec3 v = new Vec3(0, 0, 1);
                    v = v.yRot((float) ((-this.getYRot()) / 180 * Math.PI));

                    double dot = v.dot(movement.normalize());
                    if (dot > 0) {
                        this.setDeltaMovement(movement.yRot((float) (dot * 0.041)));
                    }
                }
            }

            if (this.inputRight) {
                ++this.deltaRotation;
                //steering
                if(this.status.touchingGround() && canSteer) {
                    Vec3 v = new Vec3(0, 0, 1);
                    v = v.yRot((float) ((-this.getYRot()) / 180 * Math.PI));

                    double dot = v.dot(movement.normalize());
                    if (dot > 0.8) {
                        this.setDeltaMovement(movement.yRot((float) (-dot * 0.041)));
                    }
                }
            }

            if (this.inputRight != this.inputLeft && !this.inputUp && !this.inputDown) {
                powah += 0.005F;
            }

            this.setYRot(this.getYRot() + this.deltaRotation);
            if (this.inputUp) {
                if(this.status.onSnow()) powah += 0.017f;//0.04F;
                else powah += 0.04F;
            }

            if (this.inputDown) {
                powah -= 0.005F;
            }


            this.setDeltaMovement(this.getDeltaMovement().add(
                        Mth.sin(-this.getYRot() * ((float) Math.PI / 180F)) * powah,
                        0.0D,
                        Mth.cos(this.getYRot() * ((float) Math.PI / 180F)) * powah));

        }
    }

    protected void clampRotation(Entity entity) {
        entity.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
        float f1 = Mth.clamp(f, -105.0F, 105.0F);
        entity.yRotO += f1 - f;
        entity.setYRot(entity.getYRot() + f1 - f);
        entity.setYHeadRot(entity.getYRot());
    }

    @Override
    protected void checkFallDamage(double p_38307_, boolean pOnGround, BlockState p_38309_, BlockPos p_38310_) {

        if (this.level.isClientSide && fallDistance > 0.7 && this.onGround) {
            if (this.status.onSnow()) {
                float p = Mth.clamp(fallDistance * 4f, 5, 20);
                Vec3 front = this.position().add(this.getLookAngle().scale(0.8f));
                Vec3 mov = this.getDeltaMovement().scale(1.1);
                float ySpeed = (float) (mov.lengthSqr() * 0.15 + fallDistance * 0.005f);
                for (int i = 0; i < p; i++) {

                    level.addParticle(ParticleTypes.SNOWFLAKE,
                            front.x + Mth.randomBetween(random, -0.6F, 0.6F),
                            front.y + 0.2 + Mth.randomBetween(random, -0.1F, 0.2F),
                            front.z + Mth.randomBetween(random, -0.6F, 0.6F),
                            mov.x + Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F,
                            0.1 + random.nextFloat(ySpeed),
                            mov.z + Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F);
                }
            }
        }


        if (!this.isPassenger()) {
            if (pOnGround) {
                if (this.fallDistance > 3.0F) {
                    if (!this.status.touchingGround()) {
                        this.resetFallDistance();
                        return;
                    }

                    //this.causeFallDamage(this.fallDistance, 1.0F, DamageSource.FALL);
                }

                this.resetFallDistance();
            } else if (!this.level.getFluidState(this.blockPosition().below()).is(FluidTags.WATER) && p_38307_ < 0.0D) {
                //this.fallDistance = (float) ((double) this.fallDistance - p_38307_);
            }

        }
    }

    public void setDamage(float p_38312_) {
        this.entityData.set(DATA_ID_DAMAGE, p_38312_);
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE);
    }

    public void setHurtTime(int p_38355_) {
        this.entityData.set(DATA_ID_HURT, p_38355_);
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_ID_HURT);
    }

    public void setHurtDir(int p_38363_) {
        this.entityData.set(DATA_ID_HURT_DIR, p_38363_);
    }

    public int getHurtDir() {
        return this.entityData.get(DATA_ID_HURT_DIR);
    }

    public void setWoodType(Boat.Type type) {
        this.entityData.set(DATA_ID_TYPE, type.ordinal());
    }

    public Boat.Type getWoodType() {
        return Boat.Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    @Nullable
    public DyeColor getSeatType() {
        int d = this.entityData.get(DATA_SEAT_TYPE);
        if (d == 0) return null;
        return DyeColor.byId(d - 1);
    }

    @Nullable
    public void setSeatType(@Nullable DyeColor seatColor) {
        this.entityData.set(DATA_SEAT_TYPE, seatColor == null ? 0 : seatColor.getId() + 1);
    }

    //----passenger stuff-----

    @Override
    public double getPassengersRidingOffset() {
        return 0.2D + this.additionalY;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand pHand) {
        if (!player.isSecondaryUseActive()) {
            ItemStack stack = player.getItemInHand(pHand);
            if(stack.is(ItemTags.CARPETS) && this.getSeatType() == null){
                this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F, 1.0F);

                //will crash with modded carpets. save actial item isntead. depends on implementation if we render carper or not
                this.setSeatType(ModRegistry.CARPETS.inverse().get(stack.getItem()));
                stack.shrink(1);
                return InteractionResult.sidedSuccess(player.level.isClientSide);
            }

            if (!this.level.isClientSide) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            } else {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected boolean canAddPassenger(Entity p_38390_) {
        return this.getPassengers().size() < 2 && !this.isEyeInFluid(FluidTags.WATER);
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        return this.getFirstPassenger();
    }

    @Override
    public void onInputUpdate(boolean left, boolean right, boolean up, boolean down, boolean sprint, boolean jumping) {
        this.inputLeft = left;
        this.inputRight = right;
        this.inputUp = up;
        this.inputDown = down;
    }

    // Forge: Fix MC-119811 by instantly completing lerp on board
    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float) this.lerpYRot, (float) this.lerpXRot);
        }
    }

    @Override
    public void positionRider(Entity entity) {
        if (this.hasPassenger(entity)) {
            float zPos = 0.0F;
            float yPos = (float) ((this.isRemoved() ? 0.01 : this.getPassengersRidingOffset()) + entity.getMyRidingOffset());
            if (this.getPassengers().size() > 1) {
                int i = this.getPassengers().indexOf(entity);
                float cos = Mth.sin((float) (this.getXRot() * Math.PI / 180f));
                if (i == 0) {
                    zPos = 0.1F;
                } else {
                    zPos = -0.8F;
                }
                yPos -= cos * zPos;

            }
            if(entity instanceof Animal){
                if (this.getPassengers().size() > 1) {
                    zPos +=  0.2D;
                }
                yPos += 0.125;
            }

            Vec3 vec3 = (new Vec3(zPos, 0.0D, 0.0D)).yRot(-this.getYRot() * ((float) Math.PI / 180F) - ((float) Math.PI / 2F));
            entity.setPos(this.getX() + vec3.x, this.getY() + (double) yPos, this.getZ() + vec3.z);
            entity.setYRot(entity.getYRot() + this.deltaRotation);
            entity.setYHeadRot(entity.getYHeadRot() + this.deltaRotation);
            this.clampRotation(entity);
            if (entity instanceof Animal animal && this.getPassengers().size() > 1) {
                int yRot = entity.getId() % 2 == 0 ? 90 : 270;
                entity.setYBodyRot(animal.yBodyRot + (float) yRot);
                entity.setYHeadRot(entity.getYHeadRot() + (float) yRot);
            }
        }
    }

    @Override
    public void dismountTo(double pX, double pY, double pZ) {
        this.additionalY = 0;
        this.projectedPos = Vec3.ZERO;
        super.dismountTo(pX, pY, pZ);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity entity) {
        Vec3 vec3 = getCollisionHorizontalEscapeVector(this.getBbWidth() * Mth.SQRT_OF_TWO, (double) entity.getBbWidth(), entity.getYRot());
        double d0 = this.getX() + vec3.x;
        double d1 = this.getZ() + vec3.z;
        BlockPos blockpos = new BlockPos(d0, this.getBoundingBox().maxY, d1);
        BlockPos below = blockpos.below();
        if (!this.level.isWaterAt(below)) {
            List<Vec3> list = Lists.newArrayList();
            double d2 = this.level.getBlockFloorHeight(blockpos);
            if (DismountHelper.isBlockFloorValid(d2)) {
                list.add(new Vec3(d0, (double) blockpos.getY() + d2, d1));
            }

            double d3 = this.level.getBlockFloorHeight(below);
            if (DismountHelper.isBlockFloorValid(d3)) {
                list.add(new Vec3(d0, (double) below.getY() + d3, d1));
            }

            for (Pose pose : entity.getDismountPoses()) {
                for (Vec3 vec31 : list) {
                    if (DismountHelper.canDismountTo(this.level, vec31, entity, pose)) {
                        entity.setPose(pose);
                        return vec31;
                    }
                }
            }
        }
        return super.getDismountLocationForPassenger(entity);
    }

    @Override
    public void onPassengerTurned(Entity entity) {
        this.clampRotation(entity);
    }

    //----end passenger stuff-----

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDropItem());
    }

    public Item getDropItem() {
        return ModRegistry.SLED_ITEMS.get(this.getWoodType()).get();
    }

    public enum Status {
        ON_SNOW,
        ON_SNOW_LAYER,
        ON_LAND,
        IN_WATER,
        IN_AIR;

        public boolean touchingGround() {
            return this != IN_AIR && this != IN_WATER;
        }

        public boolean onSnow() {
            return this == ON_SNOW || this == ON_SNOW_LAYER;
        }
    }


}
