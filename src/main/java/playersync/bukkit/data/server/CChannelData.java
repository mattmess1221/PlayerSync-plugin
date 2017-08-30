package playersync.bukkit.data.server;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import playersync.bukkit.data.BufferUtils;
import playersync.bukkit.data.BukkitData;
import playersync.bukkit.data.IClientDataHandler;
import playersync.data.client.IChannelData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CChannelData implements IChannelData, BukkitData<IClientDataHandler> {

    private String channel;
    private List<IChannelData.PlayerData> data;

    public CChannelData(String channel, UUID uuid, byte[] data) {
        this.channel = channel;
        this.data = Collections.singletonList(new IChannelData.PlayerData(uuid, data));
    }

    public CChannelData(String channel, Map<UUID, byte[]> data) {
        this.channel = channel;
        this.data = data.entrySet().stream()
                .map(IChannelData.PlayerData::new)
                .collect(Collectors.toList());
    }

    public void write(ByteBuffer buffer) throws IOException {
        BufferUtils.writeString(buffer, channel);
        BufferUtils.writeVarInt(buffer, data.size());
        for (IChannelData.PlayerData player : data) {
            BufferUtils.writeUUID(buffer, player.uuid);
            BufferUtils.writeVarInt(buffer, player.data.length);
            buffer.put(player.data);
        }
    }

    @Override
    public void read(ByteBuffer buffer) throws IOException {
        this.channel = BufferUtils.readString(buffer);
        int size = BufferUtils.readVarInt(buffer);
        this.data = Lists.newArrayList();
        for (int i = 0; i < size; i++) {
            UUID uuid = BufferUtils.readUUID(buffer);
            byte[] data = new byte[BufferUtils.readVarInt(buffer)];
            buffer.get(data);
            this.data.add(new IChannelData.PlayerData(uuid, data));
        }
    }

    @Override
    public void handle(Player player, IClientDataHandler handler) {

    }

    @Override
    public String getChannel() {
        return this.channel;
    }

    @Override
    public List<PlayerData> getData() {
        return this.data;
    }
}
