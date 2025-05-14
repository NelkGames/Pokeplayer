package com.pokecube.pokeplayer.init;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import pokecube.core.init.CoreCreativeTabs;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class PokeplayerCreativeTabs {

    @SubscribeEvent
    public static void buildTabContentsMod(BuildCreativeModeTabContentsEvent tabData) {
        if (tabData.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            tabData.accept(ItemInit.POKEPLAYER_MACHINE.get());
        }
        if (tabData.getTabKey() == CoreCreativeTabs.BLOCKS_ITEMS_TAB) {
            tabData.accept(ItemInit.POKEPLAYER_MACHINE.get());
        }
    }
}
