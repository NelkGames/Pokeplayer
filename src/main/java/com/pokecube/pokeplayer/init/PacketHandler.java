package com.pokecube.pokeplayer.init;

import com.pokecube.pokeplayer.data.PacketUseMove;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("","main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register(){
        int id = 0;
        INSTANCE.registerMessage(id++, PacketUseMove.class, PacketUseMove::toBytes,
                PacketUseMove::new, PacketUseMove::handler);
    }
}
