package pokecube.pokeplayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import pokecube.core.PokecubeCore;
import pokecube.pokeplayer.init.BlockInit;
import pokecube.pokeplayer.init.Config;
import pokecube.pokeplayer.init.ContainerInit;
import pokecube.pokeplayer.init.TileEntityInit;
import pokecube.pokeplayer.render.RenderPlayerPokemob;
import thut.api.entity.CopyCaps;
import thut.core.common.handlers.PlayerDataHandler;

@Mod(value = Reference.ID)
public class Pokeplayer
{
	public static final Logger LOGGER = LogManager.getLogger();
	
    public static final DeferredRegister<Block> BLOCKS     = DeferredRegister.create(ForgeRegistries.BLOCKS,
            Reference.ID);
    public static final DeferredRegister<Item>  ITEMS      = DeferredRegister.create(ForgeRegistries.ITEMS,
            Reference.ID);
    public static final DeferredRegister<BlockEntityType<?>>  TILES      = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES,
            Reference.ID);
    public static final DeferredRegister<MenuType<?>>  CONTAINER      = DeferredRegister.create(ForgeRegistries.CONTAINERS,
            Reference.ID);
    
    public static final Config config = new Config();
    
    public Pokeplayer()
    {
    	thut.core.common.config.Config.setupConfigs(Pokeplayer.config, PokecubeCore.MODID, Reference.ID);
    	PokecubeCore.POKEMOB_BUS.register(this);
    	
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(RenderPlayerPokemob::onCopyTick);
        // Handles resetting flight permissions when un-setting mob
        MinecraftForge.EVENT_BUS.addListener(RenderPlayerPokemob::onCopySet);
        // This syncs step height for the mob over
        MinecraftForge.EVENT_BUS.addListener(RenderPlayerPokemob::onPlayerTick);
        
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        
        CopyCaps.register(EntityType.PLAYER);
        
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        PlayerDataHandler.register(PokeInfo.class);
        
        Pokeplayer.BLOCKS.register(modEventBus);
        Pokeplayer.ITEMS.register(modEventBus);
        Pokeplayer.TILES.register(modEventBus);
        Pokeplayer.CONTAINER.register(modEventBus);

        BlockInit.init();
        TileEntityInit.init();
        ContainerInit.init();       
    }
    
    @SubscribeEvent
    public void serverStarting(final ServerStartingEvent event)
    {
        Pokeplayer.config.loaded = true;
        Pokeplayer.config.onUpdated();
    }
}
