package playersync.sponge;

import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;
import playersync.data.ClientData;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

import static playersync.Constants.REGISTER;
import static playersync.Constants.UNREGISTER;

public class ClientDataMessage extends ClientData implements Message {

    @Override
    public void readFrom(@Nonnull ChannelBuf buf) {
        String channel = buf.readString();
        setChannel(channel);
        if (REGISTER.equals(channel) || UNREGISTER.equals(channel)) {
            setRegistrations(readStrings(buf));
        } else {
            setData(buf.readByteArray());
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

}
