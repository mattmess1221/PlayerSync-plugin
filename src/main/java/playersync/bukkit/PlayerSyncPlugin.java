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
import playersync.Texts;
import playersync.bukkit.data.client.SClientData;
import playersync.bukkit.data.client.SRegisterData;

import java.io.ByteArrayInputStream;
import java.io.IOException;


public class PlayerSyncPlugin extends JavaPlugin implements Listener {

    private BukkitData channels;
    private BukkitPlayerSyncServer sync;

    @Override
    public void onLoad() {
        this.channels = new BukkitData(this);
        this.sync = new BukkitPlayerSyncServer(channels);

        Bukkit.getPluginManager().registerEvents(this, this);

    }

    public void handleSyncPacket(String s, Player player, byte[] bytes) {
        sync.handlePacket(player, moreData(bytes, SClientData::new));
    }

    public void handleRegisterPacket(String s, Player player, byte[] bytes) {
        sync.handleRegister(player, moreData(bytes, SRegisterData::new));
    }

    private interface DataConst<T> {
        T newInstance(ByteArrayInputStream buffer) throws IOException;
    }

    private <T> T moreData(byte[] bytes, DataConst<T> constructor) {
        try(ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            return constructor.newInstance(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onRegister(PlayerRegisterChannelEvent event) {
        Player player = event.getPlayer();
        switch (event.getChannel()) {
            case Channels.CHANNEL_REG:
                sync.onChannelRegister(player);
                break;
            case Channels.CHANNEL_OLD:
                // legacy (outdated, unsupported)
                player.sendMessage(ChatColor.YELLOW + Texts.OUTDATED);
                break;
        }

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        sync.removePlayer(event.getPlayer());
    }

}
