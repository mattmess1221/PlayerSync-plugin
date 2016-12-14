package playersync;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class PlayerSync implements Constants {

    private final Plugin plugin;

    private Map<UUID, Set<String>> playerChannels = new HashMap<>();
    private Map<String, Map<UUID, byte[]>> channels = new HashMap<>();

    public PlayerSync(Plugin plugin) {
        this.plugin = plugin;
    }

    public void handlePacket(Player player, byte[] msg) throws IOException {

        ByteArrayInputStream buf = new ByteArrayInputStream(msg);
        String channel = PacketUtils.readString(buf, 20);

        if (REGISTER.equals(channel)) {
            System.out.println(player.getListeningPluginChannels());
            Set<String> channels = getPlayerChannels(player.getUniqueId());
            channels.addAll(getChannelsFromData(buf));
        } else if (UNREGISTER.equals(channel)) {
            getPlayerChannels(player.getUniqueId()).removeAll(getChannelsFromData(buf));
        } else {
            byte[] data = new byte[buf.available()];
            buf.read(data);
            getPlayerData(channel).put(player.getUniqueId(), data);
            Set<UUID> toRemove = new HashSet<>();
            for (Entry<UUID, Set<String>> e : this.playerChannels.entrySet()) {
                if (e.getKey().equals(player.getUniqueId()) || !e.getValue().contains(channel))
                    continue;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PacketUtils.writeString(baos, channel);
                PacketUtils.writeVarInt(baos, 1);
                PacketUtils.writeUUID(baos, player.getUniqueId());
                baos.write(data);

                Player pl = Bukkit.getPlayer(e.getKey());
                if (pl == null) {
                    getPlayerData(channel).remove(e.getKey());
                    toRemove.add(e.getKey());
                } else {
                    pl.sendPluginMessage(plugin, CHANNEL, baos.toByteArray());
                }
            }
            for (UUID uuid : toRemove) {
                this.playerChannels.remove(uuid);
            }
        }
    }

    public void onChannelRegister(Player player) {
        try {
            for (String chan : getPlayerChannels(player.getUniqueId())) {
                byte[] bytes = buildInitialPackets(chan);
                player.sendPluginMessage(plugin, CHANNEL, bytes);
            }
        } catch (IOException ignored) {

        }
    }

    private byte[] buildInitialPackets(String channel) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PacketUtils.writeString(out, channel);
        Map<UUID, byte[]> playerData = getPlayerData(channel);
        PacketUtils.writeVarInt(out, playerData.size());
        for (Entry<UUID, byte[]> e : playerData.entrySet()) {
            PacketUtils.writeUUID(out, e.getKey());
            out.write(e.getValue());
        }

        return out.toByteArray();
    }

    private Set<String> getChannelsFromData(ByteArrayInputStream input) throws IOException {
        Set<String> chans = new HashSet<>();
        int size = PacketUtils.readVarInt(input);
        for (int i = 0; i < size; i++) {
            chans.add(PacketUtils.readString(input, 20));
        }
        return chans;
    }

    private Set<String> getPlayerChannels(UUID uuid) {
        return this.playerChannels.computeIfAbsent(uuid, b -> new HashSet<>());
    }

    public Map<UUID, byte[]> getPlayerData(String channel) {
        return this.channels.computeIfAbsent(channel, b -> new HashMap<>());
    }

    public void removePlayer(UUID uniqueId) {
        for (Map<UUID, byte[]> players : this.channels.values()) {
            players.remove(uniqueId);
        }
    }

}
