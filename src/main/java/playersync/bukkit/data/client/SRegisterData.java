package playersync.bukkit.data.client;

import org.bukkit.entity.Player;
import playersync.bukkit.data.BufferUtils;
import playersync.bukkit.data.BukkitData;
import playersync.bukkit.data.IServerDataHandler;
import playersync.data.server.IRegisterData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class SRegisterData implements IRegisterData, BukkitData<IServerDataHandler> {

    private int version;
    private List<String> channels = new ArrayList<>();

    @Override
    public void read(ByteBuffer buf) throws IOException {
        this.version = BufferUtils.readVarInt(buf);
        this.channels = readStrings(buf);
    }

    @Override
    public void write(ByteBuffer byteBuffer) throws IOException {
        BufferUtils.writeVarInt(byteBuffer, this.version);
        writeStrings(byteBuffer, this.channels);
    }

    private static void writeStrings(ByteBuffer buffer, List<String> strings) throws IOException {
        BufferUtils.writeVarInt(buffer, strings.size());
        for (String s : strings) {
            BufferUtils.writeString(buffer, s);
        }
    }

    private static List<String> readStrings(ByteBuffer input) throws IOException {
        List<String> strings = new ArrayList<>();
        int size = BufferUtils.readVarInt(input);
        for (int i = 0; i < size; i++) {
            strings.add(BufferUtils.readString(input));
        }
        return strings;
    }

    @Override
    public void handle(Player player, IServerDataHandler handler) {
        handler.handleRegisterData(player, this);
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public List<String> getChannels() {
        return channels;
    }

}
