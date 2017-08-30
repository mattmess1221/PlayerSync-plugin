package playersync.sponge;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Platform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ChannelRegistrationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import playersync.Channels;
import playersync.OutdatedClientException;
import playersync.PlayerSyncServer;
import playersync.Texts;
import playersync.adapters.Adapters;
import playersync.adapters.DataAdapter;
import playersync.adapters.PlayerAdapter;
import playersync.data.server.IClientData;
import playersync.data.server.IRegisterData;

@Plugin(
        id = "playersync",
        name = "PlayerSync",
        authors = "killjoy1221",
        description = "Automatically syncs settings between and with client mods."
)
public class PlayerSyncPlugin {

    private PlayerSyncServer<Player, ChannelBuf> sync;

//    @Inject
//    @DefaultConfig(sharedRoot = true)
//    private Path configPath;
//
//    @Inject
//    @DefaultConfig(sharedRoot = true)
//    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameInitializationEvent event) {

        this.registerAdapters();

        SpongeChannels channels = new SpongeChannels(this);
        this.sync = new PlayerSyncServer<>(channels);

//        try {
//            this.sync.loadConfig(this.configLoader.load());
//        } catch (IOException e) {
//            logger.warn("Unable to read config", e);
//        }

    }

    private void registerAdapters() {

        Adapters.register(DataAdapter.class, new SpongeDataAdapter());
        Adapters.register(PlayerAdapter.class, new SpongePlayerAdapter());

    }

    public void handleSyncPacket(IClientData message, RemoteConnection connection, Platform.Type side) {
        if (connection instanceof PlayerConnection) {
            sync.handlePacket(((PlayerConnection) connection).getPlayer(), message);
        }
    }

    public void handleRegisterPacket(IRegisterData message, RemoteConnection connection, Platform.Type side) {
        if (connection instanceof PlayerConnection) {
            Player player = ((PlayerConnection) connection).getPlayer();
            try {
                sync.handleRegister(player, message);
            } catch (OutdatedClientException e) {
                player.sendMessage(Text.of(TextColors.YELLOW, Texts.OUTDATED));
                logger.info("{} tried to join with outdated playersync mod. Version: {}", player.getName(), e.getVersion());
            }
        }
    }

    @Listener
    public void onRegister(ChannelRegistrationEvent.Register event, @Getter("getChannel") String channel, @First Player player) {
        System.out.println(player.getName() + " is registered for " + channel);
        if (Channels.CHANNEL.equals(channel)) {
            sync.onChannelRegister(player);
        }
    }

    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
        sync.removePlayer(player.getUniqueId());
    }

//    @Listener
//    public void configReload(GameReloadEvent reload) {
//        try {
//            this.sync.loadConfig(this.configLoader.load());
//        } catch (IOException e) {
//            this.logger.warn("Unable to load config", e);
//        }
//    }

}
