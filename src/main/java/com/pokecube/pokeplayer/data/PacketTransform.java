package com.pokecube.pokeplayer.data;

import com.pokecube.pokeplayer.Pokeplayer;
import io.netty.buffer.Unpooled;
import io.netty.channel.pool.SimpleChannelPool;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.List;

public class PacketTransform
{
    public static final ResourceLocation TRANSFORM_ID = ResourceLocation.parse("transform");

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRegisterPayload(final RegisterPayloadHandlersEvent event){
        event.registrar(
                TRANSFORM_ID,
                (buf, listener) -> {
                    DataSyncWrapper.TransformData data = new DataSyncWrapper(buf).readTransform();

                    PokeplayerDataHandler.onTransform(data.level, data.moves);
                },
                (payload) -> payload,
                PacketFlow.CLIENTBOUND
        );
    }

    public static void sendTransform(final ServerPlayer player, final int level, final List<String> moves){
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        new DataSyncWrapper(buf).writeTransform(level, moves);
        Packet<ClientGamePacketListener> pkt = new CustomPacketPayload(TRANSFORM_ID, buf);
        player.connection.send(pkt);
    }
}

