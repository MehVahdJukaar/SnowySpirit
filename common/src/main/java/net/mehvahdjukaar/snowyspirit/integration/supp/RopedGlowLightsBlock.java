package net.mehvahdjukaar.snowyspirit.integration.supp;

import net.mehvahdjukaar.moonlight.api.block.IColored;
import net.mehvahdjukaar.moonlight.api.block.IWashable;
import net.mehvahdjukaar.snowyspirit.PlatStuff;
import net.mehvahdjukaar.snowyspirit.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.mehvahdjukaar.supplementaries.common.block.blocks.RopeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RopedGlowLightsBlock extends RopeBlock implements IColored, IWashable {

    public final DyeColor color;

    public RopedGlowLightsBlock(DyeColor color, Properties properties) {
        super(properties);
        this.color = color;
    }

    @Nullable
    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public boolean supportsBlankColor() {
        return true;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (PlatStuff.isShear(stack)) {
            if (!level.isClientSide) {
                level.setBlockAndUpdate(pos, SuppCompat.glowLightsToRope(state));
                popResource(level, pos, this.lightsStack());
            }
            level.playSound(player, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(SuppCompat.ropeStack(), this.lightsStack());
    }

    @Override
    public boolean tryWash(Level level, BlockPos pos, BlockState state, Vec3 hitPos) {
        if (this.color == DyeColor.WHITE) return false;
        level.setBlockAndUpdate(pos, ModRegistry.ROPED_GLOW_LIGHTS.get(DyeColor.WHITE).get().withPropertiesOf(state));
        return true;
    }

    private ItemStack lightsStack() {
        return ModRegistry.GLOW_LIGHTS_ITEMS.get(this.color).get().getDefaultInstance();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return this.lightsStack();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() > 0.2f) return;
        Direction dir = Direction.values()[random.nextInt(6)];
        if (!this.hasConnection(dir, state)) return;
        double d = random.nextDouble() * 0.45;
        double x = pos.getX() + 0.5 + dir.getStepX() * d;
        double y = pos.getY() + 0.6875 + dir.getStepY() * d;
        double z = pos.getZ() + 0.5 + dir.getStepZ() * d;
        var c = ClientDynamicResourcesHandler.getGlowLightColor(color, random);
        level.addParticle(ModRegistry.GLOW_LIGHT_PARTICLE.get(), x, y, z, c[0], c[1], c[2]);
    }
}
