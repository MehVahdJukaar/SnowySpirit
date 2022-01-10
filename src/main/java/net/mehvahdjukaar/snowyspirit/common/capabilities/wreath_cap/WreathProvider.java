//package net.mehvahdjukaar.snowyspirit.common.capabilities.wreath_cap;
//
//import net.mehvahdjukaar.snowyspirit.common.capabilities.CapabilityHandler;
//import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
//import net.minecraft.client.multiplayer.ClientLevel;
//import net.minecraft.client.renderer.LevelRenderer;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.NbtUtils;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.world.entity.item.ItemEntity;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.*;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.properties.DoorHingeSide;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.ICapabilitySerializable;
//import net.minecraftforge.common.util.LazyOptional;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
////provider & instance
//public class WreathProvider implements IWreathProvider, ICapabilitySerializable<CompoundTag> {
//
//    private final LazyOptional<IWreathProvider> lazyOptional = LazyOptional.of(() -> this);
//
//    public Map<BlockPos, WreathData> wreathBlocks = new HashMap<>();
//
//    public void invalidate() {
//        lazyOptional.invalidate();
//    }
//
//    @Nonnull
//    @Override
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
//        return cap == CapabilityHandler.WREATH_CAPABILITY ?
//                lazyOptional.cast() : LazyOptional.empty();
//    }
//
//    @Override
//    public CompoundTag serializeNBT() {
//        CompoundTag total = new CompoundTag();
//        int i = 0;
//        for (BlockPos pos : wreathBlocks.keySet()) {
//            NbtUtils.writeBlockPos(pos);
//            total.put(i + "", NbtUtils.writeBlockPos(pos));
//            i++;
//        }
//        total.putInt("Count", i);
//        return total;
//    }
//
//    @Override
//    public void deserializeNBT(CompoundTag total) {
//        for (int i = 0; i < total.getInt("Count"); i++) {
//            CompoundTag tag = total.getCompound(i + "");
//            BlockPos pos = NbtUtils.readBlockPos(tag);
//            this.addWreath(pos, Direction.NORTH, true, true);
//        }
//    }
//
//    @Override
//    public void addWreath(BlockPos pos, Direction direction, boolean open, boolean hinge) {
//        wreathBlocks.put(pos, new WreathData(direction, open, hinge));
//        //engravedColors.put(pos, GeneralUtility.getBrightestColorFromBlock(Minecraft.getInstance().level.getBlockState(pos).getBlock(), pos));
//        // CHANNEL.sendToServer(new UpdateServerEngravedBlocks(wreathBlocks, engravedFaces, engravedColors));
//    }
//
//    public void removeWreath(BlockPos pos) {
//        wreathBlocks.remove(pos);
//    }
//
//    @Override
//    public Map<BlockPos, WreathData> getWreathBlocks() {
//        return wreathBlocks;
//    }
//
//    @Override
//    public boolean hasWreath(BlockPos pos) {
//        return this.wreathBlocks.containsKey(pos);
//    }
//
//    @Override
//    public void updateWeathBlock(BlockPos pos, Level level) {
//        BlockState state = level.getBlockState(pos);
//        if (state.getBlock() instanceof DoorBlock) {
//            Direction dir = state.getValue(DoorBlock.FACING);
//            boolean open = state.getValue(DoorBlock.OPEN);
//            boolean hinge = state.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
//            this.addWreath(pos, dir, open, hinge);
//        } else {
//            this.removeWreath(pos);
//        }
//    }
//
//    @Override
//    public void updateAllBlocksClient(ClientLevel level) {
//        Set<BlockPos> positions = new HashSet<>(this.wreathBlocks.keySet());
//        positions.forEach(p -> updateWeathBlock(p, level));
//    }
//
//    @Override
//    public void updateAllBlocks(ServerLevel level) {
//        Set<BlockPos> positions = new HashSet<>(this.wreathBlocks.keySet());
//        positions.forEach(p -> {
//            BlockState state = level.getBlockState(p);
//            if (!(state.getBlock() instanceof DoorBlock)) {
//                this.removeWreath(p);
//                ItemEntity itementity = new ItemEntity(level, p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5,
//                        ModRegistry.WREATH_ITEM.get().getDefaultInstance());
//                itementity.setDefaultPickUpDelay();
//                level.addFreshEntity(itementity);
//                level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, p, Block.getId(ModRegistry.WREATH.get().defaultBlockState()));
//                //drop
//            }
//        });
//    }
//
//
//
//    ;
//}