package pokecube.pokeplayer.network.handlers;

import net.minecraft.network.chat.TranslatableComponent;
import pokecube.core.PokecubeCore;
import pokecube.core.events.pokemob.combat.CommandAttackEvent;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.Move_Base;
import pokecube.core.interfaces.pokemob.ai.CombatStates;
import pokecube.core.interfaces.pokemob.commandhandlers.AttackLocationHandler;
import pokecube.core.moves.MovesUtils;
import pokecube.pokeplayer.Pokeplayer;
import thut.api.maths.Vector3;

// Wrapper to ensure player attacks entity as pokeplayer
public class AttackLocation extends AttackLocationHandler {
	@Override
	public void handleCommand(final IPokemob pokemob) 
	{
		// Use default handling, which just agros stuff.
		if (!pokemob.getEntity().getPersistentData().getBoolean("is_a_player")) {
			handleCommand(pokemob);
			return;
		}
		final int currentMove = pokemob.getMoveIndex();
		final CommandAttackEvent evt = new CommandAttackEvent(pokemob.getEntity(), null);
		PokecubeCore.POKEMOB_BUS.post(evt);

		if (!evt.isCanceled() && currentMove != 5 && MovesUtils.canUseMove(pokemob)) 
		{
			pokemob.setCombatState(CombatStates.EXECUTINGMOVE, false);
			pokemob.setCombatState(CombatStates.NOITEMUSE, false);
			final Move_Base move = MovesUtils.getMoveFromName(pokemob.getMoves()[currentMove]);
			// Send move use message first.
			TranslatableComponent mess = new TranslatableComponent("pokemob.action.usemove", pokemob.getDisplayName(),
					new TranslatableComponent(MovesUtils.getUnlocalizedMove(move.getName())));
			if (this.fromOwner()) pokemob.displayMessageToOwner(mess);

			// If too hungry, send message about that.
			if (pokemob.getHungerTime() > 0) {
				mess = new TranslatableComponent("pokemob.action.hungry", pokemob.getDisplayName());
				if (this.fromOwner()) pokemob.displayMessageToOwner(mess);
				return;
			}
			// Otherwise set the location for execution of move.
			Pokeplayer.LOGGER.info("Attack");
			pokemob.executeMove(null, this.location, 0);
		}
	}
}