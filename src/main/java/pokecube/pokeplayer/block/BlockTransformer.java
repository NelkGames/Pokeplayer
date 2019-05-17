package pokecube.pokeplayer.block;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pokecube.pokeplayer.tileentity.TileEntityTransformer;

public class BlockTransformer extends BlockPressurePlate implements ITileEntityProvider
{
    public BlockTransformer()
    {
        super(Material.IRON, Sensitivity.MOBS);
        this.setHardness(100000);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityTransformer();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
            EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityTransformer)
        {
            ((TileEntityTransformer) tile).onInteract(playerIn);
        }
        return true;
    }

    /** Called When an Entity Collided with the Block */
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityTransformer && entityIn instanceof EntityPlayer)
        {
            ((TileEntityTransformer) tile).onStepped((EntityPlayer) entityIn);
        }
        super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
    }
}
