package playersync.sponge.data.client;

import org.spongepowered.api.network.ChannelBuf;
import playersync.data.client.IChannelData;
import playersync.sponge.data.SpongeData;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class CChannelData implements SpongeData, IChannelData {

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

    @Override
    public String getChannel() {
        return channel;
    }

    @Override
    public List<PlayerData> getData() {
        return data;
    }


}
