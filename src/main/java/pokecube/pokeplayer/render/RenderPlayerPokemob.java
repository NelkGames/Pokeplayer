package pokecube.pokeplayer.render;

import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import pokecube.core.interfaces.IMoveConstants;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.interfaces.pokemob.ai.LogicStates;
import pokecube.core.utils.PokeType;
import pokecube.pokeplayer.PokeInfo;
import thut.api.entity.CopyCaps;
import thut.api.entity.ICopyMob;
import thut.api.entity.event.CopySetEvent;
import thut.api.entity.event.CopyUpdateEvent;

public class RenderPlayerPokemob
{
	public static void onPlayerTick(final PlayerTickEvent event)
    {
        final ICopyMob copy = CopyCaps.get(event.player);

        if (event.player instanceof final ServerPlayer player)
        {
            final Entity cam = player.getCamera();
            
            if (cam != player && cam instanceof Player)
            {
                ICopyMob.copyPositions(player, cam);
                player.connection.teleport(player.getX(), player.getY(), player.getZ(), player.getYRot(), player
                        .getXRot());
            }
        }

        // If we are copied, then just use the mob's step height.
        if (copy != null && copy.getCopiedMob() != null)
        {
            if (!event.player.getPersistentData().contains("prevStepUp")) event.player.getPersistentData().putFloat(
                    "prevStepUp", event.player.maxUpStep);
            event.player.maxUpStep = copy.getCopiedMob().maxUpStep;
        }
        else if (event.player.getPersistentData().contains("prevStepUp"))
        {
            final float prev = event.player.getPersistentData().getFloat("prevStepUp");
            event.player.getPersistentData().remove("prevStepUp");
            event.player.maxUpStep = prev;
            event.player.refreshDimensions();
        }
    }

    public static void onCopySet(final CopySetEvent event)
    {
        if (event.newCopy == null && event.getEntity() instanceof Player)
        {
            final Player player = (Player) event.getEntity();
            if (!player.getAbilities().instabuild)
            {
                player.getAbilities().mayfly = false;
                player.onUpdateAbilities();
            }
        }
    }

    public static void onCopyTick(final CopyUpdateEvent event)
    {
        if (!(event.realEntity instanceof Player)) return;
        final Player player = (Player) event.realEntity;
        final Pose pose = event.realEntity.getPose();
        if (event.getEntity().getBbHeight() < 1 && pose == Pose.SWIMMING && !event.realEntity.isInWaterOrBubble())
            event.realEntity.setPose(Pose.STANDING);

        final IPokemob pokemob = CapabilityPokemob.getPokemobFor(event.getEntity());
        
        //Water_Breathing
        final ItemStack stack = new ItemStack(Blocks.BARRIER);
        
        final MobEffectInstance breathing = new MobEffectInstance(MobEffects.WATER_BREATHING, 300, 0, false, false, true);
        breathing.setCurativeItems(Lists.newArrayList(stack));
        
        if (pokemob != null)
        {   
        	RenderPlayerPokemob.setFlying(player, pokemob);
        	RenderPlayerPokemob.updateFloating(player, pokemob);
        	RenderPlayerPokemob.updateFlying(player, pokemob);
	        RenderPlayerPokemob.updateSwimming(player, pokemob);
	        
	        PokeInfo.updateCollision(player, player.level);
	        
	        if (pokemob.getPokedexEntry().swims() || pokemob.isType(PokeType.getType("water")) && 
	        		player.isEyeInFluid(FluidTags.WATER))
	            player.addEffect(breathing);
        }
    }
    
    public static void setFlying(final Player player, final IPokemob pokemob)
    {
        if (pokemob == null) return;
        final boolean fly = pokemob.floats() || pokemob.flys();
        boolean noFloat = pokemob.getLogicState(LogicStates.SITTING) || pokemob.getLogicState(LogicStates.SLEEPING)
                || pokemob.isGrounded()
                || (pokemob.getStatus() & (IMoveConstants.STATUS_SLP + IMoveConstants.STATUS_FRZ)) > 0;
        if (!player.getAbilities().instabuild && player.getAbilities().mayfly != fly && !noFloat)
        {
            player.getAbilities().mayfly = fly;
            player.onUpdateAbilities();
        }
    }

    public static void updateFlying(final Player player, final IPokemob pokemob)
    {
        if (pokemob == null) return;
        if (pokemob.getPokedexEntry().floats() || pokemob.getPokedexEntry().flys())
        {
            player.fallDistance = 0;
            if (player instanceof ServerPlayer) ((ServerPlayer) player).connection.aboveGroundTickCount = 0;
        }
    }

    public static void updateFloating(final Player player, final IPokemob pokemob)
    {
        if (pokemob == null) return;
        if (!player.isShiftKeyDown() && pokemob.floats() && !player.isFallFlying())
        {
        	player.fallDistance = 0;
            if (player instanceof ServerPlayer) ((ServerPlayer) player).connection.aboveGroundTickCount = 0;
        }
    }

    public static void updateSwimming(final Player player, final IPokemob pokemob)
    {
        if (pokemob == null) return;  
    }
}
