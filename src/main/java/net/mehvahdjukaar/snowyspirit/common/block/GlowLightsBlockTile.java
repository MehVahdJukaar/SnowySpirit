package net.mehvahdjukaar.snowyspirit.common.block;

import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.Objects;

public class GlowLightsBlockTile extends BlockEntity {

    public BlockState mimic = Blocks.OAK_LEAVES.defaultBlockState();
    public static final ModelProperty<BlockState> MIMIC = new ModelProperty<>();

    public GlowLightsBlockTile(BlockPos pos, BlockState state) {
        super(ModRegistry.GLOW_LIGHTS_BLOCK_TILE.get(), pos, state);
    }

    public static boolean isValidBlock(BlockState state, BlockPos pos, Level world) {
        if (state == null) return false;
        Block b = state.getBlock();
        if (b instanceof EntityBlock) {
            return false;
        }
        return state.is(BlockTags.LEAVES) && Block.isShapeFullBlock(state.getCollisionShape(world, pos));
    }

    @Override
    public IModelData getModelData() {
        //return data;
        return new ModelDataMap.Builder()
                .withInitial(MIMIC, this.mimic)
                .build();
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.mimic = NbtUtils.readBlockState(compound.getCompound("Mimic"));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Mimic", NbtUtils.writeBlockState(mimic));
    }

    //client
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        //this.load(this.getBlockState(), pkt.getTag());
        BlockState oldMimic = this.mimic;
        CompoundTag tag = pkt.getTag();
        handleUpdateTag(tag);
        if (!Objects.equals(oldMimic, this.mimic)) {
            //not needed cause model data doesn't create new obj. updating old one instead
            ModelDataManager.requestModelDataRefresh(this);
            //this.data.setData(MIMIC, this.getHeldBlock());
            if (this.level != null) {
                this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
            }
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public void acceptBlock(BlockState state) {
        this.mimic = state;
        if (level.isClientSide) {
            ModelDataManager.requestModelDataRefresh(this);
        } else {
            this.setChanged();
        }
    }
}