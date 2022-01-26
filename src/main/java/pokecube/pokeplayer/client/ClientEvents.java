package pokecube.pokeplayer.client;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ScreenEvent.MouseClickedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import pokecube.core.PokecubeCore;
import pokecube.core.client.gui.GuiDisplayPokecubeInfo;
import pokecube.core.client.gui.GuiPokedex;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.pokemob.IHasCommands.Command;
import pokecube.core.network.pokemobs.PacketCommand;
import pokecube.pokeplayer.PokeInfo;
import pokecube.pokeplayer.network.handlers.Stance;
import thut.core.common.handlers.PlayerDataHandler;

public class ClientEvents 
{
//	public IPokemob getPokemob(final Player player) {
//		final IPokemob ret = PokeInfo.getPokemob(player);
//		if (ret != null && player.level.isClientSide) {
//			final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
//			info.setPlayer(player);
//		}
//		return ret;
//	}
//
//	@SubscribeEvent
//	public void onPlayerTick(final PlayerTickEvent event) {
//		IPokemob pokemob;
//		Minecraft instance = Minecraft.getInstance();
//		if (event.side == LogicalSide.SERVER || event.player != PokecubeCore.proxy.getPlayer(event.player.getUUID())
//				|| (pokemob = this.getPokemob(event.player)) == null)
//			return;
//		if (instance.screen instanceof GuiPokedex) {
//			((GuiPokedex)instance.screen).pokemob = pokemob;
//			GuiPokedex.pokedexEntry = pokemob.getPokedexEntry();
//		}
//	}
//
//	@SubscribeEvent
//	public void mouseClickEvent(final MouseClickedEvent event) {
//		IPokemob pokemob = null;
//		Player player = null;
//		final int button = event.getButton();
//
//		final boolean alt = Screen.hasAltDown();
//		final boolean ctrl = Screen.hasControlDown();
//
//		if (alt && button >= 0
//				&& (pokemob = this.getPokemob(player = PokecubeCore.proxy.getPlayer((UUID) null))) != null) {
//			if (button == 0 && ctrl) {
//				GuiDisplayPokecubeInfo.instance().pokemobAttack();
//				event.setCanceled(true);
//			}
//			if (button == 1 && ctrl) {
//				// Our custom StanceHandler will do interaction code on -2
//				PacketCommand.sendCommand(pokemob, Command.STANCE, new Stance(true, (byte) -2));
//
//				final EntityInteractSpecific evt = new EntityInteractSpecific(player, InteractionHand.MAIN_HAND,
//						pokemob.getEntity(), new Vec3(0, 0, 0));
//				MinecraftForge.EVENT_BUS.post(evt);
//				// Apply interaction, also do not allow saddle.
//				final ItemStack saddle = pokemob.getInventory().getItem(0);
//				if (!saddle.isEmpty())
//					pokemob.getInventory().canPlaceItem(0, ItemStack.EMPTY);
//				if (!saddle.isEmpty())
//					pokemob.getInventory().setItem(0, saddle);
//				event.setCanceled(true);
//			}
//		}
//	}
}
