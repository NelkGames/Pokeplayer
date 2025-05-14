package com.pokecube.pokeplayer;

import com.mojang.logging.LogUtils;
import com.pokecube.pokeplayer.data.PokeplayerDataHandler;
import com.pokecube.pokeplayer.init.GuiInit;
import com.pokecube.pokeplayer.init.ItemInit;
import com.pokecube.pokeplayer.render.PokeplayerRender;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import pokecube.api.PokecubeAPI;
import thut.core.common.ThutCore;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Pokeplayer.MODID)
public class Pokeplayer {
    public static final String MODID = "pokeplayer";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, MODID);

    public Pokeplayer(IEventBus modEventBus) {
        //modEventBus.addListener(this::commonSetup);
        ThutCore.FORGE_BUS.addListener(PokeplayerRender::onCopyTick);

        ThutCore.FORGE_BUS.addListener(PokeplayerRender::onCopySet);

        ThutCore.FORGE_BUS.addListener(PokeplayerRender::onPlayerTick);
        // interaction with self with items
        ThutCore.FORGE_BUS.addListener(PokeplayerDataHandler::onRightClickItem);

        // Evolution
        PokecubeAPI.POKEMOB_BUS.addListener(PokeplayerDataHandler::onEvolve);

        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        MENUS.register(modEventBus);

        ItemInit.init();
        GuiInit.init();
    }
}
