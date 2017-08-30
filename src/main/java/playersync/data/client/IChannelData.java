package playersync.data.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IChannelData {
    String getChannel();

    List<PlayerData> getData();

    class PlayerData {
        public UUID uuid;
        public byte[] data;

        public PlayerData(Map.Entry<UUID, byte[]> data) {
            this(data.getKey(), data.getValue());
        }

        public PlayerData(UUID uuid, byte[] data) {
            this.uuid = uuid;
            this.data = data;
        }
    }
}
