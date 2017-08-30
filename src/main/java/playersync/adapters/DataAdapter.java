package playersync.adapters;

import com.google.common.reflect.TypeToken;
import playersync.data.Data;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Matthew on 6/25/2017.
 */
public interface DataAdapter<Buf> extends Adapter<Data<Buf>> {

    Data<Buf> newChannelData(String channel, UUID uniqueId, byte[] data);
    Data<Buf> newChannelData(String channel, Map<UUID, byte[]> data);
    Data<Buf> newHelloData(int version);

    static <Buf> TypeToken<DataAdapter<Buf>> token() {
        return Adapters.getType(DataAdapter.class);
    }
}
