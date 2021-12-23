package pokecube.pokeplayer.proxy;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import pokecube.core.PokecubeCore;
import pokecube.core.network.EntityProvider;
import pokecube.pokeplayer.Reference;
import pokecube.pokeplayer.client.gui.TransformBlockScreen;
import pokecube.pokeplayer.init.ContainerInit;
import pokecube.pokeplayer.network.EntityProviderPokeplayer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Reference.ID, value = Dist.CLIENT)
public class ClientSetupHandler
{	
	@SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event)
    {
		MenuScreens.register(ContainerInit.TRANSFORM_CONTAINER.get(), TransformBlockScreen::new);
		
		PokecubeCore.provider = new EntityProviderPokeplayer((EntityProvider) PokecubeCore.provider);
	}
}
