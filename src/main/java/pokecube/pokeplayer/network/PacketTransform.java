package pokecube.pokeplayer.network;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import pokecube.core.PokecubeCore;
import pokecube.core.handlers.PokecubePlayerDataHandler;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.PokecubeMod;
import pokecube.core.interfaces.pokemob.ai.LogicStates;
import pokecube.pokeplayer.PokeInfo;

public class PacketTransform implements IMessage, IMessageHandler<PacketTransform, IMessage>
{
    public NBTTagCompound data = new NBTTagCompound();
    public int            id;

    public static void sendPacket(EntityPlayer toSend, EntityPlayerMP sendTo)
    {
        PokecubeMod.packetPipeline.sendTo(getPacket(toSend), sendTo);
    }

    public static PacketTransform getPacket(EntityPlayer toSend)
    {
        PokeInfo info = PokecubePlayerDataHandler.getInstance().getPlayerData(toSend).getData(PokeInfo.class);
        PacketTransform message = new PacketTransform();
        info.writeToNBT(message.data);
        message.id = toSend.getEntityId();
        return message;
    }

    public PacketTransform()
    {
    }

    @Override
    public IMessage onMessage(final PacketTransform message, final MessageContext ctx)
    {
        PokecubeCore.proxy.getMainThreadListener().addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
                apply(message, ctx);
            }
        });
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        try
        {
            data = new PacketBuffer(buf).readCompoundTag();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        new PacketBuffer(buf).writeCompoundTag(data);
    }

    static void apply(PacketTransform message, MessageContext ctx)
    {
        World world = PokecubeCore.proxy.getWorld();
        Entity e = PokecubeMod.core.getEntityProvider().getEntity(world, message.id, false);
        if (message.data.hasKey("U"))
        {
            EntityPlayer player = PokecubeCore.proxy.getPlayer((String) null);
            if (message.data.hasKey("H"))
            {
                PokeInfo info = PokecubePlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
                IPokemob pokemob = info.getPokemob(world);
                if (pokemob == null) { return; }
                float health = message.data.getFloat("H");
                if (pokemob.getEntity() == null) return;
                pokemob.setHealth(health);
                player.setHealth(health);
            }
            else if (message.data.hasKey("S"))
            {
                PokeInfo info = PokecubePlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
                IPokemob pokemob = info.getPokemob(world);
                if (pokemob == null) { return; }
                pokemob.setLogicState(LogicStates.SITTING, message.data.getBoolean("S"));
            }
            return;
        }
        if (e instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) e;
            PokeInfo info = PokecubePlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
            info.clear();
            info.readFromNBT(message.data);
            IPokemob pokemob = info.getPokemob(world);
            if (pokemob != null)
            {
                info.set(pokemob, player);
            }
            else
            {
                info.resetPlayer(player);
            }
        }
    }

}
