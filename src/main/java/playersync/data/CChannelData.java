package playersync.data;

import com.google.common.collect.Lists;
import org.spongepowered.api.network.ChannelBuf;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;

public class CChannelData extends ChannelDataBase {

    private List<PlayerData> data = Lists.newArrayList();

    public CChannelData(String channel, UUID uuid, byte[] data) {
        super(channel);
        this.data.add(new PlayerData(uuid, data));
    }

    public CChannelData(String channel, Map<UUID, byte[]> data) {
        super(channel);
        for (Map.Entry<UUID, byte[]> e : data.entrySet()) {
            this.data.add(new PlayerData(e.getKey(), e.getValue()));
        }
    }

    @Override
    public void write(@Nonnull ChannelBuf buf) {
        buf.writeVarInt(data.size());
        for (PlayerData player : data) {
            buf.writeUniqueId(player.id);
            buf.writeBytes(player.data);
        }
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
