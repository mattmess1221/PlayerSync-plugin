package playersync.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.network.Message;
import playersync.Channels;
import playersync.data.Data;
import playersync.sponge.data.client.CChannelData;
import playersync.sponge.data.client.CHelloData;
import playersync.sponge.data.server.SClientData;
import playersync.sponge.data.server.SRegisterData;

public class SpongeChannels implements Channels<Player, ChannelBuf> {

    private ChannelBinding.IndexedMessageChannel channel;

    public SpongeChannels(PlayerSyncPlugin plugin) {
        ChannelRegistrar reg = Sponge.getChannelRegistrar();

        channel = reg.createChannel(plugin, CHANNEL);

        channel.registerMessage(CHelloData.class, 0);
        channel.registerMessage(SRegisterData.class, 1, plugin::handleRegisterPacket);
        channel.registerMessage(CChannelData.class, 2);
        channel.registerMessage(SClientData.class, 3, plugin::handleSyncPacket);

    }

    @Override
    public void sendData(Player player, Data<ChannelBuf> data) {
        this.channel.sendTo(player, (Message) data);
    }

}
