package playersync;

import com.google.common.collect.HashMultimap;
import playersync.adapters.Adapters;
import playersync.adapters.DataAdapter;
import playersync.adapters.PlayerAdapter;
import playersync.data.Data;
import playersync.data.server.IClientData;
import playersync.data.server.IRegisterData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class PlayerSyncServer<Player, Buf> {

    private static final int PROTOCOL = 4;

    private HashMultimap<UUID, String> playerChannels = HashMultimap.create();

    private Map<String, Map<UUID, byte[]>> channels = new HashMap<>();

    private final Channels<Player, Buf> channel;

    public PlayerSyncServer(Channels<Player, Buf> channel) {
        this.channel = channel;
    }

    private UUID getUniqueIdFrom(Player player) {
        return Adapters.get(PlayerAdapter.token()).getUniqueId(player);
    }

    private Optional<Player> getPlayerFrom(UUID uuid) {
        return Adapters.get(PlayerAdapter.<Player>token()).getPlayer(uuid);
    }

    private DataAdapter<Buf> data() {
        return Adapters.get(DataAdapter.token());
    }

    public void handleRegister(Player player, IRegisterData data) throws OutdatedClientException {
        if (data.getVersion() != PROTOCOL) {
            throw new OutdatedClientException(data.getVersion());
        }
        UUID uniqueId = getUniqueIdFrom(player);
        this.playerChannels.putAll(uniqueId, data.getChannels());

        if (!this.playerChannels.isEmpty()) {
            for (String chan : playerChannels.get(uniqueId)) {
                Data<Buf> bytes = data().newChannelData(chan, getPlayerData(chan));
                this.channel.sendData(player, bytes);
            }
        }
        // TODO
//        if (!this.playerSettings.isEmpty()) {
//            DataView view = prepareSettings(uniqueId);
//            this.channel.sendData(player, new CSettingsData(view));
//        }
    }

    public void handlePacket(Player player, IClientData buf) {
        String channel = buf.getChannel();
        UUID uniqueId = getUniqueIdFrom(player);

        byte[] data = buf.getData();
        getPlayerData(channel).put(uniqueId, data);
        Set<UUID> toRemove = new HashSet<>();
        for (Entry<UUID, String> e : this.playerChannels.entries()) {
            UUID id = e.getKey();
            if (id.equals(uniqueId) || !e.getValue().equals(channel))
                continue;
            Data<Buf> cd = data().newChannelData(channel, uniqueId, data);

            Optional<Player> player1 = getPlayerFrom(e.getKey());
            if (player1.isPresent()) {
                this.channel.sendData(player1.get(), cd);
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
        channel.sendData(player, data().newHelloData(PROTOCOL));
    }

    public void removePlayer(UUID uniqueId) {
        this.playerChannels.removeAll(uniqueId);
        for (Map<UUID, byte[]> players : this.channels.values()) {
            players.remove(uniqueId);
        }
    }

//    public void loadConfig(ConfigurationNode conf) {
//        this.clientSettings.clear();
//        for (Entry<?, ? extends ConfigurationNode> e : conf.getNode("clientConfig").getChildrenMap().entrySet()) {
//            String name = e.getKey().toString();
//            this.clientSettings.put(name, e.getValue().getChildrenMap().entrySet().stream().map(entry -> {
//                String key = entry.getKey().toString();
//                String val = entry.getValue().getString();
//                return new Pair<>(key, val);
//            }).collect(Collectors.toMap(Pair::getKey, Pair::getValue)));
//        }
//    }

    private Map<UUID, byte[]> getPlayerData(String channel) {
        return this.channels.computeIfAbsent(channel, b -> new HashMap<>());
    }

//    DataView prepareSettings(UUID uuid) {
//        DataView view = new MemoryDataContainer();
//        for (String setts : this.playerSettings.get(uuid)) {
//            view.createView(DataQuery.of('.', setts), this.clientSettings.get(setts));
//        }
//        return view;
//    }

}
