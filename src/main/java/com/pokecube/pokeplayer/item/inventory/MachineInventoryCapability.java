package com.pokecube.pokeplayer.item.inventory;

import com.pokecube.pokeplayer.client.gui.MachineScreen;
import com.pokecube.pokeplayer.init.ItemInit;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.items.ComponentItemHandler;

import javax.annotation.Nullable;

@EventBusSubscriber(Dist.CLIENT)
public class MachineInventoryCapability extends ComponentItemHandler {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onItemDropped(ItemTossEvent event) {
        if (event.getEntity().getItem().getItem() == ItemInit.POKEPLAYER_MACHINE.get()) {
            if (Minecraft.getInstance().screen instanceof MachineScreen) {
                Minecraft.getInstance().player.closeContainer();
            }
        }
    }

    public MachineInventoryCapability(MutableDataComponentHolder parent) {
        super(parent, DataComponents.CONTAINER, 1);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @Nullable ItemStack stack) {
        return stack.getItem() != ItemInit.POKEPLAYER_MACHINE.get();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return super.getStackInSlot(slot).copy();
    }
}
