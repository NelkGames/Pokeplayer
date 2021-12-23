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
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.items.pokecubes.PokecubeManager;
import pokecube.pokeplayer.network.DataSyncWrapper;
import thut.core.common.network.CapabilitySync;

public class EventHandler {
	
	public static final ResourceLocation DATACAP = new ResourceLocation(Reference.ID, "data");
	
    public EventHandler(){}
    
    @SubscribeEvent
	  public void doRespawn(final PlayerEvent.PlayerRespawnEvent event)
	  {
	      if (event.getPlayer() != null && !event.getPlayer().level.isClientSide())
	      {
	          IPokemob pokemob = PokeInfo.getPokemob(event.getPlayer());
	          if (pokemob != null)
	          {
	              final ServerPlayer player = (ServerPlayer) event.getPlayer();
	              final ItemStack stack = PokecubeManager.pokemobToItem(pokemob);
	              PokecubeManager.heal(stack, event.getEntityLiving().level);
	              pokemob = PokecubeManager.itemToPokemob(stack, event.getPlayer().level);
	              pokemob.getEntity().isAlive();
	              pokemob.getEntity().deathTime = -1;
	              PokeInfo.setPokemob(event.getPlayer(), pokemob);
	              if (!player.level.isClientSide())
	              {
	            	  CapabilitySync.sendUpdate(player);
	                  player.containerMenu.getCarried().getContainerItem();
	              }
	          }
	      }
	  }
    
    static HashSet<UUID> syncSchedule = new HashSet<>();
    
        @SubscribeEvent
        public void PlayerLoggedInEvent(final PlayerEvent.PlayerLoggedInEvent event)
        {
            if (Dist.DEDICATED_SERVER != null) EventHandler.syncSchedule.add(event.getPlayer().getUUID());
        }
    
        @SubscribeEvent
        public void PlayerLoggedOutEvent(final PlayerEvent.PlayerLoggedOutEvent event)
        {
            EventHandler.syncSchedule.remove(event.getPlayer().getUUID());
        }
    
        @SubscribeEvent
        public void onEntityCapabilityAttach(final AttachCapabilitiesEvent<Entity> event)
        {
            if (event.getObject() instanceof Player) event.addCapability(EventHandler.DATACAP,
                    new DataSyncWrapper());
        }
    
    @SubscribeEvent
    public void entityJoinWorld(final EntityJoinWorldEvent evt)
    {
        if (evt.getWorld().isClientSide()) return;
        if (evt.getEntity().getPersistentData().getBoolean("is_a_player"))
        {
            final IPokemob evo = CapabilityPokemob.getPokemobFor(evt.getEntity());
            if (evo != null)
            {
                final UUID uuid = UUID.fromString(evt.getEntity().getPersistentData().getString("playerID"));
                final Player player = evt.getWorld().getPlayerByUUID(uuid);
                PokeInfo.setPokemob(player, evo);
                evt.setCanceled(true);
                return;
            }
        }
        else if (evt.getEntity() instanceof ServerPlayer) CapabilitySync.sendUpdate((Player) evt.getEntity());
    }
}
