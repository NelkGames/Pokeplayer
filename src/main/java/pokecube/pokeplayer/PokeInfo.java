package pokecube.pokeplayer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import pokecube.core.PokecubeCore;
import pokecube.core.ai.tasks.combat.management.FindTargetsTask;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.pokemob.ai.GeneralStates;
import pokecube.core.items.pokecubes.PokecubeManager;
import pokecube.core.network.packets.PacketDataSync;
import pokecube.pokeplayer.inventory.InventoryPlayerPokemob;
import pokecube.pokeplayer.network.DataSyncWrapper;
import pokecube.pokeplayer.network.PacketTransform;
import thut.api.world.mobs.data.Data;
import thut.api.world.mobs.data.DataSync;
import thut.core.common.handlers.PlayerDataHandler;
import thut.core.common.handlers.PlayerDataHandler.PlayerData;
import thut.core.common.network.CapabilitySync;
import thut.core.common.world.mobs.data.SyncHandler;

public class PokeInfo extends PlayerData
{
    private ItemStack 		stack    = ItemStack.EMPTY;
    private IPokemob  		pokemob;

    //
    private boolean   		attached = false;
    public DamageSource     lastDamage = null;
    public float            originalHP;
    //
    
    public InventoryPlayerPokemob pokeInventory;

    public PokeInfo() {}
   
    //-Make Pokeplayer
    public static void setPokemob(final Player player, final IPokemob pokemob)
    {
        PokeInfo.setMapping(player, pokemob);
    }
    
    private static void setMapping(final Player player, final IPokemob pokemob)
    {
        final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
        info.set(pokemob, player);
        if (pokemob != null)
        {
        	info.setPlayer(player);
            info.save(player);
        }
    }
    
    //-Called when it transforms into pokemon
    public void set(final IPokemob pokemob, final Player player)
    {
        if (this.pokemob != null || pokemob == null) this.resetPlayer(player);
        if (pokemob == null || this.pokemob == pokemob) return;
        
        //
        if (this.attached) return;
        //
        
        this.stack = PokecubeManager.pokemobToItem(pokemob);
        this.pokemob = pokemob;
        this.pokeInventory = new InventoryPlayerPokemob(this, player.level);
        //player.getPersistentData().putBoolean("is_a_player", false);
        
        this.originalHP = player.getMaxHealth();
        pokemob.getEntity().level = player.getLevel();
        pokemob.getEntity().getPersistentData().putString("playerID", player.getUUID().toString());
        player.getPersistentData().putString("playerID", player.getUUID().toString());
        player.getPersistentData().putString("oldName", pokemob.getPokemonNickname());
        pokemob.setPokemonNickname(player.getDisplayName().getString());
        pokemob.setOwner(player);
        pokemob.initAI();
        
        final DataSync sync = SyncHandler.getData(player);
        if (sync instanceof DataSyncWrapper) ((DataSyncWrapper) sync).wrapped = this.pokemob.dataSync();
        if (player instanceof ServerPlayer) PacketDataSync.syncData(player, this.getIdentifier());
        this.save(player);
    }

    //-Reset Player
    public void resetPlayer(final Player player)
    {
        final DataSync sync = SyncHandler.getData(player);
        if (sync instanceof DataSyncWrapper) ((DataSyncWrapper) sync).wrapped = sync;
        if (this.pokemob == null && !player.level.isClientSide()) return;
        this.pokemob = null;
        this.stack = ItemStack.EMPTY;
        this.pokeInventory = null;
        this.save(player);
        if (!player.level.isClientSide()) 
        	CapabilitySync.sendUpdate(player);
    }

    public void setPlayer(final Player player)
    {
        if (this.pokemob == null) return;
        final DataSync sync = SyncHandler.getData(player);
        if (sync instanceof DataSyncWrapper) ((DataSyncWrapper) sync).wrapped = this.pokemob.dataSync();
        this.save(player);
        player.refreshDimensions();
        player.getEyeHeight(Pose.STANDING);
        if (!player.level.isClientSide())
        {
        	CapabilitySync.sendUpdate(player);
            ((ServerPlayer) player).containerMenu.getItems();
            // Fixes the inventories appearing to vanish
            player.getPersistentData().putLong("_pokeplayer_evolved_", player.level.getGameTime() + 50);
        }
    }
    
    /*
     * Test Feature
     */
    public void postPlayerTick(final Player player)
    {
    	if (this.pokemob == null) return;
      	player.maxUpStep = this.pokemob.getEntity().maxUpStep;
    }
    
    public void onUpdate(final Player player, final Level level)
    {
      if (this.getPokemob(level) == null && !this.stack.isEmpty()) this.resetPlayer(player);
      if (this.pokemob == null) return;
      final Mob poke = this.pokemob.getEntity();
      
      // Fixes pokemob sometimes targetting self.
      if (poke.getTarget() == player || poke.getTarget() == poke)
      {
          boolean old = FindTargetsTask.handleDamagedTargets;
          FindTargetsTask.handleDamagedTargets = false;
          poke.setTarget(null);
          pokemob.setTargetID(-1);
          FindTargetsTask.handleDamagedTargets = old;
      }
      
      // Flag the data sync dirty every so often to ensure things stay synced.
      if (poke.tickCount % 20 == 0) for (final Data<?> d : this.pokemob.dataSync().getAll())
          d.setDirty(true);

      // Ensure it is tamed.
      this.pokemob.setGeneralState(GeneralStates.TAMED, true);
      // No Stay mode for pokeplayers.
      this.pokemob.setGeneralState(GeneralStates.STAYING, false);
      // No clip to prevent collision effects from the mob itself.
      poke.level = player.level;
      //poke.isAddedToWorld();
      poke.horizontalCollision = true;
      
      poke.canUpdate();
      
      // Update location
      poke.walkDist = Integer.MAX_VALUE;

      // Deal with health
      if (player.isCreative())
      {
          poke.setHealth(poke.getMaxHealth());
          this.pokemob.setHungerTime(-PokecubeCore.getConfig().pokemobLifeSpan / 4);
      }
      
      float health = poke.getHealth();     
      float playerHealth = player.getHealth();
      
      playerHealth = pokemob.getHealth();
      poke.setHealth(playerHealth);
      
      // do not manage hp for creative mode players.
      if (!player.isCreative()) {
      if (player instanceof ServerPlayer)// && player.isAddedToWorld())
        {
            health = playerHealth;
            lastDamage = null;
            
            final PacketTransform packet = new PacketTransform();
           // packet.getTag().putInt("__entityid__", player.getId());
            packet.id = player.getId();
            packet.getTag().putBoolean("U", true);
            packet.getTag().putFloat("H", health);
            packet.getTag().putFloat("M", poke.getMaxHealth());
            PacketTransform.ASSEMBLY.sendTo(packet, (ServerPlayer) player);

            // Fixes the inventories appearing to vanish
            if (player.getPersistentData().contains("_pokeplayer_evolved_") && player.getPersistentData().getLong(
                    "_pokeplayer_evolved_") > player.level.getGameTime()) ((ServerPlayer) player)
                            //.refreshContainer(player.containerMenu, player.containerMenu.getItems());
            				//.initMenu(player.containerMenu);
            				//.getInventory();
            				.containerMenu.getItems();
            else player.getPersistentData().remove("_pokeplayer_evolved_");
	        }
  		}
      
      	if (player.getHealth() > 0) player.deathTime = -1;
     	poke.deathTime = player.deathTime;

     	int num = this.pokemob.getHungerTime();
     	final int max = PokecubeCore.getConfig().pokemobLifeSpan;
     	num = Math.round((max - num) * 20 / (float) max);
     	if (player.isCreative()) {
     		num = 20;
     	}
     	player.getFoodData().setFoodLevel(num);
    }
    //

    public void clear()
    {
        this.pokemob = null;
        this.pokeInventory = null;
        this.stack = ItemStack.EMPTY;
    }

    public void save(final Player player)
    {
        if (!player.level.isClientSide()) PlayerDataHandler.getInstance().save(player.getStringUUID(),
                this.getIdentifier());
    }

    public ItemStack detach()
    {
    	this.attached = false;
    	//
    	
        if (this.pokemob == null) return ItemStack.EMPTY;
        this.pokemob.getEntity().getPersistentData().putBoolean("is_a_player", false);
        return PokecubeManager.pokemobToItem(this.pokemob);
    }

    public void setStack(final ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public String dataFileName()
    {
        return "pokeplayer";
    }

    @Override
    public String getIdentifier()
    {
        return "pokeplayer-data";
    }

    @Override
    public boolean shouldSync()
    {
        return false;
    }

    @Override
    public void writeToNBT(final CompoundTag tag)
    {
        if (this.pokemob != null)
        {
            this.stack = PokecubeManager.pokemobToItem(this.pokemob);
            this.stack.save(tag);
        }
        else if (!this.stack.isEmpty()) this.stack.save(tag);
        
        //
        tag.putFloat("hp", this.originalHP);
    }

    @Override
    public void readFromNBT(final CompoundTag tag)
    {
        this.stack = ItemStack.of(tag);
        
        //
        this.originalHP = tag.getFloat("hp");
        if (this.originalHP <= 1) this.originalHP = 20;
    }

    public IPokemob getPokemob(final Level world)
    {
        if (this.pokemob == null && !this.stack.isEmpty())
        {
            this.pokemob = PokecubeManager.itemToPokemob(this.stack, world);
            if (this.pokemob == null) this.stack = ItemStack.EMPTY;
        }
        return this.pokemob;
    }

    public static void savePokemob(final Player player)
    {
        final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
        if (info != null) info.save(player);
    }

    public static IPokemob getPokemob(final Player player)
    {
        if (player == null || player.getUUID() == null) return null;
        final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
        return info.getPokemob(player.level);
    }
    
    /* Tests for moves and evolutions
     * No finished!!!!
     */
    
    public static void updateInfo(final Player player, final Level level)
    {
      final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
      try
      {
          info.onUpdate(player, level);
      }
      catch (final Exception e)
      {
          e.printStackTrace();
      }
    }
}
