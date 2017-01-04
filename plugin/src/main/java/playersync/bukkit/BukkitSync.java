package playersync.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import playersync.Constants;
import playersync.PlayerNotFoundException;
import playersync.PlayerSync;
import playersync.data.ChannelData;
import playersync.data.ClientData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BukkitSync extends PlayerSync {

    private final Plugin plugin;

    public BukkitSync(Plugin plugin) {
        this.plugin = plugin;
    }

    void handlePacket(String channel, Player player, byte[] data) {
        try {
            handlePacket(player.getUniqueId(), decodeData(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void sendMessageToPlayer(UUID uniqueId, ChannelData data) throws PlayerNotFoundException {
        Player pl = Bukkit.getPlayer(uniqueId);
        if (pl == null) {
            throw new PlayerNotFoundException();
        }
        try {
            pl.sendPluginMessage(plugin, CHANNEL, encodeData(data));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void sendAckToPlayer(UUID uniqueId) throws PlayerNotFoundException {
        Player pl = Bukkit.getPlayer(uniqueId);
        if (pl == null) {
            throw new PlayerNotFoundException();
        }

        pl.sendPluginMessage(plugin, CHANNEL, createAckData());
    }

    private byte[] createAckData() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketUtils.writeString(out, Constants.ACKNOWLEDGE);
        return out.toByteArray();
    }

    private byte[] encodeData(ChannelData data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketUtils.writeString(out, data.getChannel());
        List<ChannelData.PlayerData> players = data.getPlayerData();
        PacketUtils.writeVarInt(out, players.size());
        for (ChannelData.PlayerData player : players) {
            PacketUtils.writeUUID(out, player.getUniqueId());
            out.write(player.getData());
        }
        return out.toByteArray();
    }

    private ClientData decodeData(byte[] data) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        String channel = PacketUtils.readString(in, 20);
        if (REGISTER.equals(channel) || UNREGISTER.equals(channel)) {
            return new ClientData(channel, readStrings(in));
        }
        byte[] b = new byte[in.available()];
        in.read(b);
        return new ClientData(channel, b);
    }

    private Set<String> readStrings(ByteArrayInputStream in) throws IOException {
        Set<String> strings = new HashSet<>();
        int size = PacketUtils.readVarInt(in);
        for (int i = 0; i < size; i++) {
            strings.add(PacketUtils.readString(in, 20));
        }
        return strings;
    }
}
