package playersync;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ChannelRegistrationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.plugin.Plugin;
import playersync.data.ChannelData;
import playersync.data.ChannelDataBase;
import playersync.data.ClientData;

import java.io.IOException;

@Plugin(
        id = "playersync",
        name = "PlayerSync",
        authors = "killjoy1221",
        version = "0.1-SNAPSHOT",
        description = "Automatically syncs settings between and with client mods."
)
public class PlayerSyncPlugin {

    private static final String CHANNEL = "pSync";
    public static final String REGISTER = "REGISTER";
    public static final String UNREGISTER = "UNREGISTER";
    public static final String ACKNOWLEDGE = "ACKNOWLEDGE";

    private PlayerSync sync;

    @Listener
    public void onServerStart(GameInitializationEvent event) {
        ChannelBinding.IndexedMessageChannel channel = Sponge.getChannelRegistrar().createChannel(this, CHANNEL);
        channel.registerMessage(ChannelData.class, 1);
        channel.registerMessage(ChannelDataBase.class, 1);
        channel.addHandler(ClientData.class, this::handlePacket);
        this.sync = new PlayerSync(channel);
    }

    private void handlePacket(ClientData message, RemoteConnection connection, Platform.Type side) {
        try {
            if (connection instanceof PlayerConnection) {
                sync.handlePacket(((PlayerConnection) connection).getPlayer(), message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Listener
    public void onRegister(ChannelRegistrationEvent.Register event, @Getter("getChannel") String channel) {
        if (CHANNEL.equals(channel))
            event.getCause()
                    .get(NamedCause.SOURCE, Player.class)
                    .map(Player::getUniqueId)
                    .ifPresent(sync::onChannelRegister);
    }

    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
        sync.removePlayer(player.getUniqueId());
    }

}
