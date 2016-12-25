package playersync.data;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChannelData {

    private final String channel;
    private List<PlayerData> data = Lists.newArrayList();

    public ChannelData(String channel) {
        this.channel = channel;
    }

    public ChannelData addData(Map<UUID, byte[]> map) {
        for (Map.Entry<UUID, byte[]> e : map.entrySet()) {
            addData(e.getKey(), e.getValue());
        }
        return this;
    }

    public ChannelData addData(UUID id, byte[] data) {
        this.data.add(new PlayerData(id, data));
        return this;
    }

    public String getChannel() {
        return channel;
    }

    public List<PlayerData> getPlayerData() {
        return data;
    }

    public static class PlayerData {

        private final UUID id;
        private final byte[] data;

        public PlayerData(UUID id, byte[] data) {

            this.id = id;
            this.data = data;
        }

        public UUID getUniqueId() {
            return id;
        }

        public byte[] getData() {
            return data;
        }
    }
}
