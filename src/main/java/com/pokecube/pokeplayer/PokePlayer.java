package com.pokecube.pokeplayer;

import com.pokecube.pokeplayer.client.KeyBindingsHandler;
import com.pokecube.pokeplayer.client.gui.MachineScreen;
import com.pokecube.pokeplayer.init.PacketHandler;
import com.pokecube.pokeplayer.init.RegisterInit;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import pokecube.api.PokecubeAPI;
import thut.api.entity.CopyCaps;
import com.pokecube.pokeplayer.render.PokeplayerRender;

@Mod(value = Reference.ID)
public class PokePlayer {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.ID);
    public static final DeferredRegister<MenuType<?>> MENU = DeferredRegister.create(ForgeRegistries.CONTAINERS, Reference.ID);

    public PokePlayer() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.addListener(PokeplayerRender::onCopyTick);
        // Handles resetting flight permissions when un-setting mob
        MinecraftForge.EVENT_BUS.addListener(PokeplayerRender::onCopySet);
        // This syncs step height for the mob over
        MinecraftForge.EVENT_BUS.addListener(PokeplayerRender::onPlayerTick);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientSetup);

        CopyCaps.register(EntityType.PLAYER);

        MinecraftForge.EVENT_BUS.register(this);
        PokecubeAPI.POKEMOB_BUS.register(this);

        PokePlayer.ITEMS.register(modEventBus);
        PokePlayer.MENU.register(modEventBus);

        RegisterInit.init();
        PacketHandler.register();
    }

    private void doClientSetup(final FMLClientSetupEvent event) {
        MenuScreens.register(RegisterInit.MACHINE_SLOT.get(), MachineScreen::new);
        KeyBindingsHandler.init(event);
    }

    private void setup(final FMLCommonSetupEvent event) {}
}
