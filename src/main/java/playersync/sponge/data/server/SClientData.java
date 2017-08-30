package playersync.sponge.data.server;

import org.spongepowered.api.network.ChannelBuf;
import playersync.data.server.IClientData;
import playersync.sponge.data.SpongeData;

import javax.annotation.Nonnull;

public class SClientData implements SpongeData, IClientData {

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
        buf.writeBytes(data);
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public String getChannel() {
        return channel;
    }
}
