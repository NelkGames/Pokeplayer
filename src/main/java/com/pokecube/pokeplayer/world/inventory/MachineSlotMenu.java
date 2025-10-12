package com.pokecube.pokeplayer.world.inventory;

import com.pokecube.pokeplayer.data.PokeInfo;
import com.pokecube.pokeplayer.init.GuiInit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import pokecube.core.items.pokecubes.PokecubeManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MachineSlotMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
    public final static HashMap<String, Object> guistate = new HashMap<>();
    public final Level world;
    public final Player entity;
    public int x, y, z;
    private ContainerLevelAccess access = ContainerLevelAccess.NULL;
    private IItemHandler internal;
    private final Map<Integer, Slot> customSlots = new HashMap<>();
    private boolean bound = false;
    private Supplier<Boolean> boundItemMatcher = null;
    private Entity boundEntity = null;
    private BlockEntity boundBlockEntity = null;

    public MachineSlotMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(GuiInit.MACHINE_MENU.get(), id);
        this.entity = inv.player;
        this.world = inv.player.level;
        this.internal = new ItemStackHandler(1);
        BlockPos pos = null;
        if (extraData != null) {
            pos = extraData.readBlockPos();
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            access = ContainerLevelAccess.create(world, pos);
        }
        if (pos != null) {
            if (extraData.readableBytes() == 1) {
                byte hand = extraData.readByte();
                ItemStack itemstack = hand == 0 ? this.entity.getMainHandItem() : this.entity.getOffhandItem();
                this.boundItemMatcher = () -> itemstack == (hand == 0 ? this.entity.getMainHandItem() : this.entity.getOffhandItem());
                IItemHandler cap = itemstack.getCapability(Capabilities.ItemHandler.ITEM);
                if (cap != null) {
                    this.internal = cap;
                    this.bound = true;
                }
            } else if (extraData.readableBytes() > 1) { // bound to entity
                extraData.readByte(); // drop padding
                boundEntity = world.getEntity(extraData.readVarInt());
                if (boundEntity != null) {
                    IItemHandler cap = boundEntity.getCapability(Capabilities.ItemHandler.ENTITY);
                    if (cap != null) {
                        this.internal = cap;
                        this.bound = true;
                    }
                }
            } else { // might be bound to block
                boundBlockEntity = this.world.getBlockEntity(pos);
                if (boundBlockEntity instanceof BaseContainerBlockEntity baseContainerBlockEntity) {
                    this.internal = new InvWrapper(baseContainerBlockEntity);
                    this.bound = true;
                }
            }
        }
        this.customSlots.put(0, this.addSlot(new SlotItemHandler(internal, 0, 80, 34) {
            private final int slot = 0;
            private int x = MachineSlotMenu.this.x;
            private int y = MachineSlotMenu.this.y;

            @Override
            public void setChanged() {
                ItemStack stack = this.getItem();
                if (!stack.isEmpty() && PokecubeManager.isFilled(stack)) {
                    PokeInfo.POKE_INFO.TransformMob(stack, entity, world);
                } else {
                    PokeInfo.POKE_INFO.RevertMob(entity);
                }
            }
        }));
        for (int si = 0; si < 3; ++si)
            for (int sj = 0; sj < 9; ++sj)
                this.addSlot(new Slot(inv, sj + (si + 1) * 9, 0 + 8 + sj * 18, 0 + 84 + si * 18));
        for (int si = 0; si < 9; ++si)
            this.addSlot(new Slot(inv, si, 0 + 8 + si * 18, 0 + 142));
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (this.bound) {
            if (this.boundItemMatcher != null)
                return this.boundItemMatcher.get();
            else if (this.boundBlockEntity != null)
                return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
            else if (this.boundEntity != null)
                return this.boundEntity.isAlive();
        }
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 1) {
                if (!this.moveItemStackTo(itemstack1, 1, this.slots.size(), true))
                    return ItemStack.EMPTY;
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                if (index < 1 + 27) {
                    if (!this.moveItemStackTo(itemstack1, 1 + 27, this.slots.size(), true))
                        return ItemStack.EMPTY;
                } else {
                    if (!this.moveItemStackTo(itemstack1, 1, 1 + 27, false))
                        return ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0)
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount())
                return ItemStack.EMPTY;
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    @Override
    protected boolean moveItemStackTo(@NotNull ItemStack stack, int index, int count, boolean move) {
        boolean flag = false;
        int i = index;
        if (move) {
            i = count - 1;
        }
        if (stack.isStackable()) {
            while (!stack.isEmpty() && (move ? i >= index : i < count)) {
                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (slot.mayPlace(itemstack) && !itemstack.isEmpty() && ItemStack.isSameItemSameComponents(stack, itemstack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int k = slot.getMaxStackSize(itemstack);
                    if (j <= k) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.set(itemstack);
                        flag = true;
                    } else if (itemstack.getCount() < k) {
                        stack.shrink(k - itemstack.getCount());
                        itemstack.setCount(k);
                        slot.set(itemstack);
                        flag = true;
                    }
                }
                if (move) {
                    i--;
                } else {
                    i++;
                }
            }
        }
        if (!stack.isEmpty()) {
            if (move) {
                i = count - 1;
            } else {
                i = index;
            }
            while (move ? i >= index : i < count) {
                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(stack)) {
                    int l = slot1.getMaxStackSize(stack);
                    slot1.setByPlayer(stack.split(Math.min(stack.getCount(), l)));
                    slot1.setChanged();
                    flag = true;
                    break;
                }
                if (move) {
                    i--;
                } else {
                    i++;
                }
            }
        }
        return flag;
    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        if (!bound && playerIn instanceof ServerPlayer serverPlayer) {
            if (!serverPlayer.isAlive() || serverPlayer.hasDisconnected()) {
                for (int j = 0; j < internal.getSlots(); ++j) {
                    if (j == 0)
                        continue;
                    playerIn.drop(internal.getStackInSlot(j), false);
                    if (internal instanceof IItemHandlerModifiable ihm)
                        ihm.setStackInSlot(j, ItemStack.EMPTY);
                }
            } else {
                for (int i = 0; i < internal.getSlots(); ++i) {
                    if (i == 0)
                        continue;
                    playerIn.getInventory().placeItemBackInInventory(internal.getStackInSlot(i));
                    if (internal instanceof IItemHandlerModifiable ihm)
                        ihm.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }
    }

    public Map<Integer, Slot> get() {
        return customSlots;
    }
}
