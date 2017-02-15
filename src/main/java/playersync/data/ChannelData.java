package playersync.data;

import com.google.common.collect.Lists;
import org.spongepowered.api.network.ChannelBuf;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;

public class ChannelData extends ChannelDataBase {

    private List<PlayerData> data = Lists.newArrayList();

    public ChannelData(String channel, UUID uuid, byte[] data) {
        super(channel);
        addData(uuid, data);
    }

    public ChannelData(String channel, Map<UUID, byte[]> data) {
        super(channel);
        addData(data);
    }

    private void addData(Map<UUID, byte[]> map) {
        for (Map.Entry<UUID, byte[]> e : map.entrySet()) {
            addData(e.getKey(), e.getValue());
        }
    }

    private void addData(UUID id, byte[] data) {
        this.data.add(new PlayerData(id, data));
    }

    @Override
    public void writeTo(@Nonnull ChannelBuf buf) {
        buf.writeVarInt(data.size());
        for (PlayerData player : data) {
            buf.writeUniqueId(player.id);
            buf.writeBytes(player.data);
        }
    }

    @Override
    public void readFrom(@Nonnull ChannelBuf buf) {
    }

    private static class PlayerData {

        private final UUID id;
        private final byte[] data;

        private PlayerData(UUID id, byte[] data) {

            this.id = id;
            this.data = data;
        }

    }
}
