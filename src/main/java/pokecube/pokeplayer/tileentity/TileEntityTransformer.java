package pokecube.pokeplayer.tileentity;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import pokecube.core.database.Database;
import pokecube.core.database.PokedexEntry;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.items.pokecubes.PokecubeManager;
import pokecube.legends.blocks.containers.GenericBarrel;
import pokecube.legends.tileentity.GenericBarrelTile;
import pokecube.pokeplayer.PokeInfo;
import pokecube.pokeplayer.Pokeplayer;
import pokecube.pokeplayer.Reference;
import pokecube.pokeplayer.block.PokeTransformContainer;
import pokecube.pokeplayer.block.TransformBlock;
import pokecube.pokeplayer.init.TileEntityInit;
import thut.api.entity.CopyCaps;
import thut.api.entity.ICopyMob;
import thut.core.common.handlers.PlayerDataHandler;
import thut.core.common.network.CapabilitySync;

public class TileEntityTransformer extends RandomizableContainerBlockEntity
{
    protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    int[]   nums     = {};
    int     stepTick = Pokeplayer.config.ticksBlockUse;

    public TileEntityTransformer(final BlockEntityType<?> tileEntityType, final BlockPos pos, final BlockState state)
    {
        super(tileEntityType, pos, state);
    }

    public TileEntityTransformer(final BlockPos pos, final BlockState state)
    {
        this(TileEntityInit.TRANSFORM_TILE.get(), pos, state);
    }

    
    @Override
    public void load(final CompoundTag nbt)
    {
        super.load(nbt);
        if (nbt.contains("stack"))
        {
            final CompoundTag tag = nbt.getCompound("stack");
            this.items.get(0).setTag(tag);
        }
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (nbt.contains("nums")) this.nums = nbt.getIntArray("nums");
        this.stepTick = nbt.getInt("stepTick");
        ContainerHelper.loadAllItems(nbt, this.items);
    }
 
    @Override
    public CompoundTag save(final CompoundTag compound)
    {
        super.save(compound);
        ContainerHelper.saveAllItems(compound, this.items);
        if (this.items.get(0).isEmpty())
        {
            final CompoundTag tag = new CompoundTag();
            this.items.get(0).save(tag);
            compound.put("stack", tag);
        }
        if (this.nums != null) compound.putIntArray("nums", this.nums);
        compound.putInt("stepTick", this.stepTick);
        return compound;
    }

    @Override
    public NonNullList<ItemStack> getItems()
    {
        return this.items;
    }

    @Override
    protected void setItems(final NonNullList<ItemStack> itemsIn)
    {
        this.items = itemsIn;
    }

    @Override
    public void clearRemoved() {
    	super.clearRemoved();
    	this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(),
                Block.UPDATE_ALL);
    }

    @Override
    protected Component getDefaultName()
    {
        return new TranslatableComponent("container." + Reference.ID + ".transform");
    }

	@Override
	protected AbstractContainerMenu createMenu(final int id, final Inventory inventory) {
		return new PokeTransformContainer(this, id, inventory);
	}

    @Override
   	public int getContainerSize() {
   		return this.items.size();
   	}

    @Override
    public boolean isEmpty()
    {
        for (final ItemStack stack : this.items)
            if (!stack.isEmpty()) return false;
        return true;
    }

    @Override
    public ItemStack getItem(final int index) {
    	return this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
    	return ContainerHelper.removeItem(this.items, index, count);
    }
    
    @Override
    public ItemStack removeItemNoUpdate(int index) {
    	return ContainerHelper.takeItem(this.items, index);
    }
    
    
    @Override
    public void setItem(final int index, final ItemStack stack)
    {
        final ItemStack itemStack = this.items.get(index);
        final boolean flag = !stack.isEmpty() && stack.areShareTagsEqual(itemStack) && ItemStack.matches(stack,
                itemStack);
        this.items.set(index, stack);
        if (stack.getCount() > this.getContainerSize()) stack.setCount(this.getContainerSize());

        if (!flag) this.setChanged();
    }
    
    @Override
    public boolean canOpen(final Player player)
    {
        if (this.level.getBlockEntity(this.getBlockPos()) != this) return false;
        else return player.distanceToSqr(this.getBlockPos().getX() + 0.5D, this.getBlockPos().getY() + 0.5D, this.getBlockPos().getZ()
                + 0.5D) <= 64.0D;
    }
    
    @Override
    public boolean canPlaceItem(final int index, final ItemStack stack)
    {
        return !stack.isDamaged();
    }

    @Override
    public void setRemoved() {
    	super.setRemoved();
    	this.items.clear();
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.save(new CompoundTag());
    }

    @Override
    public void handleUpdateTag(final CompoundTag tag)
    {
        this.load(tag);
    }

    public ItemStack getStack(final ItemStack stack)
    {
        return stack;
    }
    
//    public static int getOpenCount(final Level world, final BaseContainerBlockEntity lockableTileEntity, final int i,
//            final int j, final int k, final int l, int r)
//    {
//        if (!world.isClientSide && r != 0 && (i + j + k + l) % 200 == 0) r = TileEntityTransformer.getOpenCount(world,
//                lockableTileEntity, j, k, l);
//
//        return r;
//    }
//    
//    public static int getOpenCount(final Level world, final BaseContainerBlockEntity lockableTileEntity, final int j,
//            final int k, final int l)
//    {
//        int i = 0;
//        for (final Player player : world.getEntitiesOfClass(Player.class, new AABB(j - 5.0F, k - 5.0F, l - 5.0F, j + 1
//                + 5.0F, k + 1 + 5.0F, l + 1 + 5.0F)))
//            if (player.containerMenu instanceof PokeTransformContainer)
//            {
//                final Container iinventory = ((PokeTransformContainer) player.containerMenu).getContainer();
//                if (iinventory == lockableTileEntity || iinventory instanceof CompoundContainer
//                        && ((CompoundContainer) iinventory).contains(lockableTileEntity)) ++i;
//            }
//        return i;
//    }

	public void onWalkedOn(final Entity entityIn)
    {
		Level world = this.getLevel();
        if (world.isClientSide || this.stepTick-- > 0) return;
        this.stepTick = Pokeplayer.config.ticksBlockUse;
        final Player player = (Player) entityIn;
        final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
        final boolean isPokemob = info.getPokemob(this.level) != null;

        final boolean hasPokemob = !this.getItems().get(0).isEmpty();
        if (hasPokemob && !isPokemob)
        {        	
            final IPokemob pokemob = this.getPokemob();
            final UUID playerTrainer = pokemob.getOwnerId();
            final PokedexEntry pokedexEntry = Database.getEntry(pokemob);
            final ICopyMob copy = CopyCaps.get(player);
            
            if (playerTrainer != null && pokemob != null && pokemob.getHealth() != 0 && playerTrainer != player.getUUID())
	        {
            	// Sets info for player
            	PokeInfo.setPokemob(player, pokemob);
                // Morph Visual Player
                copy.setCopiedID(pokedexEntry.getEntityType().getRegistryName());
                CapabilitySync.sendUpdate(player);
                // Guard Item inside Player
                this.items.set(0, ItemStack.EMPTY);
	        }
            else
            {
            	Pokeplayer.LOGGER.info("Invalid Pokemob!", player.getDisplayName().getString());
                return;
            }
            Pokeplayer.LOGGER.info("Converting {} to {}", player.getDisplayName().getString(), pokemob
                    .getPokedexEntry().getName());
            return;
        }
        if (!hasPokemob && isPokemob)
        {
            final IPokemob poke = PokeInfo.getPokemob(player);
            final CompoundTag tag = poke.getEntity().serializeNBT();
            final ICopyMob copy = CopyCaps.get(player);
                
            Pokeplayer.LOGGER.info("Converting {} back to a human", player.getDisplayName().getString());
            tag.putBoolean("is_a_player", true);
                      
            info.detach();
            final ItemStack pokemob = PokecubeManager.pokemobToItem(poke);
            if (player.getAbilities().mayfly && !player.isCreative())
            {
                player.getAbilities().mayfly = false;
                player.onUpdateAbilities();
            }
            //Reset Info for Player
            PokeInfo.setPokemob(player, null);
            //Reset Morph
            copy.setCopiedID(null);
            //Back item for block inventory
            this.items.set(0, pokemob);
            CapabilitySync.sendUpdate(player);
            return;
        }
        Pokeplayer.LOGGER.info("Nothing happened to {}", player.getDisplayName().getString());
    }

	private IPokemob getPokemob()
    {
        final IPokemob pokemob = PokecubeManager.itemToPokemob(this.items.get(0), this.getLevel());
        return pokemob;
    }
}