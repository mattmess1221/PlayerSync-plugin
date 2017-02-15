package playersync.sponge.data.client;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;

import javax.annotation.Nonnull;

public class CSettingsData implements Message {

    private DataView view;

    public CSettingsData(DataView data) {
        this.view = data;
    }

    public void writeTo(@Nonnull ChannelBuf buf) {
        buf.writeDataView(view);
    }

    @Override
    public void readFrom(@Nonnull ChannelBuf buf) {

    }
}
