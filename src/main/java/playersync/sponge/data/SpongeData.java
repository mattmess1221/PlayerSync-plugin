package playersync.sponge.data;

import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;
import playersync.data.Data;

/**
 * Created by Matthew on 6/25/2017.
 */
public interface SpongeData extends Data<ChannelBuf>, Message {

    default void read(ChannelBuf buffer) {
        this.readFrom(buffer);
    }

    default void write(ChannelBuf buffer) {
        this.writeTo(buffer);
    }
}
