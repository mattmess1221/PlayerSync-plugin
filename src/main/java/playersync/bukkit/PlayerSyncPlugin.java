package playersync.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.java.JavaPlugin;
import playersync.Channels;
import playersync.OutdatedClientException;
import playersync.PlayerSyncServer;
import playersync.Texts;
import playersync.adapters.Adapters;
import playersync.adapters.DataAdapter;
import playersync.adapters.PlayerAdapter;
import playersync.bukkit.data.IServerDataHandler;
import playersync.bukkit.data.client.SClientData;
import playersync.bukkit.data.client.SRegisterData;

import java.nio.ByteBuffer;


public class PlayerSyncPlugin extends JavaPlugin implements Listener, IServerDataHandler {

    private PlayerSyncServer<Player, ? extends ByteBuffer> sync;

    @Override
    public void onLoad() {

        registerAdapters();

        BukkitChannels channels = new BukkitChannels(this);
        this.sync = new PlayerSyncServer<>(channels);

        Bukkit.getPluginManager().registerEvents(this, this);

    }

    private void registerAdapters() {

        Adapters.register(DataAdapter.class, new BukkitDataAdapter());
        Adapters.register(PlayerAdapter.class, new BukkitPlayerAdapter());

    }

    @EventHandler
    public void onRegister(PlayerRegisterChannelEvent event) {
        Player player = event.getPlayer();
        if (Channels.CHANNEL.equals(event.getChannel())) {
            sync.onChannelRegister(player);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        sync.removePlayer(event.getPlayer().getUniqueId());
    }

    @Override
    public void handleRegisterData(Player player, SRegisterData sRegisterData) {
        try {
            sync.handleRegister(player, sRegisterData);
        } catch (OutdatedClientException e) {
            player.sendMessage(ChatColor.YELLOW + Texts.OUTDATED);
            getLogger().info(player.getName() + " joined with outdated playersync mod. Version: " + e.getVersion());

        }
    }

    @Override
    public void handleClientData(Player player, SClientData sClientData) {
        sync.handlePacket(player, sClientData);
    }
}
