package pokecube.pokeplayer.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import pokecube.core.PokecubeCore;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.pokemob.IHasCommands.Command;
import pokecube.core.interfaces.pokemob.ai.LogicStates;
import pokecube.core.network.pokemobs.PacketCommand;
import pokecube.pokeplayer.PokeInfo;
import pokecube.pokeplayer.network.handlers.Stance;
import thut.core.common.handlers.PlayerDataHandler;
import thut.core.common.network.NBTPacket;
import thut.core.common.network.PacketAssembly;

public class PacketTransform extends NBTPacket
{
  
  public int id;

  public static final PacketAssembly<PacketTransform> ASSEMBLY = PacketAssembly.registerAssembler(
          PacketTransform.class, PacketTransform::new, PokecubeCore.packets);

  public static void sendPacket(final Player toSend, final ServerPlayer sendTo)
  {
      PacketTransform.ASSEMBLY.sendTo(PacketTransform.getPacket(toSend), sendTo);
  }

  public static PacketTransform getPacket(final Player toSend)
  {
      final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(toSend).getData(PokeInfo.class);
      final PacketTransform message = new PacketTransform();
      info.writeToNBT(message.getTag());
      message.getTag().putInt("__entityid__", toSend.getId());
      return message;
  }
  
  public PacketTransform()
  {
      super();
  }

  public PacketTransform(final CompoundTag tag)
  {
      super();
      this.tag = tag;
  }

  public PacketTransform(final FriendlyByteBuf buffer)
  {
      super(buffer);
  }

//  @Override
//  protected void onCompleteClient()
//  {
//      final Level level = PokecubeCore.proxy.getWorld();
//      this.id = this.getTag().getInt("__entityid__");
//      final Entity e = PokecubeCore.getEntityProvider().getEntity(level, this.id, false);
//      if (this.getTag().contains("U"))
//      {
//          final Player player = PokecubeCore.proxy.getPlayer();
//          if (this.getTag().contains("H"))
//          {
//              final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
//              final IPokemob pokemob = info.getPokemob(level);
//              if (pokemob == null) return;
//              final float health = this.getTag().getFloat("M");
//              if (pokemob.getEntity() == null) return;
//              
//              pokemob.setHealth(health);
//              player.setHealth(health);
//          }
//          else if (this.getTag().contains("S"))
//          {
//              final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
//              final IPokemob pokemob = info.getPokemob(level);
//              if (pokemob == null) return;
//              pokemob.setLogicState(LogicStates.SITTING, this.getTag().getBoolean("S"));
//              PacketCommand.sendCommand(pokemob, Command.STANCE, new Stance(true, (byte) 2));
//          }
//          return;
//      }
//      if (e instanceof Player)
//      {
//          final Player player = (Player) e;
//          final PokeInfo info = PlayerDataHandler.getInstance().getPlayerData(player).getData(PokeInfo.class);
//          info.clear();
//          info.readFromNBT(this.getTag());
//          final IPokemob pokemob = info.getPokemob(level);
//          if (pokemob != null)
//          {
//              info.set(pokemob, player);
//              // Callback to let server know to update us.
//              pokemob.getEntity().setId(player.getId());
//              PacketCommand.sendCommand(pokemob, Command.STANCE, new Stance(true, (byte) -3));
//          }
//          else info.resetPlayer(player);
//      }
//    }
}