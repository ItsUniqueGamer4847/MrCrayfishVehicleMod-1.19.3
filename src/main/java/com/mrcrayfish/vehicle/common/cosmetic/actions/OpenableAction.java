package com.mrcrayfish.vehicle.common.cosmetic.actions;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.raytrace.MatrixTransform;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.util.Axis;
import com.mrcrayfish.vehicle.util.EasingHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class OpenableAction extends Action
{
    private final Axis axis;
    private final float angle;
    private final int animationLength;
    @Nullable
    private final ResourceLocation openSound;
    @Nullable
    private final ResourceLocation closeSound;

    private boolean state = false; //Explicit to clearly indicate default state
    private int prevAnimationTick;
    private int animationTick;

    public OpenableAction(Axis axis, float angle, @Nullable ResourceLocation openSound, @Nullable ResourceLocation closeSound, int animationLength)
    {
        this.axis = axis;
        this.angle = angle;
        this.openSound = openSound;
        this.closeSound = closeSound;
        this.animationLength = animationLength;
    }

    @Override
    public void onInteract(VehicleEntity vehicle, PlayerEntity player)
    {
        this.state = !this.state;
        this.setDirty();
    }

    @Override
    public void load(CompoundNBT tag, boolean sync)
    {
        this.state = tag.getBoolean("Open");
        if(!sync && this.state)
        {
            this.animationTick = this.prevAnimationTick = this.animationLength;
        }
    }

    @Override
    public CompoundNBT save(boolean sync)
    {
        CompoundNBT tag = new CompoundNBT();
        tag.putBoolean("Open", this.state);
        return tag;
    }

    @Override
    public void serialize(JsonObject object)
    {
        JsonObject rotation = new JsonObject();
        rotation.addProperty("axis", this.axis.getKey());
        rotation.addProperty("angle", this.angle);
        rotation.addProperty("animationLength", this.animationLength);
        object.add("rotation", rotation);
        JsonObject sound = new JsonObject();
        if(this.openSound != null) sound.addProperty("open", this.openSound.toString());
        if(this.closeSound != null) sound.addProperty("close", this.closeSound.toString());
        if(sound.size() > 0) object.add("sound", sound);
    }

    @Override
    public void tick(VehicleEntity vehicle)
    {
        if(vehicle.level.isClientSide())
        {
            this.prevAnimationTick = this.animationTick;
            if(this.state)
            {
                if(this.animationTick == 0)
                {
                    this.playSound(true, vehicle);
                }
                if(this.animationTick < this.animationLength)
                {
                    this.animationTick++;
                }
            }
            else if(this.animationTick > 0)
            {
                this.animationTick--;
                if(this.animationTick == 0)
                {
                    this.playSound(false, vehicle);
                }
            }
        }
    }

    public boolean isOpen()
    {
        return this.state;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void beforeRender(MatrixStack matrixStack, VehicleEntity vehicle, float partialTicks)
    {
        if(this.animationTick != 0 || this.prevAnimationTick != 0)
        {
            float progress = MathHelper.lerp(partialTicks, this.prevAnimationTick, this.animationTick) / (float) this.animationLength;
            progress = (float) EasingHelper.easeOutBack(progress);
            matrixStack.mulPose(this.axis.getAxis().rotationDegrees(this.angle * progress));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void gatherTransforms(List<MatrixTransform> transforms)
    {
        if(this.prevAnimationTick != 0)
        {
            float progress = (float) this.prevAnimationTick / (float) this.animationLength;
            progress = (float) EasingHelper.easeOutBack(progress);
            transforms.add(MatrixTransform.rotate(this.axis.getAxis().rotationDegrees(this.angle * progress)));
        }
    }

    private void playSound(boolean state, VehicleEntity vehicle)
    {
        ResourceLocation sound = state ? this.openSound : this.closeSound;
        if(sound != null)
        {
            SoundEvent event = ForgeRegistries.SOUND_EVENTS.getValue(sound);
            if(event != null)
            {
                Vector3d position = vehicle.position();
                float pitch = 0.8F + 0.2F * vehicle.level.random.nextFloat();
                vehicle.level.playSound(null, position.x, position.y, position.z, event, SoundCategory.NEUTRAL, 1.0F, pitch);
            }
        }
    }
}
