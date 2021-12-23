package pokecube.pokeplayer.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import pokecube.pokeplayer.tileentity.TileEntityTransformer;

public class TransformBlock extends PressurePlateBlock implements EntityBlock
{
	public TransformBlock(Sensitivity sensitivity, Properties propertiesIn) {
		super(sensitivity, propertiesIn);
    }

	@Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state)
    {
        return new TileEntityTransformer(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
    		InteractionHand hand, BlockHitResult hit) 
    {  	
    	if (!world.isClientSide)
    	{
            final BlockEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof TileEntityTransformer)
            {
            	NetworkHooks.openGui((ServerPlayer) player, (TileEntityTransformer) tileentity, pos);
                return InteractionResult.SUCCESS;
            }
        }
    	return InteractionResult.FAIL;
    }
    
    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) 
    {
    	BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof TileEntityTransformer && entityIn instanceof Player)
        {
            ((TileEntityTransformer) tile).onWalkedOn(entityIn);
        }
    }
    
    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState,
    		boolean isMoving) {
    	if (state.getBlock() != newState.getBlock()) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if (tileentity instanceof TileEntityTransformer) {
				Containers.dropContents(worldIn, pos, ((TileEntityTransformer) tileentity).getItems());
				worldIn.blockUpdated(pos, this);
			}
    	}
    }
}
