package com.mrcrayfish.vehicle.recipe;

import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Author: MrCrayfish
 */
public class RecipeRefillSprayCan extends SpecialRecipe
{
    public RecipeRefillSprayCan(ResourceLocation id)
    {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, @NotNull World worldIn)
    {
        ItemStack sprayCan = ItemStack.EMPTY;
        ItemStack emptySprayCan = ItemStack.EMPTY;

        for(int i = 0; i < inventory.getContainerSize(); i++)
        {
            ItemStack stack = inventory.getItem(i);
            if(!stack.isEmpty())
            {
                if(stack.getItem() instanceof SprayCanItem)
                {
                    if(((SprayCanItem) stack.getItem()).hasColor(stack))
                    {
                        if(!sprayCan.isEmpty())
                        {
                            return false;
                        }
                        sprayCan = stack.copy();
                    }
                    else
                    {
                        if(!emptySprayCan.isEmpty())
                        {
                            return false;
                        }
                        emptySprayCan = stack.copy();
                    }
                }
            }
        }
        return !sprayCan.isEmpty() && !emptySprayCan.isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInventory inventory)
    {
        ItemStack sprayCan = ItemStack.EMPTY;
        ItemStack emptySprayCan = ItemStack.EMPTY;

        for(int i = 0; i < inventory.getContainerSize(); i++)
        {
            ItemStack stack = inventory.getItem(i);
            if(!stack.isEmpty())
            {
                if(stack.getItem() instanceof SprayCanItem)
                {
                    if(((SprayCanItem) stack.getItem()).hasColor(stack))
                    {
                        if(!sprayCan.isEmpty())
                        {
                            return ItemStack.EMPTY;
                        }
                        sprayCan = stack.copy();
                    }
                    else
                    {
                        if(!emptySprayCan.isEmpty())
                        {
                            return ItemStack.EMPTY;
                        }
                        emptySprayCan = stack.copy();
                    }
                }
            }
        }

        if(!sprayCan.isEmpty() && !emptySprayCan.isEmpty())
        {
            ItemStack copy = sprayCan.copy();
            ((SprayCanItem) copy.getItem()).refill(copy);
            return copy;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return width * height >= 2;
    }

    @Override
    public @NotNull IRecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.REFILL_SPRAY_CAN.get();
    }
}
