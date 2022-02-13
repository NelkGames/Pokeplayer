package pokecube.pokeplayer;

import java.util.HashSet;
import java.util.UUID;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import pokecube.core.events.pokemob.EvolveEvent;
import pokecube.core.events.pokemob.RecallEvent;
import pokecube.core.events.pokemob.combat.AttackEvent;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.interfaces.pokemob.IHasCommands.Command;
import pokecube.core.interfaces.pokemob.commandhandlers.AttackEntityHandler;
import pokecube.core.items.pokecubes.PokecubeManager;
import pokecube.core.network.pokemobs.PacketCommand;
import pokecube.pokeplayer.network.DataSyncWrapper;
import pokecube.pokeplayer.network.PacketTransform;
import thut.core.common.handlers.PlayerDataHandler;

public class EventHandler {

	public static final ResourceLocation DATACAP = new ResourceLocation(Reference.ID, "data");

	public EventHandler() {}

	@SubscribeEvent
	public void pokemobAttack(final AttackEvent evt) {
		if (evt.moveInfo.attacked instanceof Player) {
			final Player player = (Player) evt.moveInfo.attacked;
			final IPokemob pokemob = PokeInfo.getPokemob(player);
			if (pokemob != null)
				evt.moveInfo.attacked = pokemob.getEntity();
		}
	}

	@SubscribeEvent
	public void attack(final AttackEntityEvent event) {
		final Player player = event.getPlayer();
		final IPokemob pokemob = PokeInfo.getPokemob(player);
		if (pokemob == null)
			return;
		if (player.level.isClientSide()) PacketCommand.sendCommand(pokemob, Command.ATTACKENTITY,
					new AttackEntityHandler(event.getTarget().getId()).setFromOwner(true));
		event.setCanceled(true);
	}

	/**
	 * Sync attacks to the players over to the pokemobs, and also notifiy the
	 * pokeinfo that the pokemob was attacked.
	 *
	 * @param event
	 */
	@SubscribeEvent
	public void attack(final LivingAttackEvent event) {
		if (event.getEntity().level.isClientSide())
			return;
		Player player = null;
		if (event.getEntity() instanceof Player) {
			player = (Player) event.getEntity();
			final IPokemob pokemob = PokeInfo.getPokemob(player);
			if (pokemob != null)
				pokemob.getEntity().hurt(event.getSource(), event.getAmount());
		} else if (event.getEntity().getPersistentData().getBoolean("is_a_player")) {
			final IPokemob evo = CapabilityPokemob.getPokemobFor(event.getEntity());
			if (evo != null) {
				final UUID uuid = UUID.fromString(event.getEntity().getPersistentData().getString("playerID"));
				player = event.getEntity().level.getPlayerByUUID(uuid);
			}
		}
		if (player != null) {
			final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
			info.lastDamage = event.getSource();
		}
	}

	public static void recall(final RecallEvent.Pre evt) {
		final Entity entity = evt.recalled.getEntity();
		if (entity.getPersistentData().getBoolean("is_a_player")) {
			evt.setCanceled(true);
			evt.setResult(Result.DENY);
		}
	}

	public static void evolve(final EvolveEvent.Post evt) {
		final Entity entity = evt.mob.getEntity();
		if (entity.getPersistentData().getBoolean("is_a_player")) {
			final UUID uuid = UUID.fromString(entity.getStringUUID().concat("playerID"));
			final Player player = (Player) entity.level.getPlayerByUUID(uuid);
			final IPokemob evo = evt.mob;
			PokeInfo.setPokemob(player, evo);
			evt.setCanceled(true);
			evt.setResult(Result.DENY);
			if (!player.level.isClientSide()) {
				final ServerPlayer playerMP = (ServerPlayer) player;
				PacketTransform.sendPacket(player, playerMP);
				if (!player.level.isClientSide()) {
					EventHandler.sendUpdate(player);
					((ServerPlayer) player).containerMenu.getItems();
					// Fixes the inventories appearing to vanish
					player.getPersistentData().putLong("_pokeplayer_evolved_", player.level.getDayTime() + 50);
				}
			}
			return;
		}
	}

	@SubscribeEvent
	public void doRespawn(final PlayerEvent.PlayerRespawnEvent event) {
		if (event.getPlayer() != null && !event.getPlayer().level.isClientSide()) {
			IPokemob pokemob = PokeInfo.getPokemob(event.getPlayer());
			if (pokemob != null) {
				final ServerPlayer player = (ServerPlayer) event.getPlayer();
				final ItemStack stack = PokecubeManager.pokemobToItem(pokemob);
				PokecubeManager.heal(stack, event.getEntityLiving().level);
				pokemob = PokecubeManager.itemToPokemob(stack, event.getPlayer().level);
				pokemob.getEntity().isAlive();
				pokemob.getEntity().deathTime = -1;
				PokeInfo.setPokemob(event.getPlayer(), pokemob);
				if (!player.level.isClientSide) {
					EventHandler.sendUpdate(player);
					player.containerMenu.getCarried().getContainerItem();
				}
			}
		}
	}

	static HashSet<UUID> syncSchedule = new HashSet<>();

	@SubscribeEvent
	public void PlayerLoggedInEvent(final PlayerEvent.PlayerLoggedInEvent event) {
		if (Dist.DEDICATED_SERVER != null)
			EventHandler.syncSchedule.add(event.getPlayer().getUUID());
	}

	@SubscribeEvent
	public void PlayerLoggedOutEvent(final PlayerEvent.PlayerLoggedOutEvent event) {
		EventHandler.syncSchedule.remove(event.getPlayer().getUUID());
	}
	
	@SubscribeEvent
    public void startTracking(final PlayerEvent.StartTracking event)
    {
        if (event.getTarget() instanceof Player && event.getPlayer().isAddedToWorld())
        {
            PacketTransform.sendPacket((Player) event.getTarget(), (ServerPlayer) event.getPlayer());
        }
    }

	@SubscribeEvent
	public void onEntityCapabilityAttach(final AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player)
			event.addCapability(EventHandler.DATACAP, new DataSyncWrapper());
	}
	
	public static void postPlayerTick(final PlayerTickEvent event)
	{
		final Player player = event.player;
      	if (player == null) return;
      	final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
      	if (event.phase == Phase.END) info.postPlayerTick(player);
      	else PokeInfo.updateInfo(player, player.level);
	}

	@SubscribeEvent
	public void entityJoinWorld(final EntityJoinWorldEvent evt) {
		if (evt.getWorld().isClientSide())
			return;
		if (evt.getEntity().getPersistentData().getBoolean("is_a_player")) {
			final IPokemob evo = CapabilityPokemob.getPokemobFor(evt.getEntity());
			if (evo != null) {
				final UUID uuid = UUID.fromString(evt.getEntity().getPersistentData().getString("playerID"));
				final Player player = evt.getWorld().getPlayerByUUID(uuid);
				PokeInfo.setPokemob(player, evo);
				evt.setCanceled(true);
				return;
			}
		} else if (evt.getEntity() instanceof ServerPlayer)
			EventHandler.sendUpdate((Player) evt.getEntity());
	}

	public static void sendUpdate(final Player player) {
		PacketTransform.sendPacket(player, (ServerPlayer) player);
	}
}
