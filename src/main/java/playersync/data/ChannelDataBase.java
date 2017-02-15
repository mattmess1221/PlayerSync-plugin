package playersync.data;

import static playersync.PlayerSyncPlugin.REGISTER;
import static playersync.PlayerSyncPlugin.UNREGISTER;

import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;

public class ChannelDataBase implements Message {

    private String channel;

    public ChannelDataBase() {

    }

    public ChannelDataBase(String channel) {
        this.channel = channel;
    }

    @Override
    public void readFrom(ChannelBuf buf) {
        this.channel = buf.readString();
    }

    @Override
    public void writeTo(ChannelBuf buf) {
        buf.writeString(this.channel);
    }

    public String getChannel() {
        return channel;
    }

    public boolean isRegistration() {
        return REGISTER.equals(channel) || UNREGISTER.equals(channel);
    }
}
