package pokecube.pokeplayer.proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import pokecube.core.PokecubeCore;
import pokecube.core.client.gui.GuiDisplayPokecubeInfo;
import pokecube.core.interfaces.pokemob.IHasCommands;
import pokecube.core.interfaces.pokemob.IHasCommands.Command;
import pokecube.core.network.EntityProvider;
import pokecube.core.network.pokemobs.PacketCommand;
import pokecube.pokeplayer.Reference;
import pokecube.pokeplayer.client.gui.GuiAsPokemob;
import pokecube.pokeplayer.client.gui.TransformBlockScreen;
import pokecube.pokeplayer.init.ContainerInit;
import pokecube.pokeplayer.network.EntityProviderPokeplayer;
import pokecube.pokeplayer.network.PacketTransform;
import pokecube.pokeplayer.network.handlers.AttackEntity;
import pokecube.pokeplayer.network.handlers.AttackLocation;
import pokecube.pokeplayer.network.handlers.Stance;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Reference.ID, value = Dist.CLIENT)
public class ClientSetupHandler
{
	public static final Logger LOGGER = LogManager.getLogger();
	
	@SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event)
    {	
		PokecubeCore.packets.registerMessage(PacketTransform.class, PacketTransform::new);
		
		ScreenManager.registerFactory(ContainerInit.TRANSFORM_CONTAINER.get(), TransformBlockScreen::new);
		GuiDisplayPokecubeInfo.instance = new GuiAsPokemob();
		
		PokecubeCore.provider = new EntityProviderPokeplayer((EntityProvider) PokecubeCore.provider);
	
		PacketCommand.init();
		
		IHasCommands.COMMANDHANDLERS.put(Command.ATTACKENTITY, AttackEntity.class);
		IHasCommands.COMMANDHANDLERS.put(Command.ATTACKLOCATION, AttackLocation.class);
		IHasCommands.COMMANDHANDLERS.put(Command.STANCE, Stance.class);
	}
}
