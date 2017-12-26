package playersync.bukkit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import playersync.Channels;
import playersync.bukkit.data.BukkitData;
import playersync.bukkit.data.IServerDataHandler;
import playersync.bukkit.data.client.SClientData;
import playersync.bukkit.data.client.SRegisterData;
import playersync.bukkit.data.server.CChannelData;
import playersync.bukkit.data.server.CHelloData;
import playersync.data.Data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;

public class BukkitChannels implements Channels<Player, ByteBuffer> {


    private final PlayerSyncPlugin plugin;

    private BiMap<Integer, Class<? extends BukkitData<?>>> dataPackets = HashBiMap.create();

    public BukkitChannels(PlayerSyncPlugin plugin) {
        this.plugin = plugin;
        Messenger messenger = Bukkit.getMessenger();

        messenger.registerIncomingPluginChannel(plugin, CHANNEL, this::onMessageReceived);
        messenger.registerOutgoingPluginChannel(plugin, CHANNEL);

        dataPackets.put(0, CHelloData.class);
        dataPackets.put(1, SRegisterData.class);
        dataPackets.put(2, CChannelData.class);
        dataPackets.put(3, SClientData.class);
    }

    @SuppressWarnings("unchecked")
    private void onMessageReceived(String channel, Player player, byte[] bytes) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            int index = buffer.get();
            Class<? extends BukkitData<?>> dataClass = dataPackets.get(index);

            checkNotNull(dataClass, "Unknown packet ID: %s", index);

            BukkitData<IServerDataHandler> data = (BukkitData<IServerDataHandler>) dataClass.newInstance();

            data.read(buffer);

            if (buffer.hasRemaining()) {
                throw new IOException(dataClass + " did not read the entire packet.");
            }

            data.handle(player, this.plugin);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Unable to process packet.", e);
        }
    }

    @Override
    public void sendData(Player player, Data<ByteBuffer> data) {

        try {
            int index = dataPackets.inverse().getOrDefault(data.getClass(), -1);
            checkArgument(index >= 0, "%s is not a registered data packet.", data.getClass());

            ByteBuffer buffer = ByteBuffer.allocate(Short.MAX_VALUE - 1);
            buffer.put((byte) index);
            data.write(buffer);

            player.sendPluginMessage(plugin, CHANNEL, Arrays.copyOf(buffer.array(), buffer.position()));
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Unable to send packet.", e);
        }

    }


}
