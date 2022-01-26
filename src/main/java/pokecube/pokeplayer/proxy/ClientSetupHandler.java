package pokecube.pokeplayer.proxy;

import net.minecraft.client.gui.screens.MenuScreens;
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
import pokecube.pokeplayer.client.gui.GUIAsPokeplayer;
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
	@SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event)
    {
		MenuScreens.register(ContainerInit.TRANSFORM_CONTAINER.get(), TransformBlockScreen::new);
		
		PokecubeCore.provider = new EntityProviderPokeplayer((EntityProvider) PokecubeCore.provider);
		
		//
		GuiDisplayPokecubeInfo.instance = new GUIAsPokeplayer();
		PokecubeCore.packets.registerMessage(PacketTransform.class, PacketTransform::new);
		PacketCommand.init();
		IHasCommands.COMMANDHANDLERS.put(Command.ATTACKENTITY, AttackEntity.class);
		IHasCommands.COMMANDHANDLERS.put(Command.ATTACKLOCATION, AttackLocation.class);
		IHasCommands.COMMANDHANDLERS.put(Command.STANCE, Stance.class);
	}
}
