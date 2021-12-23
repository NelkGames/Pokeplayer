package pokecube.pokeplayer.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import pokecube.core.network.EntityProvider;
import pokecube.pokeplayer.PokeInfo;
import thut.core.common.handlers.PlayerDataHandler;

public class EntityProviderPokeplayer extends EntityProvider
{
	 public EntityProviderPokeplayer(final EntityProvider defaults)
    {
        super(defaults);
    }

    @Override
    public Entity getEntity(final Level world, final int id, final boolean expectsPokemob)
    {
        final Entity ret = world.getEntity(id);
        if (expectsPokemob && ret instanceof Player)
        {
            Minecraft instance = Minecraft.getInstance();
			final Player player = instance.player;
            final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
            if (info.getPokemob(world) != null) return info.getPokemob(world).getEntity();
        }
        return super.getEntity(world, id, expectsPokemob);
    }
}
