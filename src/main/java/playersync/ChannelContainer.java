package playersync;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import playersync.data.CAwkData;
import playersync.data.CChannelData;
import playersync.data.SClientData;
import playersync.data.SRegisterData;
import playersync.data.CSettingsData;

// POJO
public class ChannelContainer {

    private static final String  CHANNEL_REG = "pSync|reg";
    private static final String CHANNEL_DATA = "pSync|data";
    private static final String CHANNEL_CONF = "pSync|conf";

    private ChannelBinding.IndexedMessageChannel register;
    private ChannelBinding.IndexedMessageChannel clientData;
    private ChannelBinding.IndexedMessageChannel settings;

    public ChannelContainer(ChannelHandler plugin) {
        clientData = Sponge.getChannelRegistrar().createChannel(this, CHANNEL_DATA);
        clientData.registerMessage(CChannelData.class, 1);
        clientData.addHandler(SClientData.class, plugin::handleSyncPacket);

        register = Sponge.getChannelRegistrar().createChannel(this, CHANNEL_REG);
        register.addHandler(SRegisterData.class, plugin::handleRegisterPacket);
        register.registerMessage(CAwkData.class, 1);

        settings = Sponge.getChannelRegistrar().createChannel(this, CHANNEL_CONF);
        settings.registerMessage(CSettingsData.class, 1);
    }

    public void sendData(Player player, CChannelData bytes) {
        this.clientData.sendTo(player, bytes);
    }

    public void sendSettings(Player player, CSettingsData settingsData) {
        this.settings.sendTo(player, settingsData);
    }

    public void sendRegistration(Player player, CAwkData awkData) {
        this.register.sendTo(player, awkData);
    }
}
