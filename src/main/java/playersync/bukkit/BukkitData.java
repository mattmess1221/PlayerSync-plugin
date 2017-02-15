package playersync.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import playersync.Channels;
import playersync.bukkit.data.ServerData;
import playersync.bukkit.data.server.CChannelData;
import playersync.bukkit.data.server.CHelloData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BukkitData implements Channels {


    private final PlayerSyncPlugin plugin;

    public BukkitData(PlayerSyncPlugin plugin) {
        this.plugin = plugin;
        Messenger messenger = Bukkit.getMessenger();

        messenger.registerIncomingPluginChannel(plugin, CHANNEL_OLD, (c, p, m) -> {
        });

        messenger.registerOutgoingPluginChannel(plugin, CHANNEL_DATA);
        messenger.registerIncomingPluginChannel(plugin, CHANNEL_DATA, plugin::handleSyncPacket);

        messenger.registerOutgoingPluginChannel(plugin, CHANNEL_REG);
        messenger.registerIncomingPluginChannel(plugin, CHANNEL_REG, plugin::handleRegisterPacket);

    }

    public void sendData(Player player, CChannelData bytes) {
        this.sendPluginMessage(player, CHANNEL_DATA, bytes);
    }

    public void sendRegistration(Player player, CHelloData awkData) {
        this.sendPluginMessage(player, CHANNEL_REG, awkData);
    }

    private void sendPluginMessage(Player player, String channel, ServerData data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            data.write(out);
            player.sendPluginMessage(plugin, channel, out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
