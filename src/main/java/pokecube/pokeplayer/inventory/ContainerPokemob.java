package pokecube.pokeplayer.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import pokecube.core.PokecubeItems;
import pokecube.core.interfaces.IPokemob;
import pokecube.pokeplayer.PokeInfo;
import thut.core.common.handlers.PlayerDataHandler;

public class ContainerPokemob extends Container
{
	private IInventory	pokemobInv;
	private PlayerEntity playerEntity;
	
	public ContainerPokemob(ContainerType<?> type, int id)
	{
		super(type, id);
		PlayerEntity player = playerEntity;
	    final IPokemob e = PokeInfo.getPokemob(player);
	    final IInventory pokeInv;
        PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
        pokeInv = info.pokeInventory;
	    IInventory playerInv = player.inventory;
		this.pokemobInv = pokeInv;
		byte b0 = 3;
		pokeInv.startOpen(null);
		int i = (b0 - 4) * 18;
		int slot = 0;
		this.addSlot(new Slot(pokeInv, slot++, 8, 18)
		{
			/** Check if the stack is a valid item for this slot. Always true
			 * beside for the armor slots. */
			
			@Override
			public boolean mayPlace(ItemStack stack)
			{
				return super.mayPlace(stack) && stack.getItem() == Items.SADDLE && !this.getItem().isStackable();
			}
		});
		this.addSlot(new Slot(pokeInv, slot++, 8, 36)
		{
            
            /** Returns the maximum stack size for a given slot (usually the
			 * same as getInventoryStackLimit(), but 1 in the case of armor
			 * slots) */			
			@Override
			public int getMaxStackSize() {
				return 1;
			}

            /** Check if the stack is a valid item for this slot. Always true
			 * beside for the armor slots. */
			@Override
			public boolean mayPlace(ItemStack stack)
			{
				return PokecubeItems.isValidHeldItem(stack);
			}
		    
            @Override
            public ItemStack onTake(PlayerEntity playerIn, ItemStack stack)
            {
                ItemStack old = getItem();
//                if(Dist.DEDICATED_SERVER != null)
//                {
                    e.getPokedexEntry().onHeldItemChange(stack, old, e);
//                }
                return super.onTake(playerIn, stack);
            }

			/**
             * Helper method to put a stack in the slot.
             */
            @Override
            public void set(ItemStack stack)
            {
                super.set(stack);
//                if(Dist.DEDICATED_SERVER != null)
//                {
                    e.setHeldItem(stack);
//                }
            }
		});
		int j;
		int k;

		for (j = 0; j < 1; ++j)
		{
			for (k = 0; k < 5; ++k)
			{
				this.addSlot(new Slot(pokeInv, slot++, 80 + k * 18, 18 + j * 18)
				{
					/** Check if the stack is a valid item for this slot. Always
					 * true beside for the armor slots. */
					@Override
					public boolean mayPlace(ItemStack stack)
					{
						return PokecubeItems.isValidHeldItem(stack);
					}
				});
			}
		}
		slot = 0;
        for (j = 0; j < 9; ++j)
        {
            this.addSlot(new Slot(playerInv, slot++, 8 + j * 18, 160 + i));
        }

		for (j = 0; j < 3; ++j)
		{
			for (k = 0; k < 9; ++k)
			{
				this.addSlot(new Slot(playerInv, slot++, 8 + k * 18, 102 + j * 18 + i));
			}
		}
	}

	@Override
	public boolean isSynched(PlayerEntity player) {
		return true;
	}

	/** Called when the container is closed. */
	@Override
	public void removed(PlayerEntity player) {
		super.removed(player);
		player.drop(true);
	}

	/** Called when a player shift-clicks on a slot. You must override this or
	 * you will crash when someone does that. */	
	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int slotId)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotId);

		if (slot != null && slot.getItem().isStackable())
		{
			ItemStack itemstack1 = slot.getItem().getStack();
			itemstack = itemstack1.copy();

			if (slotId < this.pokemobInv.getContainerSize())
			{
				if (!this.moveItemStackTo(itemstack1, this.pokemobInv.getContainerSize(), this.slots.size(),
						true)) { return ItemStack.EMPTY; }
			}
			else if (this.getSlot(1).mayPlace(itemstack1) && !this.getSlot(1).hasItem())
			{
			    this.getSlot(1).getMaxStackSize(slot.getItem().split(1));
			}
			else if (this.getSlot(0).mayPlace(itemstack1))
			{
				if (!this.moveItemStackTo(itemstack1, 0, 1, false)) { return ItemStack.EMPTY; }
			}
			else if (this.pokemobInv.getContainerSize() <= 2
					|| !this.moveItemStackTo(itemstack1, 2, this.pokemobInv.getContainerSize(), false)) { return ItemStack.EMPTY; }

            if (!itemstack1.isEmpty())
			{
				slot.getMaxStackSize(ItemStack.EMPTY);
			}
			else
			{
				slot.setChanged();
			}
		}
		return itemstack;
	}

	@Override
	public boolean stillValid(PlayerEntity playerIn) {
		return true;
	}
}
