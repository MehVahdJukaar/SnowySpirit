package net.mehvahdjukaar.snowyspirit.integration;

import net.mehvahdjukaar.snowyspirit.common.entity.ContainerHolderEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.BuiltInLootrTypes;
import noobanidus.mods.lootr.common.api.ILootrType;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.ILootrInfo;
import noobanidus.mods.lootr.common.api.data.LootrBlockType;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class LootrCompat {

    public static boolean tryOpenLootrContainer(ContainerHolderEntity entity, RandomizableContainerBlockEntity inner, ServerPlayer player) {
        if (inner.getLootTable() == null) return false;

        LootrAPI.handleProviderOpen(new SledContainerLootrInfoProvider(entity, inner, 
                ILootrInfo.generateInfoKey(entity.getUUID())), player);
        return true;
    }

    
    
    public record SledContainerLootrInfoProvider(
            ContainerHolderEntity entity, RandomizableContainerBlockEntity inner,
            String cachedId) implements ILootrEntity {
        @Override
        @Deprecated
        public LootrBlockType getInfoBlockType() {
            return LootrBlockType.ENTITY;
        }

        @Override
        public ILootrType getInfoNewType() {
            return BuiltInLootrTypes.MINECART;
        }

        @Override
        public @NotNull Vec3 getInfoVec() {
            return entity.position();
        }


        @Override
        public @NotNull UUID getInfoUUID() {
            return entity.getUUID();
        }

        @Override
        public String getInfoKey() {
            return cachedId();
        }

        @Override
        public boolean hasBeenOpened() {
            return false;
        }

        @Override
        public boolean isPhysicallyOpen() {
            return false;
        }

        @Override
        public @NotNull BlockPos getInfoPos() {
            return entity.blockPosition();
        }

        @Override
        public ResourceKey<LootTable> getInfoLootTable() {
            return inner.getLootTable();
        }

        @Override
        public @Nullable Component getInfoDisplayName() {
            return entity.getDisplayName();
        }

        @Override
        public @NotNull ResourceKey<Level> getInfoDimension() {
            return entity.level().dimension();
        }

        @Override
        public int getInfoContainerSize() {
            return entity.getContainerSize();
        }

        @Override
        public @Nullable NonNullList<ItemStack> getInfoReferenceInventory() {
            return null;
        }

        @Override
        public boolean isInfoReferenceInventory() {
            return false;
        }

        @Override
        public long getInfoLootSeed() {
            return inner.getLootTableSeed();
        }

        @Override
        public void setLootTableInternal(ResourceKey<LootTable> table, long seed) {
            inner.setLootTable(table, seed);
        }

        @Override
        public Level getInfoLevel() {
            return entity.level();
        }

        @Override
        public Container getInfoContainer() {
            return entity;
        }

        @Override
        public void markChanged() {
            entity.setChanged();
        }

        @Override
        public @Nullable Set<UUID> getClientOpeners() {
            return null;
        }

        @Override
        public boolean isClientOpened() {
            return false;
        }

        @Override
        public void setClientOpened(boolean opened) {
        }
    }

}
