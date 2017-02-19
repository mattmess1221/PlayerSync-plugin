package playersync;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import playersync.data.CChannelData;
import playersync.data.CHelloData;
import playersync.data.CSettingsData;
import playersync.data.SClientData;
import playersync.data.SRegisterData;

// POJO
public class ChannelContainer {

    private static final String CHANNEL_OLD = "pSync";
    private static final String CHANNEL_REG = "pSync|reg";
    private static final String CHANNEL_DATA = "pSync|data";
    private static final String CHANNEL_CONF = "pSync|conf";

    private ChannelBinding.IndexedMessageChannel register;
    private ChannelBinding.IndexedMessageChannel clientData;
    private ChannelBinding.IndexedMessageChannel settings;

    public ChannelContainer(ChannelHandler plugin) {
        Sponge.getChannelRegistrar().createChannel(plugin, CHANNEL_OLD);

        clientData = Sponge.getChannelRegistrar().createChannel(plugin, CHANNEL_DATA);
        clientData.registerMessage(CChannelData.class, 1);
        clientData.addHandler(SClientData.class, plugin::handleSyncPacket);

        register = Sponge.getChannelRegistrar().createChannel(plugin, CHANNEL_REG);
        register.addHandler(SRegisterData.class, plugin::handleRegisterPacket);
        register.registerMessage(CHelloData.class, 1);

        settings = Sponge.getChannelRegistrar().createChannel(plugin, CHANNEL_CONF);
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
