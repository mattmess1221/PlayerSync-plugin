package playersync.bukkit.data.server;

import org.bukkit.entity.Player;
import playersync.bukkit.data.BufferUtils;
import playersync.bukkit.data.BukkitData;
import playersync.bukkit.data.IClientDataHandler;
import playersync.data.client.IHelloData;

import java.io.IOException;
import java.nio.ByteBuffer;

public class CHelloData implements IHelloData, BukkitData<IClientDataHandler> {

    private int version;

    public CHelloData(int version) {
        this.version = version;
    }

    @Override
    public void read(ByteBuffer buffer) throws IOException {
        version = BufferUtils.readVarInt(buffer);
    }

    @Override
    public void write(ByteBuffer buffer) throws IOException {
        BufferUtils.writeVarInt(buffer, version);
    }

    @Override
    public void handle(Player player, IClientDataHandler handler) {

    }

    @Override
    public int getVersion() {
        return version;
    }
}
