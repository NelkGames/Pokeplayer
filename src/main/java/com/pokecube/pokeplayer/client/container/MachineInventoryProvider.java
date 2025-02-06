package com.pokecube.pokeplayer.client.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public class MachineInventoryProvider implements MenuProvider
{
    private final FriendlyByteBuf extraData;

    public MachineInventoryProvider(FriendlyByteBuf extraData){
        this.extraData = extraData;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TextComponent("PokePlayer Transform");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player){
        return new MachineSlotMenu(id, inventory, extraData);
    }
}
