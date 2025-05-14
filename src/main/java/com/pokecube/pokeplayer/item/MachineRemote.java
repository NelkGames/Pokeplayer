package com.pokecube.pokeplayer.item;

import com.pokecube.pokeplayer.world.inventory.MachineSlotMenu;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class MachineRemote extends Item {
    private final String name;

    public MachineRemote(String name, int capacity) {
        super(new Item.Properties()
                .stacksTo(capacity)
                .fireResistant()
                .rarity(Rarity.COMMON));
        this.name = name;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
        InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("pokeplayer_machine");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
                    FriendlyByteBuf packetBuff = new FriendlyByteBuf(Unpooled.buffer());
                    packetBuff.writeBlockPos(player.blockPosition());
                    packetBuff.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
                    return new MachineSlotMenu(id, playerInventory, packetBuff);
                }
            }, buff -> {
                buff.writeBlockPos(entity.blockPosition());
                buff.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
            });
        }
        return ar;
    }
}
