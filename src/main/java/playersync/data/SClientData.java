package playersync.data;

import org.spongepowered.api.network.ChannelBuf;

import javax.annotation.Nonnull;

public class SClientData extends ChannelDataBase {

    private byte[] data;

    @Override
    public void read(@Nonnull ChannelBuf buf) {
        if (isRegistration()) return; // not supported anymore
        this.data = buf.readByteArray();
    }

    public byte[] getData() {
        return data;
    }

    private boolean isRegistration() {
        String channel = getChannel();
        return "REGISTER".equals(channel) || "UNREGISTER".equals(channel);
    }
}
