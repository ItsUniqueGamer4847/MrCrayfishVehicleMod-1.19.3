package com.mrcrayfish.vehicle.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class DustParticle extends SpriteTexturedParticle
{
    public DustParticle(ClientWorld world, double x, double y, double z, double xd, double yd, double zd)
    {
        super(world, x, y, z);
        this.lifetime = 50 + this.random.nextInt(20);
        this.quadSize = 0.3F;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.alpha = 0.45F;
    }

    @Override
    public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if(this.age++ < this.lifetime)
        {
            this.xd *= 0.98;
            this.yd *= 0.98;
            this.zd *= 0.98;
            this.alpha *= 0.95;
            this.move(this.xd, this.yd, this.zd);
        }
        else
        {
            this.remove();
        }
    }

    @Override
    public @NotNull IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull BasicParticleType type, @NotNull ClientWorld world, double x, double y, double z, double xd, double yd, double zd)
        {
            DustParticle particle = new DustParticle(world, x, y, z, xd, yd, zd);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
