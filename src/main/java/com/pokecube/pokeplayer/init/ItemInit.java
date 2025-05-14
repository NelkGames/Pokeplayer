package com.pokecube.pokeplayer.init;

import com.pokecube.pokeplayer.Pokeplayer;
import com.pokecube.pokeplayer.item.MachineRemote;
import com.pokecube.pokeplayer.item.inventory.MachineInventoryCapability;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredItem;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ItemInit {
    public static final DeferredItem<Item> POKEPLAYER_MACHINE;

    static {
        POKEPLAYER_MACHINE = Pokeplayer.ITEMS.register("pokeplayer_machine",
                () -> new MachineRemote("pokeplayer_machine", 1));
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, context) ->
                new MachineInventoryCapability(stack), POKEPLAYER_MACHINE.get());
    }

    public static void init(){}
}
