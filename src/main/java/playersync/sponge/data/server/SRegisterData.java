package playersync.sponge.data.server;

import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class SRegisterData implements Message {

    private int version;
    private List<String> channels = new ArrayList<>();
    private List<String> settings = new ArrayList<>();

    @Override
    public void readFrom(@Nonnull ChannelBuf buf) {
        this.version = buf.readVarInt();
        this.channels = readStrings(buf);
        this.settings = readStrings(buf);
    }

    @Override
    public void writeTo(@Nonnull ChannelBuf buf) {
         buf.writeVarInt(this.version);
         writeStrings(buf, this.channels);
         writeStrings(buf, this.settings);
    }

    private static List<String> readStrings(ChannelBuf input) {
        List<String> strings = new ArrayList<>();
        int size = input.readVarInt();
        for (int i = 0; i < size; i++) {
            strings.add(input.readString());
        }
        return strings;
    }

    private static void writeStrings(ChannelBuf input, List<String> strings) {
        input.writeVarInt(strings.size());
        for (String s : strings) {
            input.writeString(s);
        }
    }

    public int getVersion() {
        return version;
    }

    public List<String> getChannels() {
        return channels;
    }

    public List<String> getSettings() {
        return settings;
    }
}
