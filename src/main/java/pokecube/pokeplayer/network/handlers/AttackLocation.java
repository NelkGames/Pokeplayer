package pokecube.pokeplayer.network.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.chat.TranslatableComponent;
import pokecube.core.PokecubeCore;
import pokecube.core.ai.brain.BrainUtils;
import pokecube.core.events.pokemob.combat.CommandAttackEvent;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.Move_Base;
import pokecube.core.interfaces.pokemob.ai.CombatStates;
import pokecube.core.interfaces.pokemob.commandhandlers.AttackLocationHandler;
import pokecube.core.moves.MovesUtils;
import pokecube.core.network.pokemobs.PacketCommand.DefaultHandler;
import pokecube.pokeplayer.Pokeplayer;
import thut.api.maths.Vector3;

// Wrapper to ensure player attacks entity as pokeplayer
public class AttackLocation extends DefaultHandler 
{
	Vector3 location;
	
	public AttackLocation() {}
	
	public AttackLocation(Vector3 targetLocation) {
		this.location = targetLocation;
	}

	@Override
	public void handleCommand(final IPokemob pokemob) 
	{
		// Use default handling, which just agros stuff.
		if (!pokemob.getEntity().getPersistentData().getBoolean("is_a_player")) 
		{
			Pokeplayer.LOGGER.info("Lock");
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
				pokemob.executeMove(null, Vector3.vecMult.set(location), 0);
				BrainUtils.setMoveUseTarget(pokemob.getEntity(), this.location);
			}
		}
		else
		{
			AttackLocationHandler defaults = new AttackLocationHandler();
			ByteBuf buffer = Unpooled.buffer();
			this.writeToBuf(buffer);
			defaults.readFromBuf(buffer);
			defaults.handleCommand(pokemob);
		}
	}
	
	@Override
    public void writeToBuf(ByteBuf buf)
    {
        super.writeToBuf(buf);
        location.writeToBuff(buf);
    }

    @Override
    public void readFromBuf(ByteBuf buf)
    {
        super.readFromBuf(buf);
        location = Vector3.readFromBuff(buf);
    }
}