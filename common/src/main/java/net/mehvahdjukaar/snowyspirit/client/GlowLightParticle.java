package net.mehvahdjukaar.snowyspirit.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.moonlight.api.util.math.MthUtils;
import net.mehvahdjukaar.supplementaries.client.particles.FeatherParticle;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class GlowLightParticle extends TextureSheetParticle {

    private final float scale;
    private float oldQuadSize;
    private float deltaRot;

    private GlowLightParticle(ClientLevel arg, double d, double e, double f) {
        super(arg, d, e, f);
        this.gravity = 0.0F;
        this.lifetime = 40 + this.random.nextInt(10);
        this.hasPhysics = false;
        this.alpha = 0;
        this.quadSize = 0;
        this.bbHeight = 0.2f;
        this.bbWidth = 0.2f;
        this.deltaRot = MthUtils.nextWeighted(this.random, 0.03f);
        this.scale = 0.07f + this.random.nextFloat() * 0.2f;
        this.roll = (float) (Math.PI * this.random.nextFloat());
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        //super.render(buffer, renderInfo, partialTicks);
        var magicBuffer = SammysParticleHacks.getMagicBuffer();
        float a = this.alpha;
        float old = this.quadSize;
        float oldO = this.oldQuadSize;

        this.alpha = a * 0.4f;
        super.render(magicBuffer, renderInfo, partialTicks);

        this.quadSize *= 0.5;
        this.oldQuadSize *= 0.5;
        this.alpha *= 0.65;
        super.render(magicBuffer, renderInfo, partialTicks);

        this.quadSize *= 0.5;
        this.oldQuadSize *= 0.5;
        this.alpha *= 0.8;
        super.render(magicBuffer, renderInfo, partialTicks);
        this.quadSize *= 0.5;
        this.oldQuadSize *= 0.5;
        this.alpha = a;
        super.render(magicBuffer, renderInfo, partialTicks);
        this.quadSize = old;
        this.oldQuadSize = oldO;


    }

    @Override
    public ParticleRenderType getRenderType() {
        return SammysParticleHacks.GLOW_LIGHT_PARTICLE_RENDER_TYPE;
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return Mth.lerp(partialTicks, oldQuadSize, quadSize);
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = Mth.sin((float) (Math.PI * this.age / this.lifetime));
        this.oldQuadSize = this.quadSize;
        this.quadSize = (float) (this.scale * Math.pow(Mth.sin((float) (Math.PI * this.age / (this.lifetime))), 3f));
        this.oRoll = this.roll;
        this.roll += this.deltaRot;
    }

    @Override
    protected int getLightColor(float partialTick) {

        int i = super.getLightColor(partialTick);
        int k = i >> 16 & 0xFF;
        int a = (int) (255 * Mth.sin((float) (Math.PI * this.age / this.lifetime)));

        return a | k << 16;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z, double reg, double green, double blue) {
            var p = new GlowLightParticle(level, x, y, z);
            p.setSpriteFromAge(sprites);
            p.setColor((float) reg, (float) green, (float) blue);
            return p;
        }
    }
}
