package playersync.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ChannelRegistrationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.plugin.Plugin;

import static playersync.Constants.CHANNEL;

@Plugin(
        id = "playersync",
        name = "PlayerSync",
        authors = "killjoy1221",
        version = "0.1-SNAPSHOT",
        description = "Automatically syncs settings between and with client mods."
)
public class PlayerSyncSponge {

    private SpongeSync sync;

    @Listener
    public void onServerStart(GameInitializationEvent event) {
        ChannelBinding.IndexedMessageChannel channel = Sponge.getChannelRegistrar().createChannel(this, CHANNEL);
        channel.registerMessage(ChannelDataMessage.class, 1);
        channel.addHandler(ClientDataMessage.class, sync::handlePacket);
        this.sync = new SpongeSync(channel);
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
