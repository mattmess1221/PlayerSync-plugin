package playersync.sponge.data.client;

import org.spongepowered.api.network.ChannelBuf;
import playersync.data.client.IHelloData;
import playersync.sponge.data.SpongeData;

import javax.annotation.Nonnull;

public class CHelloData implements SpongeData, IHelloData {

    private int version;

    public CHelloData(int version) {
        this.version = version;
    }

    @Override
    public void readFrom(@Nonnull ChannelBuf buf) {
        version = buf.readVarInt();
    }

    @Override
    public void writeTo(@Nonnull ChannelBuf buf) {
        buf.writeVarInt(version);
    }

    @Override
    public int getVersion() {
        return version;
    }
}
