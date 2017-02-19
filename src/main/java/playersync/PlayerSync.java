package playersync;

import com.google.common.collect.HashMultimap;
import javafx.util.Pair;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.entity.living.player.Player;
import playersync.data.CAwkData;
import playersync.data.CChannelData;
import playersync.data.CSettingsData;
import playersync.data.SClientData;
import playersync.data.SRegisterData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerSync {

    private HashMultimap<UUID, String> playerChannels = HashMultimap.create();
    private HashMultimap<UUID, String> playerSettings = HashMultimap.create();

    private Map<String, Map<UUID, byte[]>> channels = new HashMap<>();

    private Map<String, Map<String, String>> clientSettings = new HashMap<>();

    private final ChannelContainer channel;

    public PlayerSync(ChannelContainer channel) {
        this.channel = channel;
    }

    void handleRegister(Player player, SRegisterData data) {
        UUID uniqueId = player.getUniqueId();
        this.playerChannels.putAll(uniqueId, data.getChannels());
        this.playerSettings.putAll(uniqueId, data.getSettings());

        if (!this.playerChannels.isEmpty()) {
            for (String chan : playerChannels.get(uniqueId)) {
                CChannelData bytes = new CChannelData(chan, getPlayerData(chan));
                this.channel.sendData(player, bytes);
            }
        }
        if (!this.playerSettings.isEmpty()) {
            DataView view = prepareSettings(uniqueId);
            this.channel.sendSettings(player, new CSettingsData(view));
        }
    }

    void handlePacket(Player player, SClientData buf) {
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
            try {
                Player player2 = getPlayerFromUniqueId(e.getKey());
                this.channel.sendData(player2, cd);
            } catch (PlayerNotFoundException ex) {
                getPlayerData(channel).remove(e.getKey());
                toRemove.add(e.getKey());
            }
        }
        for (UUID uuid : toRemove) {
            this.playerChannels.removeAll(uuid);
        }

    }

    void onChannelRegister(UUID uniqueId) {
        try {
            Player player = getPlayerFromUniqueId(uniqueId);
            channel.sendRegistration(player, new CAwkData());
        } catch (PlayerNotFoundException ignored) {
        }
    }


    void removePlayer(UUID uniqueId) {
        this.playerChannels.removeAll(uniqueId);
        for (Map<UUID, byte[]> players : this.channels.values()) {
            players.remove(uniqueId);
        }
    }

    void loadConfig(ConfigurationNode conf) {
        this.clientSettings.clear();
        for (Entry<?, ? extends ConfigurationNode> e : conf.getNode("clientConfig").getChildrenMap().entrySet()) {
            String name = e.getKey().toString();
            this.clientSettings.put(name, e.getValue().getChildrenMap().entrySet().stream().map(entry -> {
                String key = entry.getKey().toString();
                String val = entry.getValue().getString();
                return new Pair<>(key, val);
            }).collect(Collectors.toMap(Pair::getKey, Pair::getValue)));
        }
    }

    private Map<UUID, byte[]> getPlayerData(String channel) {
        return this.channels.computeIfAbsent(channel, b -> new HashMap<>());
    }

    private static Player getPlayerFromUniqueId(UUID uniqueId) throws PlayerNotFoundException {
        return Sponge.getServer().getPlayer(uniqueId).orElseThrow(PlayerNotFoundException::new);
    }

    private DataView prepareSettings(UUID uuid) {
        DataView view = new MemoryDataContainer();
        for (String setts : this.playerSettings.get(uuid)) {
            view.createView(DataQuery.of('.', setts), this.clientSettings.get(setts));
        }
        return view;
    }

}
