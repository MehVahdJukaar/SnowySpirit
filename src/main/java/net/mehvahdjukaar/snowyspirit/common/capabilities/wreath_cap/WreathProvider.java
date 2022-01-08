package net.mehvahdjukaar.snowyspirit.common.capabilities.wreath_cap;

import net.mehvahdjukaar.snowyspirit.common.capabilities.CapabilityHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

//provider & instance
public class WreathProvider implements IWreathProvider, ICapabilitySerializable<CompoundTag> {

    private final LazyOptional<IWreathProvider> lazyOptional = LazyOptional.of(() -> this);

    public Set<BlockPos> wreathBlocks = new HashSet<>();

    public void invalidate() {
        lazyOptional.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityHandler.WREATH_CAPABILITY ?
                lazyOptional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag total = new CompoundTag();
        int i = 0;
        for (BlockPos pos : wreathBlocks) {
            NbtUtils.writeBlockPos(pos);
            CompoundTag tag = new CompoundTag();
            tag.put("Pos", NbtUtils.writeBlockPos(pos));
            total.put(i + "", tag);
            i++;
        }
        total.putInt("Count", i);
        return total;
    }

    @Override
    public void deserializeNBT(CompoundTag total) {
        for (int i = 0; i < total.getInt("Count"); i++) {
            CompoundTag tag = total.getCompound(i + "");
            BlockPos pos = NbtUtils.readBlockPos(tag);
            wreathBlocks.add(pos);
        }
    }

    @Override
    public void addWreath(BlockPos pos) {
        wreathBlocks.add(pos);
        //engravedColors.put(pos, GeneralUtility.getBrightestColorFromBlock(Minecraft.getInstance().level.getBlockState(pos).getBlock(), pos));
        // CHANNEL.sendToServer(new UpdateServerEngravedBlocks(wreathBlocks, engravedFaces, engravedColors));
    }

    public void removeWreath(BlockPos pos) {
        wreathBlocks.remove(pos);
    }

    @Override
    public Set<BlockPos> getWreathBlocks() {
        return wreathBlocks;
    }

    public void setWreathBlocks(HashSet<BlockPos> wreathBlocks) {
        this.wreathBlocks = wreathBlocks;
    }

    @Override
    public boolean hasWreath(BlockPos pos) {
        return this.wreathBlocks.contains(pos);
    }

    @Override
    public void updateWeathBlock(BlockPos pos, boolean hasWreath, Direction face, boolean open) {
        if(hasWreath){
            this.addWreath(pos);
        }else{
            this.removeWreath(pos);
        }
    }

}