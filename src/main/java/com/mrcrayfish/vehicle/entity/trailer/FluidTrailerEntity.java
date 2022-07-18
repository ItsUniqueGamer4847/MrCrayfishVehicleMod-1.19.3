package com.mrcrayfish.vehicle.entity.trailer;

import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import com.mrcrayfish.vehicle.network.message.MessageEntityFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Author: MrCrayfish
 */
public class FluidTrailerEntity extends TrailerEntity implements IEntityAdditionalSpawnData
{
    protected FluidTank tank = new FluidTank(FluidAttributes.BUCKET_VOLUME * 100)
    {
        @Override
        protected void onContentsChanged()
        {
            syncTank();
        }
    };

    public FluidTrailerEntity(EntityType<? extends FluidTrailerEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public @NotNull ActionResultType interact(@NotNull PlayerEntity player, @NotNull Hand hand)
    {
        if(!level.isClientSide && !player.isCrouching())
        {
            if(FluidUtil.interactWithFluidHandler(player, hand, tank))
            {
                return ActionResultType.SUCCESS;
            }
        }
        return super.interact(player, hand);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound)
    {
        super.readAdditionalSaveData(compound);
        if(compound.contains("Tank", Constants.NBT.TAG_COMPOUND))
        {
            this.tank.readFromNBT(compound.getCompound("Tank"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound)
    {
        super.addAdditionalSaveData(compound);
        CompoundNBT tankTag = new CompoundNBT();
        this.tank.writeToNBT(tankTag);
        compound.put("Tank", tankTag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap)
    {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return LazyOptional.of(() -> this.tank).cast();
        return super.getCapability(cap);
    }

    public FluidTank getTank()
    {
        return this.tank;
    }

    public void syncTank()
    {
        if(!this.level.isClientSide)
        {
            PacketHandler.getPlayChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new MessageEntityFluid(this.getId(), this.tank.getFluid()));
        }
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer)
    {
        super.writeSpawnData(buffer);
        buffer.writeNbt(this.tank.writeToNBT(new CompoundNBT()));
    }

    @Override
    public void readSpawnData(PacketBuffer buffer)
    {
        super.readSpawnData(buffer);
        this.tank.readFromNBT(buffer.readNbt());
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerInteractionBoxes()
    {
        EntityRayTracer.instance().registerInteractionBox(ModEntities.FLUID_TRAILER.get(), () -> {
            return createScaledBoundingBox(-7.0, -0.5, 12.0, 7.0, 3.5, 24.0, 0.0625);
        }, (entity, rightClick) -> {
            if(rightClick) {
                PacketHandler.getPlayChannel().sendToServer(new MessageAttachTrailer(entity.getId()));
                Minecraft.getInstance().player.swing(Hand.MAIN_HAND);
            }
        }, entity -> true);
    }
}
