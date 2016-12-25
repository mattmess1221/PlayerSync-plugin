package playersync;

import playersync.data.ChannelData;
import playersync.data.ClientData;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public abstract class PlayerSync implements Constants {

    private Map<UUID, Set<String>> playerChannels = new HashMap<>();
    private Map<String, Map<UUID, byte[]>> channels = new HashMap<>();

    protected abstract void sendMessageToPlayer(UUID uniqueId, ChannelData msg) throws PlayerNotFoundException;

    public void handlePacket(UUID uniqueId, ClientData buf) throws IOException {

        String channel = buf.getChannel();

        if (REGISTER.equals(channel)) {
            Set<String> channels = getPlayerChannels(uniqueId);
            channels.addAll(buf.getRegistrations());
        } else if (UNREGISTER.equals(channel)) {
            getPlayerChannels(uniqueId).removeAll(buf.getRegistrations());
        } else {
            byte[] data = buf.getData();
            getPlayerData(channel).put(uniqueId, data);
            Set<UUID> toRemove = new HashSet<>();
            for (Entry<UUID, Set<String>> e : this.playerChannels.entrySet()) {
                if (e.getKey().equals(uniqueId) || !e.getValue().contains(channel))
                    continue;
                ChannelData cd = new ChannelData(channel).addData(uniqueId, data);
                try {
                    sendMessageToPlayer(e.getKey(), cd);
                } catch (PlayerNotFoundException ex) {
                    getPlayerData(channel).remove(e.getKey());
                    toRemove.add(e.getKey());
                }
            }
            for (UUID uuid : toRemove) {
                this.playerChannels.remove(uuid);
            }
        }
    }

    public void onChannelRegister(UUID uniqueId) {
        try {
            for (String chan : getPlayerChannels(uniqueId)) {
                ChannelData bytes = new ChannelData(chan).addData(getPlayerData(chan));
                sendMessageToPlayer(uniqueId, bytes);
            }
        } catch (PlayerNotFoundException ignored) {
        }
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