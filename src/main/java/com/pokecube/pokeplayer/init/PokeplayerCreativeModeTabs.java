package com.pokecube.pokeplayer.init;

import com.pokecube.pokeplayer.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import pokecube.core.PokecubeItems;
import pokecube.core.init.CoreCreativeTabs;

@Mod.EventBusSubscriber(modid = Reference.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PokeplayerCreativeModeTabs extends CoreCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Reference.ID);

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        addAfter(event, PokecubeItems.PC_TOP.get(), RegisterInit.POKEPLAYER_MACHINE.get());
    }
}
