package playersync.bukkit.data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.UUID;

public final class BufferUtils {

    private static final int MAX_STRING_LENGTH = Short.MAX_VALUE >> 2;

    public static String readString(ByteBuffer buffer) throws IOException {
        int i = readVarInt(buffer);

        if (i > MAX_STRING_LENGTH * 4) {
            throw new IOException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + MAX_STRING_LENGTH * 4 + ")");
        }
        if (i < 0) {
            throw new IOException("The received encoded string buffer length is less than zero! Weird string!");
        }
        try {
            byte[] bytes = new byte[i];
            buffer.get(bytes);
            String s = new String(bytes, "utf-8");
            if (s.length() > MAX_STRING_LENGTH) {
                throw new IOException("The received string length is longer than maximum allowed (" + s.length() + " > " + MAX_STRING_LENGTH + ")");
            } else {
                return s;
            }
        } catch (UnsupportedEncodingException e) {
            throw new IOException(e);
        }


    }

    public static void writeString(ByteBuffer buffer, String string) throws IOException {
        try {
            byte[] abyte = string.getBytes("utf-8");
            if (abyte.length > 32767) {
                throw new IOException("String too big (was " + abyte.length + " bytes encoded, max " + 32767 + ")");
            } else {
                writeVarInt(buffer, abyte.length);
                buffer.get(abyte, 0, abyte.length);
            }
        } catch (UnsupportedEncodingException e) {
            throw new IOException(e);
        }

    }

    public static int readVarInt(ByteBuffer buffer) throws IOException {
        int i = 0;
        int j = 0;

        while (true) {
            int b0 = buffer.get();
            i |= (b0 & 127) << j++ * 7;

            if (j > 5) {
                throw new IOException("VarInt too big");
            }

            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    public static void writeVarInt(ByteBuffer buffer, int input) {
        while ((input & -128) != 0) {
            buffer.put((byte) (input & 127 | 128));
            input >>>= 7;
        }

        buffer.put((byte) input);
    }

    public static UUID readUUID(ByteBuffer buffer) {
        long most = buffer.getLong();
        long least = buffer.getLong();
        return new UUID(most, least);
    }

    public static void writeUUID(ByteBuffer buffer, UUID uuid) throws IOException {
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
    }

}
