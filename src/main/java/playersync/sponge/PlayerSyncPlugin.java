package playersync.sponge;

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
import playersync.Channels;
import playersync.Texts;
import playersync.sponge.data.server.SClientData;
import playersync.sponge.data.server.SRegisterData;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = "playersync",
        name = "PlayerSync",
        authors = "killjoy1221",
        description = "Automatically syncs settings between and with client mods."
)
public class PlayerSyncPlugin {

    private SpongeData channels;
    private SpongePlayerSyncServer sync;

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

        this.channels = new SpongeData(this);
        this.sync = new SpongePlayerSyncServer(this.channels);

        try {
            this.sync.loadConfig(this.configLoader.load());
        } catch (IOException e) {
            logger.warn("Unable to read config", e);
        }

    }

    public void handleSyncPacket(SClientData message, RemoteConnection connection, Platform.Type side) {
        if (connection instanceof PlayerConnection) {
            sync.handlePacket(((PlayerConnection) connection).getPlayer(), message);
        }
    }

    public void handleRegisterPacket(SRegisterData message, RemoteConnection connection, Platform.Type side) {
        if (connection instanceof PlayerConnection) {
            sync.handleRegister(((PlayerConnection) connection).getPlayer(), message);
        }
    }

    @Listener
    public void onRegister(ChannelRegistrationEvent.Register event, @Getter("getChannel") String channel) {
        Optional<Player> player = event.getCause().get(NamedCause.SOURCE, Player.class);
        switch (channel) {
            case Channels.CHANNEL_REG:
                player.ifPresent(sync::onChannelRegister);
                break;
            case Channels.CHANNEL_OLD:
                // legacy (outdated, unsupported)
                player.ifPresent(this::warnPlayerOfOutdatedMod);
                break;
        }
    }

    private void warnPlayerOfOutdatedMod(Player player) {
        player.sendMessage(ChatTypes.SYSTEM, Text.of(TextColors.YELLOW, Texts.OUTDATED));
    }

    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
        sync.removePlayer(player);
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
