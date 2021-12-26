package pokecube.pokeplayer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.items.pokecubes.PokecubeManager;
import pokecube.core.network.packets.PacketDataSync;
import pokecube.pokeplayer.inventory.InventoryPlayerPokemob;
import pokecube.pokeplayer.network.DataSyncWrapper;
import thut.api.world.mobs.data.DataSync;
import thut.core.common.handlers.PlayerDataHandler;
import thut.core.common.handlers.PlayerDataHandler.PlayerData;
import thut.core.common.network.CapabilitySync;
import thut.core.common.world.mobs.data.SyncHandler;

public class PokeInfo extends PlayerData
{
    private ItemStack stack    = ItemStack.EMPTY;
    private IPokemob  pokemob;

    public InventoryPlayerPokemob pokeInventory;

    public PokeInfo() {}
   
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
    
    // Called when it transforms into pokemon.//	
    public void set(final IPokemob pokemob, final Player player)
    {
        if (this.pokemob != null || pokemob == null) this.resetPlayer(player);
        if (pokemob == null || this.pokemob == pokemob) return;
        this.stack = PokecubeManager.pokemobToItem(pokemob);
        this.pokemob = pokemob;
        this.pokeInventory = new InventoryPlayerPokemob(this, player.level);
        player.getPersistentData().putBoolean("is_a_player", false);
        
        final DataSync sync = SyncHandler.getData(player);
        if (sync instanceof DataSyncWrapper) ((DataSyncWrapper) sync).wrapped = this.pokemob.dataSync();
        if (player instanceof ServerPlayer) PacketDataSync.syncData(player, this.getIdentifier());
        this.save(player);
    }

    //Reset Player//
    public void resetPlayer(final Player player)
    {
        final DataSync sync = SyncHandler.getData(player);
        if (sync instanceof DataSyncWrapper) ((DataSyncWrapper) sync).wrapped = sync;
        if (this.pokemob == null && !player.level.isClientSide()) return;
        this.pokemob = null;
        this.stack = ItemStack.EMPTY;
        this.pokeInventory = null;
        this.save(player);
        if (!player.level.isClientSide()) CapabilitySync.sendUpdate(player);
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
            ((ServerPlayer) player).nextContainerCounter();
        }
    }

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
        if (this.pokemob == null) return ItemStack.EMPTY;
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
    }

    @Override
    public void readFromNBT(final CompoundTag tag)
    {
        this.stack = ItemStack.of(tag);
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
}
