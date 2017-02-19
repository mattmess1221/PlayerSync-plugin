package playersync.data;

import com.google.common.collect.Lists;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;

public class CChannelData implements Message {

    private String channel;
    private List<PlayerData> data = Lists.newArrayList();

    public CChannelData() {
    }

    public CChannelData(String channel, UUID uuid, byte[] data) {
        this.channel = channel;
        this.data.add(new PlayerData(uuid, data));
    }

    public CChannelData(String channel, Map<UUID, byte[]> data) {
        this.channel = channel;
        for (Map.Entry<UUID, byte[]> e : data.entrySet()) {
            this.data.add(new PlayerData(e.getKey(), e.getValue()));
        }
    }

    @Override
    public void writeTo(@Nonnull ChannelBuf buf) {
        buf.writeString(channel);
        buf.writeVarInt(data.size());
        for (PlayerData player : data) {
            buf.writeUniqueId(player.id);
            buf.writeByteArray(player.data);
        }
    }

    @Override
    public void readFrom(@Nonnull ChannelBuf buf) {
        this.channel = buf.readString();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            UUID uuid = buf.readUniqueId();
            byte[] bytes = buf.readByteArray();
            data.add(new PlayerData(uuid, bytes));
        }
    }

    public String getChannel() {
        return channel;
    }

    public List<PlayerData> getData() {
        return data;
    }

    public static class PlayerData {

        private final UUID id;
        private final byte[] data;

        private PlayerData(UUID id, byte[] data) {

            this.id = id;
            this.data = data;
        }

    }
}
