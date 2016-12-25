package playersync.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static playersync.Constants.CHANNEL;

public class PlayerSyncBukkit extends JavaPlugin implements Listener {

    private BukkitSync sync = new BukkitSync(this);

    @Override
    public void onEnable() {

        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, sync::handlePacket);

        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent quit) {
        this.sync.removePlayer(quit.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onRegisterChannel(PlayerRegisterChannelEvent event) {

        if (CHANNEL.equals(event.getChannel()))
            sync.onChannelRegister(event.getPlayer().getUniqueId());
    }

}
