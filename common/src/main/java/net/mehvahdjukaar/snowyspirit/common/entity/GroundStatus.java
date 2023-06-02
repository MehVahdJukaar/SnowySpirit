package net.mehvahdjukaar.snowyspirit.common.entity;

import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.mehvahdjukaar.snowyspirit.configs.CommonConfigs;
import net.mehvahdjukaar.snowyspirit.reg.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum GroundStatus {
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

    private Pair<GroundStatus, Float> withFriction(float friction){
        return Pair.of(this, friction);
    }

    /**
     * Decides how much the boat should be gliding on the land (based on any slippery blocks)
     */
    public static Pair<GroundStatus, Float> computeFriction(Entity sled) {
        if (sled.isInWater()) {
            return IN_WATER.withFriction(1);
        } else {
            Level level = sled.level;
            //boat code here
            AABB aabb = sled.getBoundingBox();
            AABB aabb1 = new AABB(aabb.minX, aabb.minY - 0.001D, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ);
            int i = Mth.floor(aabb1.minX) - 1;
            int j = Mth.ceil(aabb1.maxX) + 1;
            int k = Mth.floor(aabb1.minY) - 1;
            int l = Mth.ceil(aabb1.maxY) + 1;
            int i1 = Mth.floor(aabb1.minZ) - 1;
            int j1 = Mth.ceil(aabb1.maxZ) + 1;
            VoxelShape voxelshape = Shapes.create(aabb1);
            float cumulativeFriction = 0.0F;
            int blockCount = 0;
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
                                final double snowFriction = CommonConfigs.SNOW_FRICTION.get();
                                BlockState above = level.getBlockState(mutable.above());
                                if (above.getBlock() instanceof SnowLayerBlock ||
                                        (above.hasProperty(SnowLayerBlock.LAYERS) && above.is(ModTags.SLED_SNOW))) {
                                    onSnowLayer = true;
                                    cumulativeFriction += snowFriction;
                                    ++blockCount;
                                    continue;
                                }
                                BlockState blockstate = level.getBlockState(mutable);
                                if (blockstate.is(ModTags.SLED_SNOW)) {
                                    onSnow = true;
                                    cumulativeFriction += snowFriction;
                                    ++blockCount;
                                } else if (blockstate.is(ModTags.SLED_SAND)) {
                                    //sand friction
                                    cumulativeFriction += CommonConfigs.SAND_FRICTION.get();
                                    ++blockCount;
                                } else if (Shapes.joinIsNotEmpty(blockstate.getCollisionShape(level, mutable).move(l1, k2, i2), voxelshape, BooleanOp.AND)) {
                                    //decreases friction for blocks and ice in particular
                                    float fr = ForgeHelper.getFriction(blockstate, level, mutable, sled);
                                    if (fr > 0.9) fr *= CommonConfigs.ICE_FRICTION_MULTIPLIER.get();
                                    cumulativeFriction += fr;
                                    ++blockCount;
                                }
                            }
                        }
                    }
                }
            }
            if (cumulativeFriction <= 0) {
                return IN_AIR.withFriction(0);
            }

            float friction = cumulativeFriction / blockCount;
            if (sled.isOnGround()) {
                //alters friction when on slope
                double slopeFriction = Mth.clamp(sled.getXRot(), -45, 45) / 45f;
                friction += CommonConfigs.SLOPE_FRICTION_INCREASE.get() * slopeFriction;
            }

            float landFriction = Math.min(0.9995f, friction);
            if (onSnowLayer) return ON_SNOW_LAYER.withFriction(landFriction);
            if (onSnow) return ON_SNOW.withFriction(landFriction);
            return ON_LAND.withFriction(landFriction);
        }
    }


}
