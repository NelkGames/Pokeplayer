package pokecube.pokeplayer.network.handlers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import pokecube.core.PokecubeCore;
import pokecube.core.ai.brain.BrainUtils;
import pokecube.core.events.pokemob.combat.CommandAttackEvent;
import pokecube.core.interfaces.IMoveConstants;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.Move_Base;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.interfaces.pokemob.ai.CombatStates;
import pokecube.core.interfaces.pokemob.commandhandlers.AttackEntityHandler;
import pokecube.core.moves.MovesUtils;
import pokecube.pokeplayer.Pokeplayer;
import thut.api.maths.Vector3;

// Wrapper to ensure player attacks entity as pokeplayer
public class AttackEntity extends AttackEntityHandler 
{	
    @Override
    public void handleCommand(final IPokemob pokemob)
    {
        // Use default handling, which just agros stuff.
        if (!pokemob.getEntity().getPersistentData().getBoolean("is_a_player"))
        {
        	PokecubeCore.LOGGER.debug("Talvez");
            super.handleCommand(pokemob);
            return;
        }

        // Actually execute the move if needed.
        final Level level = pokemob.getEntity().level;
        final Entity target = PokecubeCore.getEntityProvider().getEntity(level, this.targetId, true);
        final Entity real = PokecubeCore.getEntityProvider().getEntity(level, this.targetId, false);
        if (target == null || !(target instanceof LivingEntity)) return;
        final int currentMove = pokemob.getMoveIndex();
        final CommandAttackEvent event = new CommandAttackEvent(pokemob.getEntity(), target);
        MinecraftForge.EVENT_BUS.post(event);
        PokecubeCore.LOGGER.debug("Event: " + event.isCanceled() + " Move: " + currentMove + " Poke: " + MovesUtils.canUseMove(pokemob));
        if (currentMove != 5)
        {
            final Move_Base move = MovesUtils.getMoveFromName(pokemob.getMoves()[currentMove]);
            pokemob.setCombatState(CombatStates.EXECUTINGMOVE, false);
            pokemob.setCombatState(CombatStates.NOITEMUSE, false);
            if (move.isSelfMove()) {
            	pokemob.executeMove(pokemob.getEntity(), null, 0);
            }
            else
            {
                pokemob.getEntity().setTarget((LivingEntity) target);
                if (target instanceof Mob) {
                	BrainUtils.initiateCombat((Mob) target, (LivingEntity) real);
                }
                
                final IPokemob targ = CapabilityPokemob.getPokemobFor(target);
                if (targ != null) {
                	targ.setCombatState(CombatStates.ANGRY, true);
                }
                // Checks if within range
                final float dist = target.distanceTo(pokemob.getEntity());
                double range = (move.getAttackCategory() & IMoveConstants.CATEGORY_DISTANCE) > 0 ? PokecubeCore
                        .getConfig().rangedAttackDistance : PokecubeCore.getConfig().contactAttackDistance;
                range = Math.max(pokemob.getMobSizes().x, range);
                range = Math.max(1, range);
                if (dist < range) {
                	Pokeplayer.LOGGER.info("Attack-Entity");
                	pokemob.executeMove(target, Vector3.empty.set(target), dist);
                }
            }
        }
    }
}