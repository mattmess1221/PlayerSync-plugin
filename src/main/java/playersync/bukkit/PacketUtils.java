package playersync.bukkit;

import com.google.common.primitives.Longs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class PacketUtils {

    public static String readString(ByteArrayInputStream buffer, int maxLength) throws IOException {
        int i = readVarInt(buffer);

        if (i > maxLength * 4) {
            throw new RuntimeException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")");
        }
        if (i < 0) {
            throw new RuntimeException("The received encoded string buffer length is less than zero! Weird string!");
        }
        try {
            byte[] bytes = new byte[i];
            buffer.read(bytes);
            String s = new String(bytes, "utf-8");
            if (s.length() > maxLength) {
                throw new RuntimeException("The received string length is longer than maximum allowed (" + s.length() + " > " + maxLength + ")");
            } else {
                return s;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }


    }

    public static void writeString(ByteArrayOutputStream buffer, String string) {
        try {
            byte[] abyte = string.getBytes("utf-8");
            if (abyte.length > 32767) {
                throw new RuntimeException("String too big (was " + abyte.length + " bytes encoded, max " + 32767 + ")");
            } else {
                writeVarInt(buffer, abyte.length);
                buffer.write(abyte, 0, abyte.length);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    public static int readVarInt(ByteArrayInputStream buffer) {
        int i = 0;
        int j = 0;

        while (true) {
            int b0 = buffer.read();
            i |= (b0 & 127) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }

            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    public static void writeVarInt(ByteArrayOutputStream buffer, int input) {
        while ((input & -128) != 0) {
            buffer.write((byte) (input & 127 | 128));
            input >>>= 7;
        }

        buffer.write((byte) input);
    }

    public static void writeUUID(ByteArrayOutputStream buffer, UUID uuid) throws IOException {
        buffer.write(Longs.toByteArray(uuid.getMostSignificantBits()));
        buffer.write(Longs.toByteArray(uuid.getLeastSignificantBits()));
    }

}
