package com.pokecube.pokeplayer.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUseMove {
    private final int moveIndex;

    public PacketUseMove(int moveIndex) {
        this.moveIndex = moveIndex;
    }

    public PacketUseMove(FriendlyByteBuf buf) {
        this.moveIndex = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(moveIndex);
    }

    public boolean handler(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerHandler.handleUseMove(context.get().getSender(), moveIndex);
        });
        return true;
    }
}
