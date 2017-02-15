package playersync.bukkit;

import com.google.common.collect.HashMultimap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import playersync.Texts;
import playersync.bukkit.data.client.SClientData;
import playersync.bukkit.data.client.SRegisterData;
import playersync.bukkit.data.server.CChannelData;
import playersync.bukkit.data.server.CHelloData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class BukkitPlayerSyncServer {

    private static final int PROTOCOL = 2;

    private HashMultimap<UUID, String> playerChannels = HashMultimap.create();

    private Map<String, Map<UUID, byte[]>> channels = new HashMap<>();

    private final BukkitData channel;

    public BukkitPlayerSyncServer(BukkitData channel) {
        this.channel = channel;
    }

    public void handleRegister(Player player, SRegisterData data) {
        if (data.getVersion() != PROTOCOL) {
            player.sendMessage(ChatColor.YELLOW + Texts.OUTDATED);
            return;
        }
        UUID uniqueId = player.getUniqueId();
        this.playerChannels.putAll(uniqueId, data.getChannels());

        if (!this.playerChannels.isEmpty()) {
            for (String chan : playerChannels.get(uniqueId)) {
                CChannelData bytes = new CChannelData(chan, getPlayerData(chan));
                this.channel.sendData(player, bytes);
            }
        }
    }

    public void handlePacket(Player player, SClientData buf) {
        UUID uniqueId = player.getUniqueId();
        String channel = buf.getChannel();

        byte[] data = buf.getData();
        getPlayerData(channel).put(uniqueId, data);
        Set<UUID> toRemove = new HashSet<>();
        for (Entry<UUID, String> e : this.playerChannels.entries()) {
            UUID id = e.getKey();
            if (id.equals(uniqueId) || !e.getValue().equals(channel))
                continue;
            CChannelData cd = new CChannelData(channel, uniqueId, data);
            Player player2 = Bukkit.getPlayer(e.getKey());
            if (player2 != null) {
                this.channel.sendData(player2, cd);
            } else {
                getPlayerData(channel).remove(e.getKey());
                toRemove.add(e.getKey());
            }
        }
        for (UUID uuid : toRemove) {
            this.playerChannels.removeAll(uuid);
        }

    }

    public void onChannelRegister(Player player) {
        channel.sendRegistration(player, new CHelloData(PROTOCOL));
    }

    public void removePlayer(Player player) {
        UUID uniqueId = player.getUniqueId();
        this.playerChannels.removeAll(uniqueId);
        for (Map<UUID, byte[]> players : this.channels.values()) {
            players.remove(uniqueId);
        }
    }

    private Map<UUID, byte[]> getPlayerData(String channel) {
        return this.channels.computeIfAbsent(channel, b -> new HashMap<>());
    }

}
