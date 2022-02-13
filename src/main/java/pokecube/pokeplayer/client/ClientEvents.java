package pokecube.pokeplayer.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import pokecube.core.PokecubeCore;
import pokecube.core.client.gui.GuiPokedex;
import pokecube.core.interfaces.IPokemob;
import pokecube.pokeplayer.PokeInfo;
import pokecube.pokeplayer.client.gui.GuiAsPokemob;
import thut.core.common.handlers.PlayerDataHandler;

public class ClientEvents 
{
	public IPokemob getPokemob(final Player player) {
		final IPokemob ret = PokeInfo.getPokemob(player);
		if (ret != null && player.level.isClientSide) {
			final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
			info.setPlayer(player);
		}
		return ret;
	}

	@SubscribeEvent
	public void onPlayerTick(final PlayerTickEvent event) {
		IPokemob pokemob = GuiAsPokemob.instance().getCurrentPokemob();
		Minecraft instance = Minecraft.getInstance();
		if (event.side == LogicalSide.SERVER || event.player != PokecubeCore.proxy.getPlayer(event.player.getUUID())
				|| (pokemob = this.getPokemob(event.player)) == null)
			return;
		if (instance.screen instanceof GuiPokedex) {
			((GuiPokedex)instance.screen).pokemob = pokemob;
			GuiPokedex.pokedexEntry = pokemob.getPokedexEntry();
		}
	}
}
