package com.pokecube.pokeplayer.client.container;

import com.pokecube.pokeplayer.init.RegisterInit;
import com.pokecube.pokeplayer.data.PokePlayerDataHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import pokecube.api.entity.pokemob.IPokemob;
import pokecube.core.items.pokecubes.PokecubeManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MachineSlotMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
    public final static HashMap<String, String> guistate = new HashMap<>();
    public final Level world;
    public final Player entity;
    public int x, y, z;
    private IItemHandler internal;
    private final Map<Integer, Slot> customSlots = new HashMap<>();
    private boolean bound = false;

    public MachineSlotMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(RegisterInit.MACHINE_SLOT.get(), id);
        this.entity = inv.player;
        this.world = inv.player.level;
        this.internal = new ItemStackHandler(1);
        BlockPos pos = null;
        if (extraData != null) {
            pos = extraData.readBlockPos();
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
        }
        if (pos != null) {
            if (extraData.readableBytes() == 1) {
                byte hand = extraData.readByte();
                ItemStack itemStack;
                if (hand == 0)
                    itemStack = this.entity.getMainHandItem();
                else
                    itemStack = this.entity.getOffhandItem();
                itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(cap -> {
                    this.internal = cap;
                    this.bound = true;
                });
            } else if (extraData.readableBytes() > 1) {
                extraData.readByte();
                Entity entity = world.getEntity(extraData.readVarInt());
                if (entity != null)
                    entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(cap -> {
                        this.internal = cap;
                        this.bound = true;
                    });
            } else {
                BlockEntity ent = inv.player != null ? inv.player.level.getBlockEntity(pos) : null;
                if (ent != null) {
                    ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(cap -> {
                        this.internal = cap;
                        this.bound = true;
                    });
                }
            }
        }
        this.customSlots.put(0, this.addSlot(new SlotItemHandler(internal, 0, 80, 35) {
            @Override
            public void setChanged(){
                super.setChanged();
                ItemStack stack = this.getItem();
                if(!stack.isEmpty() && PokecubeManager.isFilled(stack)) {
                    System.out.println("Item detectado e não está vazio. Verificando se é um Pokemob...");
                    TransformMob(stack);
                } else {
                    RevertMob();
                }
            }
        }));
        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 9; ++col)
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; ++col)
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
    }

    private void TransformMob(ItemStack pokecube) {
       if(PokecubeManager.isFilled(pokecube)) {
           IPokemob pokemob = PokecubeManager.itemToPokemob(pokecube, this.world);
           if (pokemob != null) {
               System.out.println("Pokemob encontrado: " + pokemob.getPokedexEntry().getName());
               PokePlayerDataHandler.getInstance().transformToPokemob(this.entity, pokemob);

               String[] moves = pokemob.getMoves();
               if(moves != null){
                   for(int i = 0; i < moves.length; i++) {
                       if(moves[i] != null){
                           System.out.println("Movimento: " + moves[i]);
                           guistate.put("move_" + i, moves[i]);
                       }
                   }
               }
           }
       }
    }

    private void RevertMob(){
        PokePlayerDataHandler.getInstance().revertToPlayer(this.entity);

        guistate.clear();

        this.entity.setHealth(this.entity.getMaxHealth());
        this.entity.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(20.0D);
        this.entity.refreshDimensions();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemStack = stack.copy();
            if (index < 1) {
                if (!this.moveItemStackTo(stack, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemStack);
            } else if (!this.moveItemStackTo(stack, 0, 1, false)) {
                if (index < 1 + 27) {
                    if (!this.moveItemStackTo(stack, 1 + 27, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(stack, 1, 1 + 27, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                return ItemStack.EMPTY;
            }
            if (stack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, stack);
        }
        return itemStack;
    }

    @Override
    protected boolean moveItemStackTo(@NotNull ItemStack stack, int index, int count, boolean move) {
        boolean flag = false;
        int i = index;
        if (move) {
            i = count - 1;
        }
        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (move) {
                    if (i < index) {
                        break;
                    }
                } else if (i >= count) {
                    break;
                }
                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (slot.mayPlace(itemstack) && !itemstack.isEmpty() && ItemStack.isSameItemSameTags(stack, itemstack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), stack.getMaxStackSize());
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.set(itemstack);
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.set(itemstack);
                        flag = true;
                    }
                }
                if (move) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        if (!stack.isEmpty()) {
            if (move) {
                i = count - 1;
            } else {
                i = index;
            }
            while (true) {
                if (move) {
                    if (i < index) {
                        break;
                    }
                } else if (i >= count) {
                    break;
                }
                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(stack)) {
                    if (stack.getCount() > slot1.getMaxStackSize()) {
                        slot1.set(stack.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.set(stack.split(stack.getCount()));
                    }
                    slot1.setChanged();
                    flag = true;
                    break;
                }
                if (move) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        return flag;
    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        if (!bound && playerIn instanceof ServerPlayer sPlayer) {
            if (!sPlayer.isAlive() || sPlayer.hasDisconnected()) {
                for (int j = 0; j < internal.getSlots(); ++j) {
                    if (j == 0)
                        continue;
                    playerIn.drop(internal.extractItem(j, internal.getStackInSlot(j).getCount(), false), false);
                }
            } else {
                for (int i = 0; i < internal.getSlots(); ++i) {
                    if (i == 0)
                        continue;
                    playerIn.getInventory().placeItemBackInInventory(internal.extractItem(i, internal.getStackInSlot(i).getCount(), false));
                }
            }
        }
    }

    public Map<Integer, Slot> get(){
        return customSlots;
    }
}
