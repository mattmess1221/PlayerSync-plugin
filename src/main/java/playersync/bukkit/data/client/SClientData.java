package playersync.bukkit.data.client;

import playersync.bukkit.data.BufferUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SClientData {

    private String channel;
    private byte[] data;

    public SClientData(ByteArrayInputStream buf) throws IOException {
        this.channel = BufferUtils.readString(buf);
        this.data = new byte[buf.available()];
        buf.read(data);
    }

    public byte[] getData() {
        return data;
    }

    public String getChannel() {
        return channel;
    }
}
