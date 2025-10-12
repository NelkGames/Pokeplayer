package com.pokecube.pokeplayer.data;

import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public class DataSyncWrapper {

    private final FriendlyByteBuf buf;

    public DataSyncWrapper(FriendlyByteBuf buf) {
        this.buf = buf;
    }

    /** Writes level and move list into the buffer */
    public DataSyncWrapper writeTransform(int level, List<String> moves) {
        buf.writeInt(level);
        buf.writeInt(moves.size());
        for (String m : moves) buf.writeUtf(m);
        return this;
    }

    /** Reads level and move list from the buffer */
    public TransformData readTransform() {
        int level = buf.readInt();
        int count = buf.readInt();
        String[] moves = new String[count];
        for (int i = 0; i < count; i++) moves[i] = buf.readUtf();
        return new TransformData(level, moves);
    }

    /** Container for deserialized data */
    public static class TransformData {
        public final int level;
        public final String[] moves;

        public TransformData(int level, String[] moves) {
            this.level = level;
            this.moves = moves;
        }
    }
}