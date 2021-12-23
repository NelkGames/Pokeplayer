//package pokecube.pokeplayer.inventory;
//
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.inventory.IInventory;
//import net.minecraft.world.World;
//import pokecube.core.entity.pokemobs.AnimalChest;
//import pokecube.core.interfaces.IPokemob;
//import pokecube.pokeplayer.PokeInfo;
//
//public class InventoryPlayerPokemob extends AnimalChest
//{
//    final PokeInfo info;
//
//    public InventoryPlayerPokemob(PokeInfo info, World world)
//    {
//        super();
//        for (int i = 0; i < info.getPokemob(world).getInventory().getContainerSize(); i++)
//        {
//            this.canPlaceItem(i, info.getPokemob(world).getInventory().getItem(i));
//        }
//        this.info = info;
//    }
//
//    public InventoryPlayerPokemob(AnimalChest inventory)
//    {
//        super();
//        for (int i = 0; i < inventory.getContainerSize(); i++)
//        {
//            this.canPlaceItem(i, inventory.getItem(i));
//        }
//        this.info = null;
//    }
//
//    public void saveToPokemob(IPokemob pokemob, PlayerEntity player)
//    {
//        IInventory inventory = pokemob.getInventory();
//        for (int i = 0; i < inventory.getContainerSize(); i++)
//        {
//            inventory.canPlaceItem(i, this.getItem(i));
//        }
//        if (info != null)
//        {
//            info.save(player);
//        }
//    }
//
//    public void syncFromPokemob(IPokemob pokemob)
//    {
//        IInventory inventory = pokemob.getInventory();
//        for (int i = 0; i < inventory.getContainerSize(); i++)
//        {
//            this.canPlaceItem(i, inventory.getItem(i));
//        }
//    }
//    
//    @Override
//    public void startOpen(PlayerEntity playerIn) {
//    	super.startOpen(playerIn);
//    }
//
//    @Override
//    public void stopOpen(PlayerEntity player)
//    {
//        if (player.getEntityData().isEmpty()) return;
//        IPokemob e = PokeInfo.getPokemob(player);
//        saveToPokemob(e, player);
//    }
//}
