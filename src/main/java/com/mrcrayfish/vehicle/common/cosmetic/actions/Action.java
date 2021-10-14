package com.mrcrayfish.vehicle.common.cosmetic.actions;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
public abstract class Action
{
    /**
     * Called when a player interacts (right clicks) the cosmetic. This is called on both logical
     * client and server.
     *
     * @param player the player interacting with the cosmetic
     * @param hand   the hand the player used to interact
     */
    public void onInteract(VehicleEntity vehicle, PlayerEntity player) {}

    /**
     * Called every time the vehicle ticks. This is called on both logical client and server.
     *
     * @param vehicle the vehicle this cosmetic action is bound to
     */
    public void tick(VehicleEntity vehicle) {}

    public void save(CompoundNBT tag) {}

    public void load(CompoundNBT tag) {}

    public abstract void serialize(JsonObject object);

    @OnlyIn(Dist.CLIENT)
    public void beforeRender(MatrixStack stack, VehicleEntity vehicle, float partialTicks) {}
}