package net.mehvahdjukaar.snowyspirit.mixins.forge;

import net.mehvahdjukaar.snowyspirit.common.entity.ContainerHolderEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(ContainerHolderEntity.class)
public abstract class SelfContainerHolderEntityMixin extends Entity implements Container {

    protected SelfContainerHolderEntityMixin(EntityType<?> arg, Level arg2) {
        super(arg, arg2);
    }

    // Forge Start
    @Unique
    private LazyOptional<?> snowyspirit_multi$itemHandler = LazyOptional.of(() -> new InvWrapper(this));


    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
        if (this.isAlive() && capability == ForgeCapabilities.ITEM_HANDLER)
            return snowyspirit_multi$itemHandler.cast();

        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.snowyspirit_multi$itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.snowyspirit_multi$itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    }

}
