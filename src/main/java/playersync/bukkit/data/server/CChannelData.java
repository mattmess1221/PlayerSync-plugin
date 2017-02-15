package playersync.bukkit.data.server;

import playersync.bukkit.data.BufferUtils;
import playersync.bukkit.data.ServerData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CChannelData implements ServerData {

    private String channel;
    private List<PlayerData> data;

    public CChannelData(String channel, UUID uuid, byte[] data) {
        this.channel = channel;
        this.data = Collections.singletonList(new PlayerData(uuid, data));
    }

    public CChannelData(String channel, Map<UUID, byte[]> data) {
        this.channel = channel;
        this.data = data.entrySet().stream()
                .map(PlayerData::new)
                .collect(Collectors.toList());
    }

    public void write(ByteArrayOutputStream buf) throws IOException {
        BufferUtils.writeString(buf, channel);
        BufferUtils.writeVarInt(buf, data.size());
        for (PlayerData player : data) {
            BufferUtils.writeUUID(buf, player.uuid);
            buf.write(player.data);
        }
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
