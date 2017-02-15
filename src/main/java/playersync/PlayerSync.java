package playersync;

import static playersync.PlayerSyncPlugin.REGISTER;
import static playersync.PlayerSyncPlugin.UNREGISTER;

import com.google.common.collect.HashMultimap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import playersync.data.ChannelData;
import playersync.data.ChannelDataBase;
import playersync.data.ClientData;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class PlayerSync {


    private HashMultimap<UUID, String> playerChannels = HashMultimap.create();
    private Map<String, Map<UUID, byte[]>> channels = new HashMap<>();

    private final ChannelBinding.IndexedMessageChannel channel;

    public PlayerSync(ChannelBinding.IndexedMessageChannel channel) {
        this.channel = channel;
    }

    void handlePacket(Player player, ClientData buf) throws IOException {
        UUID uniqueId = player.getUniqueId();
        String channel = buf.getChannel();

        if (REGISTER.equals(channel)) {
            this.playerChannels.putAll(uniqueId, buf.getRegistrations());
            for (String chan : playerChannels.get(uniqueId)) {
                ChannelData bytes = new ChannelData(chan, getPlayerData(chan));
                this.channel.sendTo(player, bytes);
            }
        } else if (UNREGISTER.equals(channel)) {
            this.playerChannels.get(uniqueId).removeAll(buf.getRegistrations());
        } else {
            byte[] data = buf.getData();
            getPlayerData(channel).put(uniqueId, data);
            Set<UUID> toRemove = new HashSet<>();
            for (Entry<UUID, String> e : this.playerChannels.entries()) {
                UUID id = e.getKey();
                if (id.equals(uniqueId) || !e.getValue().equals(channel))
                    continue;
                ChannelData cd = new ChannelData(channel, uniqueId, data);
                try {
                    Player player2 = getPlayerFromUniqueId(e.getKey());
                    this.channel.sendTo(player2, cd);
                } catch (PlayerNotFoundException ex) {
                    getPlayerData(channel).remove(e.getKey());
                    toRemove.add(e.getKey());
                }
            }
            for (UUID uuid : toRemove) {
                this.playerChannels.removeAll(uuid);
            }
        }

    }

    void onChannelRegister(UUID uniqueId) {
        try {
            Player player = getPlayerFromUniqueId(uniqueId);
            channel.sendTo(player, new ChannelDataBase(PlayerSyncPlugin.ACKNOWLEDGE));
        } catch (PlayerNotFoundException ignored) {
        }
    }


    void removePlayer(UUID uniqueId) {
        this.playerChannels.removeAll(uniqueId);
        for (Map<UUID, byte[]> players : this.channels.values()) {
            players.remove(uniqueId);
        }
    }

    private Map<UUID, byte[]> getPlayerData(String channel) {
        return this.channels.computeIfAbsent(channel, b -> new HashMap<>());
    }

    private static Player getPlayerFromUniqueId(UUID uniqueId) throws PlayerNotFoundException {
        return Sponge.getServer().getPlayer(uniqueId).orElseThrow(PlayerNotFoundException::new);
    }

}
