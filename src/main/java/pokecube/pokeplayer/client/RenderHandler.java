package pokecube.pokeplayer.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import pokecube.pokeplayer.PokeInfo;
import thut.core.common.handlers.PlayerDataHandler;

@EventBusSubscriber(value = Dist.CLIENT)
public class RenderHandler 
{
  @SubscribeEvent
  public static void renderHand(final RenderHandEvent event)
  {
	 final Minecraft instance = Minecraft.getInstance();
     final Player player = instance.player;
     final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
     if (info.getPokemob(player.level) == null) 
    	event.getItemStack();
     	event.getHand();
  }
}
