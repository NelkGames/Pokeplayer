package pokecube.pokeplayer.network.handlers;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import pokecube.core.PokecubeCore;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.pokemob.ai.LogicStates;
import pokecube.core.interfaces.pokemob.commandhandlers.StanceHandler;
import pokecube.core.network.pokemobs.PacketCommand.DefaultHandler;
import pokecube.pokeplayer.PokeInfo;
import pokecube.pokeplayer.network.PacketTransform;
import thut.core.common.handlers.PlayerDataHandler;
import thut.core.common.world.mobs.data.PacketDataSync;

public class Stance extends StanceHandler //DefaultHandler
{
    public static final byte BUTTONTOGGLESIT = 2;
    public static final byte SELFINTERACT    = -2;
    public static final byte SYNCUPDATE      = -3;

    boolean state;
    byte    key;

    public Stance() {}

    public Stance(final Boolean state, final Byte key)
    {
        this.state = state;
        this.key = key;
    }

    @Override
    public void handleCommand(final IPokemob pokemob) throws Exception
    {
    	super.handleCommand(pokemob);
        // Start by handling the default stance messages.
        final StanceHandler defaults = new StanceHandler(
        		this.state, this.key);
        defaults.handleCommand(pokemob);

        // Handle pokeplayer specific things.
        if (pokemob.getEntity().getPersistentData().getBoolean("is_a_player"))
        {
            final Entity entity = pokemob.getEntity().level.getEntity(pokemob.getEntity().getId());
            if (entity instanceof Player)
            {
                final Player player = (Player) entity;

                if (this.key == Stance.SELFINTERACT)
                {
                    final EntityInteractSpecific evt = new EntityInteractSpecific(player,  InteractionHand.MAIN_HAND, pokemob
                            .getEntity(), new Vec3(0, 0, 0));

                    // Apply interaction, also do not allow saddle.
                    final ItemStack saddle = pokemob.getInventory().getItem(0);
                    if (!saddle.isEmpty()) pokemob.getInventory().canPlaceItem(0, ItemStack.EMPTY);
                    PokecubeCore.MOVE_BUS.post(evt);
                    if (!saddle.isEmpty()) pokemob.getInventory().canPlaceItem(0, saddle);

                    final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
                    info.save(player);
                }
                else if (this.key == Stance.SYNCUPDATE) {
                	PacketDataSync.sync((ServerPlayer) player, pokemob.dataSync(), player.getId(), true);
                }
                else if (this.key == Stance.BUTTONTOGGLESIT)
                {
                    final PacketTransform packet = new PacketTransform();
                    packet.getTag().putInt("__entityid__", player.getId());
                    packet.getTag().putBoolean("U", true);
                    packet.getTag().putBoolean("S", pokemob.getLogicState(LogicStates.SITTING));
                    PacketTransform.ASSEMBLY.sendTo(packet, (ServerPlayer) player);
                }
            }
        }
    }
    
    @Override
    public void writeToBuf(ByteBuf buf)
    {
        super.writeToBuf(buf);
        buf.writeBoolean(state);
        buf.writeByte(key);
    }

    @Override
    public void readFromBuf(ByteBuf buf)
    {
        super.readFromBuf(buf);
        state = buf.readBoolean();
        key = buf.readByte();
    }
}