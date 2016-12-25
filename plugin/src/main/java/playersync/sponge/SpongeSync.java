package playersync.sponge;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.Message;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.network.RemoteConnection;
import playersync.PlayerNotFoundException;
import playersync.PlayerSync;
import playersync.data.ChannelData;

import java.io.IOException;
import java.util.UUID;

public class SpongeSync extends PlayerSync {

    private final ChannelBinding.IndexedMessageChannel channel;

    public SpongeSync(ChannelBinding.IndexedMessageChannel channel) {
        super();
        this.channel = channel;
    }

    public void handlePacket(ClientDataMessage message, RemoteConnection connection, Platform.Type side) {
        try {
            if (connection instanceof PlayerConnection) {
                handlePacket(((PlayerConnection) connection).getPlayer().getUniqueId(), message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void sendMessageToPlayer(UUID uniqueId, ChannelData msg) throws PlayerNotFoundException {
        Player player = Sponge.getServer().getPlayer(uniqueId).orElseThrow(PlayerNotFoundException::new);
        channel.sendTo(player, (Message) msg);
    }
}
