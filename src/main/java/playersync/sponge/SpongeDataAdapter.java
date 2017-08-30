package playersync.sponge;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.network.ChannelBuf;
import playersync.adapters.Adapter;
import playersync.adapters.DataAdapter;
import playersync.data.Data;
import playersync.sponge.data.client.CChannelData;
import playersync.sponge.data.client.CHelloData;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Matthew on 6/25/2017.
 */
public class SpongeDataAdapter implements DataAdapter<ChannelBuf> {
    @Override
    public TypeToken<? extends Adapter<Data<ChannelBuf>>> getType() {
        return TypeToken.of(SpongeDataAdapter.class);
    }

    @Override
    public Data<ChannelBuf> newChannelData(String channel, UUID uniqueId, byte[] data) {
        return new CChannelData(channel, uniqueId, data);
    }

    @Override
    public Data<ChannelBuf> newChannelData(String channel, Map<UUID, byte[]> data) {
        return new CChannelData(channel, data);
    }

    @Override
    public Data<ChannelBuf> newHelloData(int version) {
        return new CHelloData(version);
    }
}
