package playersync.bukkit.data.server;

import playersync.bukkit.data.BufferUtils;
import playersync.bukkit.data.ServerData;

import java.io.ByteArrayOutputStream;

public class CHelloData implements ServerData {

    private int version;

    public CHelloData(int version) {
        this.version = version;
    }

    public void write(ByteArrayOutputStream buf) {
        BufferUtils.writeVarInt(buf, version);
    }

}
