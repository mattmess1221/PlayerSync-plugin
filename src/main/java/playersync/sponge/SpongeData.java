package playersync.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelRegistrar;
import playersync.Channels;
import playersync.sponge.data.client.CChannelData;
import playersync.sponge.data.client.CHelloData;
import playersync.sponge.data.client.CSettingsData;
import playersync.sponge.data.server.SClientData;
import playersync.sponge.data.server.SRegisterData;

public class SpongeData implements Channels {

    private static final String CHANNEL_OLD = "pSync";
    private static final String CHANNEL_REG = "pSync|reg";
    private static final String CHANNEL_DATA = "pSync|data";
    private static final String CHANNEL_CONF = "pSync|conf";

    private ChannelBinding.IndexedMessageChannel register;
    private ChannelBinding.IndexedMessageChannel clientData;
    private ChannelBinding.IndexedMessageChannel settings;

    public SpongeData(PlayerSyncPlugin plugin) {
        ChannelRegistrar reg = Sponge.getChannelRegistrar();

        reg.createChannel(plugin, CHANNEL_OLD);

        clientData = reg.createChannel(plugin, CHANNEL_DATA);
        clientData.registerMessage(CChannelData.class, 1);
        clientData.registerMessage(SClientData.class, 2, plugin::handleSyncPacket);

        register = reg.createChannel(plugin, CHANNEL_REG);
        register.registerMessage(CHelloData.class, 1);
        register.registerMessage(SRegisterData.class, 2, plugin::handleRegisterPacket);

        settings = reg.createChannel(plugin, CHANNEL_CONF);
        settings.registerMessage(CSettingsData.class, 1);
    }

    public void sendData(Player player, CChannelData bytes) {
        this.clientData.sendTo(player, bytes);
    }

    public void sendSettings(Player player, CSettingsData settingsData) {
        this.settings.sendTo(player, settingsData);
    }

    public void sendRegistration(Player player, CHelloData awkData) {
        this.register.sendTo(player, awkData);
    }
}
