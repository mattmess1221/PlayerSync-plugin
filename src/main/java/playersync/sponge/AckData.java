package playersync.sponge;

import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;
import playersync.Constants;

import javax.annotation.Nonnull;

public class AckData implements Message {


    @Override
    public void readFrom(@Nonnull ChannelBuf buf) {
    }

    @Override
    public void writeTo(@Nonnull ChannelBuf buf) {
        buf.writeString(Constants.ACKNOWLEDGE);
    }
}
