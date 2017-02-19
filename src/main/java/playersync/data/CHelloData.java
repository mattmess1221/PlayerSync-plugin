package playersync.data;

import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;

import javax.annotation.Nonnull;

public class CHelloData implements Message {

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
}
