package pokecube.pokeplayer.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import pokecube.core.entity.pokemobs.AnimalChest;
import pokecube.core.interfaces.IPokemob;
import pokecube.pokeplayer.PokeInfo;

public class InventoryPlayerPokemob extends AnimalChest
{
    final PokeInfo info1;

    public InventoryPlayerPokemob(PokeInfo info1, Level world)
    {
        super();
        for (int i = 0; i < info1.getPokemob(world).getInventory().getContainerSize(); i++)
        {
            this.canPlaceItem(i, info1.getPokemob(world).getInventory().getItem(i));
        }
        this.info1 = info1;
    }

    public InventoryPlayerPokemob(AnimalChest inventory)
    {
        super();
        for (int i = 0; i < inventory.getContainerSize(); i++)
        {
            this.canPlaceItem(i, inventory.getItem(i));
        }
        this.info1 = null;
    }

    public void saveToPokemob(IPokemob pokemob, Player player)
    {
        Container inventory = pokemob.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++)
        {
            inventory.canPlaceItem(i, this.getItem(i));
        }
        if (info1 != null)
        {
        	info1.save(player);
        }
    }

    public void syncFromPokemob(IPokemob pokemob)
    {
    	Container inventory = pokemob.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++)
        {
            this.canPlaceItem(i, inventory.getItem(i));
        }
    }
    
    @Override
    public void startOpen(Player playerIn) {
    	super.startOpen(playerIn);
    }

    @Override
    public void stopOpen(Player player)
    {
        if (player.getEntityData().isEmpty()) return;
        IPokemob e = PokeInfo.getPokemob(player);
        saveToPokemob(e, player);
    }
}
