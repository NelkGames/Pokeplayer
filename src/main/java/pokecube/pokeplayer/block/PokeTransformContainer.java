package pokecube.pokeplayer.block;

import java.util.Objects;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import pokecube.pokeplayer.init.BlockInit;
import pokecube.pokeplayer.init.ContainerInit;
import pokecube.pokeplayer.tileentity.TileEntityTransformer;

public class PokeTransformContainer extends AbstractContainerMenu {
	
	public final TileEntityTransformer container;
	public final ContainerLevelAccess acess;
	
	public PokeTransformContainer(TileEntityTransformer containerType, int windowId, Inventory playerInventory) {
		super(ContainerInit.TRANSFORM_CONTAINER.get(), windowId);
		this.container = containerType;
		this.acess = ContainerLevelAccess.create(containerType.getLevel(), containerType.getBlockPos());
		this.addSlot(new Slot(containerType, 0, 81, 36));
		
		//Main Inventory
		int startX = 8;
		int startY = 84;
		int slotSizePlus2 = 18;
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(playerInventory, 9 + (row * 9) + column, startX + (column * slotSizePlus2),
								startY + (row * slotSizePlus2)));
			}
		}
		
		//Hotbar
		for(int column = 0; column < 9; column++) {
			this.addSlot(new Slot(playerInventory, column, startX + (column * slotSizePlus2), 142));
		}
	}
	
	public PokeTransformContainer (final int windowID, final Inventory playerInv, final FriendlyByteBuf data) {
		this(getTileEntity(playerInv, data), windowID, playerInv);
	}
	
	private static TileEntityTransformer getTileEntity(final Inventory playerIn, final FriendlyByteBuf data) {
		Objects.requireNonNull(playerIn, "");
		Objects.requireNonNull(data, "");
		final BlockEntity tileAtPos = playerIn.player.level.getBlockEntity(data.readBlockPos());
		if(tileAtPos instanceof TileEntityTransformer) {
			return (TileEntityTransformer) tileAtPos;
		}
		throw new IllegalStateException("" + tileAtPos);
	}
	
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if(slot != null && slot.getItem().isStackable()) {
			ItemStack itemStack1 = slot.getItem().copy();
			itemStack = itemStack1.copy();
			if(index < 1) {
				if (!this.moveItemStackTo(itemStack1, 1, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			}else if (!this.moveItemStackTo(itemStack1, 0, 1, false)) {
				return ItemStack.EMPTY;
			}
			
			if(itemStack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			}else {
				slot.setChanged();
			}
		}
		
		return itemStack;
	}
	
	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(acess, playerIn, BlockInit.TRANSFORM.get());
	}

	public Container getContainer() {
		return this.container;
	}
}
