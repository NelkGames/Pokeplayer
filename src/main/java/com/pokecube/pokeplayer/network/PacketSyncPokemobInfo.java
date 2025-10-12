package com.pokecube.pokeplayer.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.network.PacketDistributor;
import pokecube.api.entity.pokemob.IPokemob;
import pokecube.api.entity.pokemob.PokemobCaps;
import pokecube.core.PokecubeCore;
import pokecube.core.network.PokecubePacketHandler;

import javax.naming.Context;
import java.util.function.Supplier;

public class PacketSyncPokemobInfo
{
/*    private final int level;
    private final String[] moves;

    public PacketSyncPokemobInfo(final int level, final String[] moves)
    {
        this.level = level;
        this.moves = moves;
    }

    public static void encode(final PacketSyncPokemobInfo msg, final FriendlyByteBuf buf)
    {
        buf.writeInt(msg.level);
        buf.writeInt(msg.moves.length);
        for (final String move : msg.moves) buf.writeUtf(move);
    }

    public static PacketSyncPokemobInfo decode(final FriendlyByteBuf buf)
    {
        final int level = buf.readInt();
        final int count = buf.readInt();
        final String[] moves = new String[count];
        for (int i = 0; i < count; i++) moves[i] = buf.readUtf();
        return new PacketSyncPokemobInfo(level, moves);
    }

    public static void handle(final PacketSyncPokemobInfo msg, final Supplier<NetworkE> ctx)
    {
        final Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide() != Dist.CLIENT) return;
            final Player player = Minecraft.getInstance().player;
            final IPokemob pokemob = PokemobCaps.getPokemobFor(player);
            if (pokemob == null) return;
            pokemob.levelUp(msg.level);
            for (int i = 0; i < msg.moves.length && i < 4; i++) pokemob.setMove(i, msg.moves[i]);
        });
        context.setPacketHandled(true);
    }

    public static void sendToClient(ServerPlayer player){
        final IPokemob pokemob = PokemobCaps.getPokemobFor(player);
        if(pokemob == null) return;
        final String[] moves = new String[4];
        for (int i = 0; i < 4; i++) moves[i] = pokemob.getMove(i);
        final PacketSyncPokemobInfo packet = new PacketSyncPokemobInfo(pokemob.getLevel(), moves);
        PokecubePacketHandler.CHANNEL.sendTo(PacketDistributor.PLAYER.with(() -> player), packet);
    }*/
}
