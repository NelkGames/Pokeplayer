package com.pokecube.pokeplayer.data;

import com.pokecube.pokeplayer.client.PokeplayerClient;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import pokecube.api.data.PokedexEntry;
import pokecube.api.entity.pokemob.IPokemob;
import pokecube.api.entity.pokemob.PokemobCaps;
import pokecube.api.events.pokemobs.EvolveEvent;
import pokecube.core.database.Database;
import pokecube.core.items.ItemPokedex;
import thut.api.ThutCaps;
import thut.api.attachments.TrackedAttachment;
import thut.api.entity.ICopyMob;
import thut.lib.RegHelper;

public class PokeplayerDataHandler {
    public static final PokeplayerDataHandler INSTANCE = new PokeplayerDataHandler();

    public static PokeplayerDataHandler getInstance() { return INSTANCE; }

    public static void onRightClickItem(PlayerInteractEvent.RightClickItem evt)
    {
        // Try using it on self if it is a usable item or a pokedex
        final ICopyMob copy = ThutCaps.getCopyMob(evt.getEntity());
        if (copy != null && copy.getCopiedMob() != null)
        {
            var stack = evt.getItemStack();
            if (stack.getItem() instanceof ItemPokedex && evt.getEntity().isShiftKeyDown())
            {
                stack.interactLivingEntity(evt.getEntity(), copy.getCopiedMob(), evt.getHand());
                evt.setCanceled(true);
                return;
            }
            var usable = PokemobCaps.getPokemobUsable(stack);
            var pokemob = PokemobCaps.getPokemobFor(copy.getCopiedMob());
            if (usable != null && pokemob != null)
            {
                var res = usable.onUse(pokemob, stack, evt.getEntity());
                if (res.getResult().indicateItemUse())
                {
                    evt.setCancellationResult(res.getResult());
                    evt.setCanceled(true);
                }
            }
            if (copy instanceof TrackedAttachment tracked) tracked.markDirty();
        }
    }

    public static void onEvolve(EvolveEvent.Post event)
    {
        var entity = event.mob.getEntity();
        if (entity.getPersistentData().hasUUID("copy_parent"))
        {
            var id = entity.getPersistentData().getUUID("copy_parent");
            var player = entity.level().getPlayerByUUID(id);
            var copy = ThutCaps.getCopyMob(player);
            if (copy != null)
            {
                copy.setCopiedMob(entity);
                event.setCanceled(true);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void onTransform(int level, String[] moves)
    {
        PokeplayerClient.currentLevel = level;
        PokeplayerClient.currentMoves = moves;
    }
}
