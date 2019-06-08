package pokecube.pokeplayer.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.network.EntityProvider;
import pokecube.pokeplayer.PokePlayer;

public class EntityProviderPokeplayer extends EntityProvider
{

    public EntityProviderPokeplayer(EntityProvider defaults)
    {
        super(defaults);
    }

    @Override
    public Entity getEntity(World world, int id, boolean expectsPokemob)
    {
        Entity ret = world.getEntityByID(id);
        IPokemob pokemob;
        if (expectsPokemob && ret instanceof PlayerEntity
                && (pokemob = PokePlayer.PROXY.getPokemob((PlayerEntity) ret)) != null) { return pokemob.getEntity(); }
        return super.getEntity(world, id, expectsPokemob);
    }

}
