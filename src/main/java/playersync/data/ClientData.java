package playersync.data;

import org.spongepowered.api.network.ChannelBuf;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

public class ClientData extends ChannelDataBase {

    private byte[] data;
    private Set<String> registrations;

    @Override
    public void readFrom(@Nonnull ChannelBuf buf) {
        if (isRegistration()) {
            this.registrations = readStrings(buf);
        } else {
            this.data = buf.readByteArray();
        }
    }

    private Set<String> readStrings(ChannelBuf input) {
        Set<String> strings = new HashSet<>();
        int size = input.readVarInt();
        for (int i = 0; i < size; i++) {
            strings.add(input.readString());
        }
        return strings;
    }

    @Override
    public void writeTo(@Nonnull ChannelBuf buf) {
    }

    public byte[] getData() {
        return data;
    }

    public Set<String> getRegistrations() {
        return registrations;
    }
}
