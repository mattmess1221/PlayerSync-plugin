package playersync;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Platform;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ChannelRegistrationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import playersync.data.SClientData;
import playersync.data.SRegisterData;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = "playersync",
        name = "PlayerSync",
        authors = "killjoy1221",
        version = "0.4-SNAPSHOT",
        description = "Automatically syncs settings between and with client mods."
)
public class PlayerSyncPlugin implements ChannelHandler {

    private static final String CHANNEL_LEGACY = "pSync";

    private ChannelContainer channels;
    private PlayerSync sync;


    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path configPath;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameInitializationEvent event) {

        this.channels = new ChannelContainer(this);
        this.sync = new PlayerSync(this.channels);

        try {
            this.sync.loadConfig(this.configLoader.load());
        } catch (IOException e) {
            logger.warn("Unable to read config", e);
        }

    }

    @Override
    public void handleSyncPacket(SClientData message, RemoteConnection connection, Platform.Type side) {
        if (connection instanceof PlayerConnection) {
            sync.handlePacket(((PlayerConnection) connection).getPlayer(), message);
        }
    }

    @Override
    public void handleRegisterPacket(SRegisterData message, RemoteConnection connection, Platform.Type side) {
        if (connection instanceof PlayerConnection) {
            sync.handleRegister(((PlayerConnection) connection).getPlayer(), message);
        }
    }

    @Listener
    public void onRegister(ChannelRegistrationEvent.Register event, @Getter("getChannel") String channel) {
        Optional<Player> player = event.getCause().get(NamedCause.SOURCE, Player.class);
        switch (channel) {
            case "pSync|reg":
                player.map(Player::getUniqueId).ifPresent(sync::onChannelRegister);
                break;
            case CHANNEL_LEGACY:
                // legacy (outdated, unsupported)
                player.ifPresent(this::warnPlayerOfOutdatedMod);
                break;
        }
    }

    private void warnPlayerOfOutdatedMod(Player player) {
        player.sendMessage(ChatTypes.SYSTEM, Text.of(TextColors.YELLOW,
                "Your version of PlayerSync is outdated. Please update to use it on this server"));
    }

    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
        sync.removePlayer(player.getUniqueId());
    }

    @Listener
    public void configReload(GameReloadEvent reload) {
        try {
            this.sync.loadConfig(this.configLoader.load());
        } catch (IOException e) {
            this.logger.warn("Unable to load config", e);
        }
    }

}
