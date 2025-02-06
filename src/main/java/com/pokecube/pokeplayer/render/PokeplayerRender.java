package com.pokecube.pokeplayer.render;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import pokecube.api.entity.pokemob.IPokemob;
import pokecube.api.entity.pokemob.PokemobCaps;
import pokecube.api.utils.PokeType;
import pokecube.core.network.pokemobs.PacketSyncGene;
import pokecube.core.utils.PokemobTracker;
import thut.api.ThutCaps;
import thut.api.entity.ICopyMob;
import thut.api.entity.event.CopySetEvent;
import thut.api.entity.event.CopyUpdateEvent;
import thut.api.entity.genetics.Alleles;

public class PokeplayerRender
{
    @SuppressWarnings("deprecation")
    public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
        final ICopyMob copy = ThutCaps.getCopyMob(event.player);

        if (copy != null && copy.getCopiedMob() != null)
        {
            if (!event.player.getPersistentData().contains("prevStepUp"))
                event.player.getPersistentData().putFloat("prevStepUp", event.player.maxUpStep);
            event.player.maxUpStep = copy.getCopiedMob().maxUpStep;
        }
        else if (event.player.getPersistentData().contains("prevStepUp"))
        {
            final float prev = event.player.getPersistentData().getFloat("prevStepUp");
            event.player.getPersistentData().remove("prevStepUp");
            event.player.maxUpStep = prev;
        }
    }

    public static void onCopySet(final CopySetEvent event) {
        if (event.newCopy == null && event.getEntity() instanceof Player player) {
            if (!player.getAbilities().instabuild) {
                player.getAbilities().mayfly = false;
                player.onUpdateAbilities();
            }

            IPokemob newMob = PokemobCaps.getPokemobFor(event.newCopy);
            if (player.level.isClientSide()) {
                IPokemob oldMob = PokemobCaps.getPokemobFor(event.oldCopy);
                if (newMob != null) PokemobTracker.addPokemob(newMob);
                if (oldMob != null) PokemobTracker.removePokemob(oldMob);
            }
        }
    }

    public static void onCopyTick(final CopyUpdateEvent event) {
        if (!(event.realEntity instanceof Player player)) return;

        final Pose pose = event.realEntity.getPose();
        if (event.getEntity().getBbHeight() < 1 && pose == Pose.SWIMMING && !event.realEntity.isInWaterOrBubble())
            event.realEntity.setPose(Pose.STANDING);

        final IPokemob pokemob = PokemobCaps.getPokemobFor(event.getEntity());
        if (pokemob != null) {
            setFlying(player, pokemob);
            updateFloating(player, pokemob);
            updateFlying(player, pokemob);
            updateSwimming(player, pokemob);

            if (player instanceof ServerPlayer splayer && splayer.tickCount % 20 == 0) {
                var mobGenes = ThutCaps.getGenetics(pokemob.getEntity());
                pokemob.getEntity().onAddedToWorld();
                for (final Alleles<?, ?> allele : mobGenes.getAlleles().values()) {
                    PacketSyncGene.syncGeneToTracking(event.getEntity(), allele);
                    PacketSyncGene.syncGene(event.getEntity(), allele, splayer);
                }
                pokemob.getEntity().onRemovedFromWorld();
            }
        }
    }

    public static void setFlying(final Player player, final IPokemob pokemob)
    {
        if (pokemob == null) return;
        final boolean fly = pokemob.floats() || pokemob.flys();
        if (!player.getAbilities().instabuild && player.getAbilities().mayfly != fly)
        {
            player.getAbilities().mayfly = fly;
            player.onUpdateAbilities();
        }
    }

    public static void updateFlying(final Player player, final IPokemob pokemob)
    {
        if (pokemob == null) return;
        if (pokemob.floats() || pokemob.flys())
        {
            player.fallDistance = 0;
            if (player instanceof ServerPlayer) ((ServerPlayer) player).fallDistance = 0;
        }
    }

    public static void updateFloating(final Player player, final IPokemob pokemob)
    {
        if (pokemob == null) return;
        if (!player.isShiftKeyDown() && pokemob.floats() && !player.isFallFlying())
        {
            // TODO fix floating effects
        }
    }

    public static void updateSwimming(final Player player, final IPokemob pokemob)
    {
        if (pokemob == null) return;
        if (pokemob.getPokedexEntry().swims() || pokemob.isType(PokeType.getType("water")))
            player.setAirSupply(300);
    }
}
