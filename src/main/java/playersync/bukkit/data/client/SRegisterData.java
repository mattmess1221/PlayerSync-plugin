package playersync.bukkit.data.client;

import playersync.bukkit.data.BufferUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

public class SRegisterData {

    private int version;
    private Set<String> channels = new HashSet<>();
    private Set<String> settings = new HashSet<>();

    public SRegisterData(@Nonnull ByteArrayInputStream buf) throws IOException {
        this.version = BufferUtils.readVarInt(buf);
        this.channels = readStrings(buf);
        // not supported on bukkit, but I'll read it anyway
        this.settings = readStrings(buf);
    }

    private static Set<String> readStrings(ByteArrayInputStream input) throws IOException {
        Set<String> strings = new HashSet<>();
        int size = BufferUtils.readVarInt(input);
        for (int i = 0; i < size; i++) {
            strings.add(BufferUtils.readString(input));
        }
        return strings;
    }

    public int getVersion() {
        return version;
    }

    public Set<String> getChannels() {
        return channels;
    }

}
