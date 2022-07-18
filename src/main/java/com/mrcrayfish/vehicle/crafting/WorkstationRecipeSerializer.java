package com.mrcrayfish.vehicle.crafting;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class WorkstationRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<WorkstationRecipe>
{
    @Override
    public @NotNull WorkstationRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject parent)
    {
        ImmutableList.Builder<WorkstationIngredient> builder = ImmutableList.builder();
        JsonArray input = JSONUtils.getAsJsonArray(parent, "materials");
        for(int i = 0; i < input.size(); i++)
        {
            JsonObject object = input.get(i).getAsJsonObject();
            builder.add(WorkstationIngredient.fromJson(object));
        }
        if(!parent.has("vehicle"))
        {
            throw new com.google.gson.JsonSyntaxException("Missing vehicle entry");
        }
        ResourceLocation vehicle = new ResourceLocation(JSONUtils.getAsString(parent, "vehicle"));
        Optional<EntityType<?>> optional = EntityType.byString(JSONUtils.getAsString(parent, "vehicle"));
        if(optional.isEmpty())
        {
            throw new com.google.gson.JsonSyntaxException("Invalid vehicle entity: " + vehicle);
        }
        return new WorkstationRecipe(recipeId, optional.get(), builder.build());
    }

    @Nullable
    @Override
    public WorkstationRecipe fromNetwork(@NotNull ResourceLocation recipeId, PacketBuffer buffer)
    {
        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(buffer.readResourceLocation());
        ImmutableList.Builder<WorkstationIngredient> builder = ImmutableList.builder();
        int size = buffer.readVarInt();
        for(int i = 0; i < size; i++)
        {
            builder.add((WorkstationIngredient) Ingredient.fromNetwork(buffer));
        }
        return new WorkstationRecipe(recipeId, entityType, builder.build());
    }

    @Override
    public void toNetwork(PacketBuffer buffer, WorkstationRecipe recipe)
    {
        buffer.writeResourceLocation(recipe.getVehicle().getRegistryName());
        buffer.writeVarInt(recipe.getMaterials().size());
        for(WorkstationIngredient stack : recipe.getMaterials())
        {
            stack.toNetwork(buffer);
        }
    }
}
