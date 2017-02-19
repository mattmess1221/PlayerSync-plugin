package playersync.data;

import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

public class SRegisterData implements Message {

    private int version;
    private Set<String> channels = new HashSet<>();
    private Set<String> settings = new HashSet<>();

    @Override
    public void readFrom(@Nonnull ChannelBuf buf) {
        this.version = buf.readVarInt();
        this.channels = readStrings(buf);
        this.settings = readStrings(buf);
    }

    @Override
    public void writeTo(@Nonnull ChannelBuf buf) {
        // buf.writeVarInt(this.version);
        // TODO when sponge client is ever done
    }

    private static Set<String> readStrings(ChannelBuf input) {
        Set<String> strings = new HashSet<>();
        int size = input.readVarInt();
        for (int i = 0; i < size; i++) {
            strings.add(input.readString());
        }
        return strings;
    }

    public int getVersion() {
        return version;
    }

    public Set<String> getChannels() {
        return channels;
    }

    public Set<String> getSettings() {
        return settings;
    }
}
