//package pokecube.pokeplayer;
//
//import net.minecraft.entity.MobEntity;
//import net.minecraft.entity.Pose;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.util.DamageSource;
//import net.minecraft.world.World;
//import pokecube.core.PokecubeCore;
//import pokecube.core.ai.tasks.combat.management.FindTargetsTask;
//import pokecube.core.interfaces.IMoveConstants;
//import pokecube.core.interfaces.IPokemob;
//import pokecube.core.interfaces.pokemob.ai.GeneralStates;
//import pokecube.core.interfaces.pokemob.ai.LogicStates;
//import pokecube.core.items.pokecubes.PokecubeManager;
//import pokecube.core.network.packets.PacketDataSync;
//import pokecube.core.utils.EntityTools;
//import pokecube.core.utils.PokeType;
//import pokecube.pokeplayer.inventory.InventoryPlayerPokemob;
//import pokecube.pokeplayer.network.DataSyncWrapper;
//import pokecube.pokeplayer.network.PacketTransform;
//import thut.api.world.mobs.data.Data;
//import thut.api.world.mobs.data.DataSync;
//import thut.core.common.handlers.PlayerDataHandler;
//import thut.core.common.handlers.PlayerDataHandler.PlayerData;
//import thut.core.common.world.mobs.data.SyncHandler;
//
//public class PokeInfo extends PlayerData
//{
//    private ItemStack stack    = ItemStack.EMPTY;
//    private IPokemob  pokemob;
//    private boolean   attached = false;
//
//    public DamageSource           lastDamage = null;
//    public InventoryPlayerPokemob pokeInventory;
//    public float                  originalHeight;
//    public float                  originalWidth;
//    public float                  originalHP;
//
//    public PokeInfo()
//    {
//    }
//
//    // Called when it transforms into pokemon.//	
//    public void set(final IPokemob pokemob, final PlayerEntity player)
//    {
//        if (this.pokemob != null || pokemob == null) this.resetPlayer(player);
//        if (pokemob == null || this.pokemob == pokemob) return;
//        if (this.attached) return;
//        this.stack = PokecubeManager.pokemobToItem(pokemob);
//        this.pokemob = pokemob;
//        this.pokeInventory = new InventoryPlayerPokemob(this, player.getEntity().level);
//        this.originalHeight = player.getBbHeight();
//        this.originalWidth = player.getBbWidth();
//        this.originalHP = player.getMaxHealth();
//        pokemob.getEntity().setLevel(player.getEntity().level);
//        pokemob.getEntity().getPersistentData().putBoolean("is_a_player", true);
//        pokemob.getEntity().getPersistentData().putString("playerID", player.getUUID().toString());
//        pokemob.getEntity().getPersistentData().putString("oldName", pokemob.getPokemonNickname());
//        pokemob.setPokemonNickname(player.getDisplayName().getString());
//        pokemob.setOwner(player);
//        pokemob.initAI();
//        player.getScale();
//        final DataSync sync = SyncHandler.getData(player);
//        if (sync instanceof DataSyncWrapper) ((DataSyncWrapper) sync).wrapped = this.pokemob.dataSync();
//        if (player instanceof ServerPlayerEntity) PacketDataSync.syncData(player, this.getIdentifier());
//        this.save(player);
//    }
//
//    //Reset Player//
//    public void resetPlayer(final PlayerEntity player)
//    {
//        final DataSync sync = SyncHandler.getData(player);
//        if (sync instanceof DataSyncWrapper) ((DataSyncWrapper) sync).wrapped = sync;
//        if (this.pokemob == null && !player.getEntity().level.isClientSide()) return;
//        player.getEyeHeight();
//        player.getScale();
//        this.setFlying(player, false);
//        this.pokemob = null;
//        this.stack = ItemStack.EMPTY;
//        this.pokeInventory = null;
//        this.save(player);
//        if (!player.getEntity().level.isClientSide()) EventsHandler.sendUpdate(player);
//    }
//
//    public void setPlayer(final PlayerEntity player)
//    {
//        if (this.pokemob == null) return;
//        final DataSync sync = SyncHandler.getData(player);
//        if (sync instanceof DataSyncWrapper) ((DataSyncWrapper) sync).wrapped = this.pokemob.dataSync();
//        this.pokemob.setSize((float) (this.pokemob.getSize() / PokecubeCore.getConfig().scalefactor));
//        player.maxUpStep = this.pokemob.getEntity().maxUpStep;
//        this.setFlying(player, true);
//        this.save(player);
//        if (!player.getEntity().level.isClientSide())
//        {
//            EventsHandler.sendUpdate(player);
//            ((ServerPlayerEntity) player).refreshContainer(player.containerMenu, player.containerMenu.getItems());
//            // // Fixes the inventories appearing to vanish
//            player.getPersistentData().putLong("_pokeplayer_evolved_", player.getEntity().level.getGameTime() + 50);
//        }
//    }
//
//    public void postPlayerTick(final PlayerEntity player)
//    {
//        if (this.pokemob == null) return;
//        player.maxUpStep = this.pokemob.getEntity().maxUpStep;
//    }
//
//    public void onUpdate(final PlayerEntity player, final World world)
//    {
//        if (this.getPokemob(world) == null && !this.stack.isEmpty()) this.resetPlayer(player);
//        if (this.pokemob == null) return;
//        final MobEntity poke = this.pokemob.getEntity();
//
//        final float eye = poke.getEyeHeight(Pose.STANDING);
//        if (eye != player.getEyeHeight()) player.getScale();
//
//        // Fixes pokemob sometimes targetting self.
//        if (poke.getTarget() == player || poke.getTarget() == poke)
//        {
//            boolean old = FindTargetsTask.handleDamagedTargets;
//            FindTargetsTask.handleDamagedTargets = false;
//            poke.setTarget(null);
//            pokemob.setTargetID(-1);
//            FindTargetsTask.handleDamagedTargets = old;
//        }
//        
//        // Flag the data sync dirty every so often to ensure things stay synced.
//        if (poke.tickCount % 20 == 0) for (final Data<?> d : this.pokemob.dataSync().getAll())
//            d.setDirty(true);
//
//        // Ensure it is tamed.
//        this.pokemob.setGeneralState(GeneralStates.TAMED, true);
//        // No Stay mode for pokeplayers.
//        this.pokemob.setGeneralState(GeneralStates.STAYING, false);
//        // Update the mob.
//        // Ensure the mob has correct world.
//        poke.setLevel(player.getEntity().level);
//        poke.inChunk = true;
//        // No clip to prevent collision effects from the mob itself.
//        poke.horizontalCollision = true;
//
////        poke.canUpdate();
//        
//        // Update location
//        poke.walkDist = Integer.MAX_VALUE;
//        EntityTools.copyEntityTransforms(poke, player);
//
//        // Deal with health
//        if (player.isCreative())
//        {
//            poke.setHealth(poke.getMaxHealth());
//            this.pokemob.setHungerTime(-PokecubeCore.getConfig().pokemobLifeSpan / 4);
//        }
//        
//        float health = poke.getHealth();
//        
//        float playerHealth = player.getHealth();
//        
//        playerHealth = pokemob.getHealth();
//        poke.setHealth(playerHealth);
//        
//        // do not manage hp for creative mode players.
//        if (!player.isCreative()) {
//        	if (player instanceof ServerPlayerEntity && player.inChunk)
//	        {
//        		// Set Player is Fly!
//        		setFlying(player, true);
//        		
////	            float playerHealth = player.getHealth();
////	
////	            // Sync pokehealth to player health.
////	            playerHealth = pokemob.getHealth();
////	            poke.setHealth(playerHealth);
//	
//	            health = playerHealth;
//	
//	            final PacketTransform packet = new PacketTransform();
//	            //packet.getTag().putInt("__entityid__", player.getEntity().getId());
//	            packet.id = player.getEntity().getId();
//	            packet.getTag().putBoolean("U", true);
//	            packet.getTag().putFloat("H", health);
//	            packet.getTag().putFloat("M", poke.getMaxHealth());
//	            PacketTransform.ASSEMBLY.sendTo(packet, (ServerPlayerEntity) player);
//	
//	            // Fixes the inventories appearing to vanish
//	            if (player.getPersistentData().contains("_pokeplayer_evolved_") && player.getPersistentData().getLong(
//	                    "_pokeplayer_evolved_") > player.getEntity().level.getGameTime()) ((ServerPlayerEntity) player)
//	                            .refreshContainer(player.containerMenu, player.containerMenu.getItems());
//	            else player.getPersistentData().remove("_pokeplayer_evolved_");
//	        }
//    	}
//	        
//        if (player.getHealth() > 0) player.deathTime = -1;
//        poke.deathTime = player.deathTime;
//
//        int num = this.pokemob.getHungerTime();
//        final int max = PokecubeCore.getConfig().pokemobLifeSpan;
//        num = Math.round((max - num) * 20 / (float) max);
//        if (player.isCreative()) {
//        	num = 20;
//        }
//        player.getFoodData().setFoodLevel(num);
//
////        this.updateFloating(player);
//        this.updateFlying(player);
//        this.updateSwimming(player);
//
//        // Synchronize the hitbox locations
//        poke.setPos(player.getX(), player.getY(), player.getZ());
//    }
//
//    public void clear()
//    {
//        this.pokemob = null;
//        this.pokeInventory = null;
//        this.stack = ItemStack.EMPTY;
//    }
//
//    public void save(final PlayerEntity player)
//    {
//        if (!player.getEntity().level.isClientSide()) PlayerDataHandler.getInstance().save(player.getStringUUID(), //getEntity().level.getCachedUniqueIdString(),
//                this.getIdentifier());
//    }
//
//    private void setFlying(final PlayerEntity player, final boolean set)
//    {
//        if (this.pokemob == null) return;
//        final boolean fly = this.pokemob.floats() || this.pokemob.flys();
//        boolean noFloat = pokemob.getLogicState(LogicStates.SITTING) || pokemob.getLogicState(LogicStates.SLEEPING)
//                || pokemob.isGrounded()
//                || (pokemob.getStatus() & (IMoveConstants.STATUS_SLP + IMoveConstants.STATUS_FRZ)) > 0;
//                
//        if (fly && !player.isCreative() && !noFloat)
//        {
//            player.abilities.flying = set;
//            player.onUpdateAbilities();
//        }
//    }
//
//    private void updateFlying(final PlayerEntity player)
//    {
//        if (this.pokemob == null) return;
//        if (this.pokemob.floats() || this.pokemob.flys())
//        {
//            player.fallDistance = 0;
//            if (player instanceof ServerPlayerEntity) ((ServerPlayerEntity) player).connection.aboveGroundTickCount = 0;
//        }
//    }
////
////    private void waitFly(final PlayerEntity player)
////    {
////        if (this.pokemob == null) return;
////        if (!player.isCrouching() && this.pokemob.floats() && !player.isFallFlying())
////        {
////            boolean noFloat = pokemob.getLogicState(LogicStates.SITTING) || pokemob.getLogicState(LogicStates.SLEEPING)
////                    || pokemob.isGrounded()
////                    || (pokemob.getStatus() & (IMoveConstants.STATUS_SLP + IMoveConstants.STATUS_FRZ)) > 0;
////
////            if ()!noFloat)
////        }
////    }
//
//    private void updateSwimming(final PlayerEntity player)
//    {
//        if (this.pokemob == null) return;
//        if (this.pokemob.getPokedexEntry().swims() || this.pokemob.isType(PokeType.getType("water"))) { 
//        	player.setAirSupply(300);      	
//        }
//    }
//
//    public ItemStack detach()
//    {
//        this.attached = false;
//        if (this.pokemob == null) return ItemStack.EMPTY;
//        this.pokemob.getEntity().getPersistentData().putBoolean("is_a_player", false);
//        return PokecubeManager.pokemobToItem(this.pokemob);
//    }
//
//    public void setStack(final ItemStack stack)
//    {
//        this.stack = stack;
//    }
//
//    @Override
//    public String dataFileName()
//    {
//        return "pokeplayer";
//    }
//
//    @Override
//    public String getIdentifier()
//    {
//        return "pokeplayer-data";
//    }
//
//    @Override
//    public boolean shouldSync()
//    {
//        return false;
//    }
//
//    @Override
//    public void writeToNBT(final CompoundNBT tag)
//    {
//        if (this.pokemob != null)
//        {
//            this.stack = PokecubeManager.pokemobToItem(this.pokemob);
//            this.stack.save(tag);
//        }
//        else if (!this.stack.isEmpty()) this.stack.save(tag);
//        tag.putFloat("h", this.originalHeight);
//        tag.putFloat("w", this.originalWidth);
//        tag.putFloat("hp", this.originalHP);
//    }
//
//    @Override
//    public void readFromNBT(final CompoundNBT tag)
//    {
//        this.stack = ItemStack.of(tag);
//        this.originalHeight = tag.getFloat("h");
//        this.originalWidth = tag.getFloat("w");
//        this.originalHP = tag.getFloat("hp");
//        if (this.originalHP <= 1) this.originalHP = 20;
//    }
//
//    public IPokemob getPokemob(final World world)
//    {
//        if (this.pokemob == null && !this.stack.isEmpty())
//        {
//            this.pokemob = PokecubeManager.itemToPokemob(this.stack, world);
//            if (this.pokemob == null) this.stack = ItemStack.EMPTY;
//        }
//        return this.pokemob;
//    }
//
//    public static void setPokemob(final PlayerEntity player, final IPokemob pokemob)
//    {
//        PokeInfo.setMapping(player, pokemob);
//    }
//
//    public static void savePokemob(final PlayerEntity player)
//    {
//        final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
//        if (info != null) info.save(player);
//    }
//
//    private static void setMapping(final PlayerEntity player, final IPokemob pokemob)
//    {
//        final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
//        info.set(pokemob, player);
//        if (pokemob != null)
//        {
//            info.setPlayer(player);
//            info.save(player);
//        }
//    }
//
//    public static IPokemob getPokemob(final PlayerEntity player)
//    {
//        if (player == null || player.getUUID() == null) return null;
//        final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
//        return info.getPokemob(player.getEntity().level);
//    }
//
//    public static void updateInfo(final PlayerEntity player, final World world)
//    {
//        final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
//        try
//        {
//            info.onUpdate(player, world);
//        }
//        catch (final Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//}
