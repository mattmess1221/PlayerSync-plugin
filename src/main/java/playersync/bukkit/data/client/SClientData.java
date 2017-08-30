package playersync.bukkit.data.client;

import org.bukkit.entity.Player;
import playersync.bukkit.data.BufferUtils;
import playersync.bukkit.data.BukkitData;
import playersync.bukkit.data.IServerDataHandler;
import playersync.data.server.IClientData;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SClientData implements IClientData, BukkitData<IServerDataHandler> {

    private String channel;
    private byte[] data;

    @Override
    public void read(ByteBuffer buffer) throws IOException {

        this.channel = BufferUtils.readString(buffer);
        this.data = new byte[buffer.remaining()];
        buffer.get(data);
    }

    @Override
    public void write(ByteBuffer buffer) throws IOException {
        BufferUtils.writeString(buffer, this.channel);
        buffer.put(data);
    }

    @Override
    public void handle(Player player, IServerDataHandler handler) {
        handler.handleClientData(player, this);
    }

    public byte[] getData() {
        return data;
    }

    public String getChannel() {
        return channel;
    }

}
