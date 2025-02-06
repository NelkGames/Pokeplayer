package com.pokecube.pokeplayer.data;

import com.pokecube.pokeplayer.client.gui.MachineScreen;
import com.pokecube.pokeplayer.init.RegisterInit;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class MachineInventoryCapability implements ICapabilitySerializable<CompoundTag> {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onItemDropped(ItemTossEvent event) {
        if (event.getEntityItem().getItem().getItem() == RegisterInit.POKEPLAYER_MACHINE.get()) {
            if (Minecraft.getInstance().screen instanceof MachineScreen) {
                Minecraft.getInstance().player.closeContainer();
            }
        }
    }

    private final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(this::createItemHandler);

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.inventory.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return getItemHandler().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        getItemHandler().deserializeNBT(nbt);
    }

    private ItemStackHandler createItemHandler() {
        return new ItemStackHandler(1) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() != RegisterInit.POKEPLAYER_MACHINE.get();
            }

            @Override
            public void setSize(int size) {
            }
        };
    }

    private ItemStackHandler getItemHandler() {
        return inventory.orElseThrow(RuntimeException::new);
    }
}
