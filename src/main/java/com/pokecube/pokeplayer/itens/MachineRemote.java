package com.pokecube.pokeplayer.itens;

import com.pokecube.pokeplayer.data.MachineInventoryCapability;
import com.pokecube.pokeplayer.client.container.MachineInventoryProvider;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MachineRemote extends Item
{
    String tooltip_id;

    // Info
    public MachineRemote(final String tooltipName, final CreativeModeTab tab, final int maxStackSize)
    {
        super(new Properties().tab(tab).stacksTo(maxStackSize));
        this.tooltip_id = tooltipName;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player entity, @NotNull InteractionHand hand) {
        InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        ItemStack itemStack = ar.getObject();
        if (entity instanceof ServerPlayer sPlayer) {
            FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
            extraData.writeBlockPos(entity.blockPosition());
            extraData.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);

            NetworkHooks.openGui(sPlayer, new MachineInventoryProvider(extraData), buf -> {
                buf.writeBlockPos(entity.blockPosition());
                buf.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
            });
        }
        return ar;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag tag){
        return new MachineInventoryCapability();
    }

    @Override
    public CompoundTag getShareTag(ItemStack stack){
        CompoundTag nbt = super.getShareTag(stack);
        if(nbt != null)
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability ->
                    nbt.put("Inventory", ((ItemStackHandler) capability).serializeNBT()));
        return nbt;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt){
        super.readShareTag(stack, nbt);
        if(nbt != null)
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability ->
                    ((ItemStackHandler) capability).deserializeNBT((CompoundTag) nbt.get("Inventory")));
    }
}
