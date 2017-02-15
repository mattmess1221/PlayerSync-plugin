package playersync.sponge.data.client;

import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class CChannelData implements Message {

    private String channel;
    private List<PlayerData> data;

    public CChannelData(String channel, UUID uniqueId, byte[] data) {
        this.channel = channel;
        this.data = Collections.singletonList(new PlayerData(uniqueId, data));
    }
    public CChannelData(String channel, Map<UUID, byte[]> data) {
        this.channel = channel;
        this.data = data.entrySet().stream()
                .map(PlayerData::new)
                .collect(Collectors.toList());
    }


    @Override
    public void writeTo(@Nonnull ChannelBuf buf) {
        buf.writeString(channel);
        buf.writeVarInt(data.size());
        for (PlayerData player : data) {
            buf.writeUniqueId(player.uuid);
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

    private static class PlayerData {
        private UUID uuid;
        private byte[] data;

        private PlayerData(Map.Entry<UUID, byte[]> data) {
            this(data.getKey(), data.getValue());
        }

        private PlayerData(UUID uuid, byte[] data) {
            this.uuid = uuid;
            this.data = data;
        }
    }

}
