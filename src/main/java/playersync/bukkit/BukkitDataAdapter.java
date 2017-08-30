package playersync.bukkit;

import com.google.common.reflect.TypeToken;
import playersync.adapters.Adapter;
import playersync.adapters.DataAdapter;
import playersync.bukkit.data.server.CChannelData;
import playersync.bukkit.data.server.CHelloData;
import playersync.data.Data;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

public class BukkitDataAdapter implements DataAdapter<ByteBuffer>  {
    @Override
    public TypeToken<? extends Adapter<Data<ByteBuffer>>> getType() {
        return TypeToken.of(BukkitDataAdapter.class);
    }

    @Override
    public Data<ByteBuffer> newChannelData(String channel, UUID uniqueId, byte[] data) {
        return new CChannelData(channel, uniqueId, data);
    }

    @Override
    public Data<ByteBuffer> newChannelData(String channel, Map<UUID, byte[]> data) {
        return new CChannelData(channel, data);
    }

    @Override
    public Data<ByteBuffer> newHelloData(int version) {
        return new CHelloData(version);
    }
}
