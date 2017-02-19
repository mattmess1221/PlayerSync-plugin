package playersync.data;

import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;

import javax.annotation.Nonnull;

public class SClientData implements Message {

    private String channel;
    private byte[] data;

    @Override
    public void readFrom(@Nonnull ChannelBuf buf) {
        this.channel = buf.readString();
        this.data = buf.readByteArray();
    }

    @Override
    public void writeTo(@Nonnull ChannelBuf buf) {
        buf.writeString(channel);
        buf.writeByteArray(data);
    }

    public byte[] getData() {
        return data;
    }

    public String getChannel() {
        return channel;
    }
}
