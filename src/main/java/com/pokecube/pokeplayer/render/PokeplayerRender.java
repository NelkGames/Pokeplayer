package com.pokecube.pokeplayer.render;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import pokecube.api.entity.pokemob.IPokemob;
import pokecube.api.entity.pokemob.PokemobCaps;
import pokecube.api.utils.PokeType;
import pokecube.core.PokecubeCore;
import pokecube.core.ai.tasks.idle.HungerTask;
import pokecube.core.utils.PokemobTracker;
import thut.api.ThutCaps;
import thut.api.attachments.TrackedAttachment;
import thut.api.entity.ICopyMob;
import thut.api.entity.event.CopySetEvent;
import thut.api.entity.event.CopyUpdateEvent;
import thut.api.maths.Vector3;
import thut.wearables.inventory.PlayerWearables;

public class PokeplayerRender {

    private static final ResourceLocation STEP = ResourceLocation.parse("pokeplayer:step_adjust");

    public static void onPlayerTick(final PlayerTickEvent.Pre event)
    {
        final ICopyMob copy = ThutCaps.getCopyMob(event.getEntity());
        copy.setFullTick(true);

        // If we are copied, then just use the mob's step height.
        if (copy != null && copy.getCopiedMob() != null)
        {
            double dStep = copy.getCopiedMob().getAttribute(Attributes.STEP_HEIGHT).getValue() - event.getEntity()
                    .getAttribute(Attributes.STEP_HEIGHT).getValue();
            AttributeModifier mod = new AttributeModifier(STEP, dStep, AttributeModifier.Operation.ADD_VALUE);
            event.getEntity().getAttribute(Attributes.STEP_HEIGHT).addOrUpdateTransientModifier(mod);
        }
        else if (event.getEntity().getAttribute(Attributes.STEP_HEIGHT).hasModifier(STEP))
        {
            event.getEntity().getAttribute(Attributes.STEP_HEIGHT).removeModifier(STEP);
        }
    }

    public static void onCopySet(final CopySetEvent event)
    {
        if (event.getEntity() instanceof Player player)
        {
            ResourceLocation FLYID = ResourceLocation.parse("pokeplayer:fly_sync");
            player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT).removeModifier(FLYID);

            IPokemob oldMob = PokemobCaps.getPokemobFor(event.oldCopy);
            if (oldMob != null) PokemobTracker.removePokemob(oldMob);
            if (event.newCopy != null) event.newCopy.getPersistentData().putUUID("copy_parent", player.getUUID());
        }
    }

    public static void onCopyTick(final CopyUpdateEvent event)
    {
        if (!(event.realEntity instanceof Player player)) return;
        final Pose pose = event.realEntity.getPose();
        var entity = event.getEntity();
        // Short mobs need to be able to walk properly in small spaces, so force
        // standing pose if not in water
        if (entity.getBbHeight() < 1 && pose == Pose.SWIMMING && !event.realEntity.isInWaterOrBubble())
            event.realEntity.setPose(Pose.STANDING);

        entity.setData(PlayerWearables.TYPE, player.getData(PlayerWearables.TYPE));

        final IPokemob pokemob = PokemobCaps.getPokemobFor(entity);
        if (pokemob != null)
        {
            var hunger = pokemob.getHungerTime();
            var foodData = player.getFoodData();
            int food = foodData.getFoodLevel();
            float pokeHunger = HungerTask.calculateHunger(pokemob);
            int hungerRate = PokecubeCore.getConfig().pokemobLifeSpan / 25;
            if (pokeHunger < 0.8)
            {
                if (food > 0)
                {
                    foodData.setFoodLevel(food - 1);
                    pokemob.setHungerTime(hunger - hungerRate);
                }
            }
            else if (foodData.needsFood() && pokeHunger > 0.9)
            {
                foodData.setFoodLevel(food + 1);
                pokemob.setHungerTime(hunger + hungerRate);
            }
            // TODO find appropriate places to do this instead of once per second.
            if (player.tickCount % 20 == 0) pokemob.markDirty();

            pokemob.setOwner(player);
            pokemob.setDataSync(ThutCaps.getDataSync(player));
            setFlying(player, pokemob);
            updateFloating(player, pokemob);
            updateFlying(player, pokemob);
            updateSwimming(player, pokemob);

            final ICopyMob copy = ThutCaps.getCopyMob(player);
            if (copy instanceof TrackedAttachment tracked && !(player.level().isClientSide()))
            {
                if (pokemob.isDirty()) tracked.markDirty();
                if (pokemob.getGenes().isDirty()) tracked.markDirty();
            }
        }
    }

    public static void setFlying(final Player player, final IPokemob pokemob)
    {
        if (pokemob == null) return;
        final boolean fly = pokemob.floats() || pokemob.flys();
        if (player.mayFly() != fly)
        {
            ResourceLocation FLYID = ResourceLocation.parse("pokeplayer:fly_sync");
            if (fly) player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT).addOrReplacePermanentModifier(
                    new AttributeModifier(FLYID, 1, AttributeModifier.Operation.ADD_VALUE));
            else player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT).addOrReplacePermanentModifier(
                    new AttributeModifier(FLYID, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            player.onUpdateAbilities();
        }
    }

    public static void updateFlying(final Player player, final IPokemob pokemob)
    {
        if (pokemob == null) return;
        if (pokemob.floats() || pokemob.flys())
        {
            player.fallDistance = 0;
            if (player instanceof ServerPlayer) ((ServerPlayer) player).connection.aboveGroundTickCount = 0;
        }
    }

    public static void updateFloating(final Player player, final IPokemob pokemob)
    {
        if (pokemob == null || !pokemob.floats()) return;
        if (!player.isShiftKeyDown())
        {
            player.setNoGravity(false);
            double gravity = player.getGravity();
            player.setNoGravity(true);
            var level = player.level();
            Vector3 hereVec = new Vector3(player);
            Vector3 nextVec = new Vector3(hereVec).addTo(0, -pokemob.getFloatHeight(), 0);
            var hit = level.clip(new ClipContext(hereVec.toVec3d(), nextVec.toVec3d(), ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.ANY, player));
            Vector3 push = new Vector3(0, gravity, 0);
            if (hit.getType() == HitResult.Type.MISS) push.scalarMultBy(-1);
            else
            {
                double offset = 1 - (player.getY() - hit.getLocation().y()) / pokemob.getFloatHeight();
                push.scalarMultBy(offset);
            }
            double vy = player.getDeltaMovement().y;
            if (Math.signum(vy) != Math.signum(push.y)) push.addVelocities(player);
        }
        else player.setNoGravity(false);
    }

    public static void updateSwimming(final Player player, final IPokemob pokemob)
    {
        if (pokemob == null) return;
        if (pokemob.getPokedexEntry().swims() || pokemob.isType(PokeType.getType("water"))) player.setAirSupply(300);
    }
}
