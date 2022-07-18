package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.tileentity.JackTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("deprecation")
public class JackBlock extends RotatedObjectBlock
{
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 10, 15);

    public JackBlock()
    {
        super(AbstractBlock.Properties.of(Material.PISTON));
        this.registerDefaultState(this.getStateDefinition().any().setValue(DIRECTION, Direction.NORTH).setValue(ENABLED, false));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, IBlockReader worldIn, @NotNull BlockPos pos, @NotNull ISelectionContext context)
    {
        TileEntity tileEntity = worldIn.getBlockEntity(pos);
        if(tileEntity instanceof JackTileEntity)
        {
            JackTileEntity jack = (JackTileEntity) tileEntity;
            return VoxelShapes.create(SHAPE.bounds().expandTowards(0, 0.5 * jack.getProgress(), 0));
        }
        return SHAPE;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new JackTileEntity();
    }

    @Override
    @NotNull
    public BlockRenderType getRenderShape(@NotNull BlockState state)
    {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.@NotNull Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(ENABLED);
    }

    // Prevents the tile entity from being removed if the replacement block is the same
    @Override
    public void onRemove(BlockState state, @NotNull World world, @NotNull BlockPos pos, BlockState replaceState, boolean what)
    {
        if(!state.is(replaceState.getBlock()))
        {
            super.onRemove(state, world, pos, replaceState, what);
        }
    }
}
