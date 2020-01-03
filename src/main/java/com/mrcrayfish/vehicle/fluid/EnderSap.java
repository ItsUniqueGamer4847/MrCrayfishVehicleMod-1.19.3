package com.mrcrayfish.vehicle.fluid;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

/**
 * Author: MrCrayfish
 */
public abstract class EnderSap extends ForgeFlowingFluid
{
    public EnderSap()
    {
        super(new Properties(() -> ModFluids.ENDER_SAP, () -> ModFluids.FLOWING_ENDER_SAP, FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "block/ender_sap_still"), new ResourceLocation(Reference.MOD_ID, "block/ender_sap_flowing")).viscosity(3000)));
    }

    @Override
    public Item getFilledBucket()
    {
        return ModItems.ENDER_SAP_BUCKET;
    }

    public static class Source extends EnderSap
    {
        public Source()
        {
            this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "ender_sap"));
        }

        @Override
        public boolean isSource(IFluidState state)
        {
            return true;
        }

        @Override
        public int getLevel(IFluidState p_207192_1_)
        {
            return 8;
        }
    }

    public static class Flowing extends EnderSap
    {
        public Flowing()
        {
            this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "flowing_ender_sap"));
        }

        @Override
        protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder)
        {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        @Override
        public int getLevel(IFluidState state)
        {
            return state.get(LEVEL_1_8);
        }

        @Override
        public boolean isSource(IFluidState state)
        {
            return false;
        }
    }
}
