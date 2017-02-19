package playersync.data;

import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;

import javax.annotation.Nonnull;

public abstract class ChannelDataBase implements Message {

    private String channel;

    public ChannelDataBase() {

    }

    public ChannelDataBase(String channel) {
        this.channel = channel;
    }

    @Override
    public final void readFrom(@Nonnull ChannelBuf buf) {
        this.channel = buf.readString();
        read(buf);
    }

    protected void read(@Nonnull ChannelBuf buf) {
    }

    @Override
    public final void writeTo(@Nonnull ChannelBuf buf) {
        buf.writeString(this.channel);
        write(buf);
    }

    protected void write(@Nonnull ChannelBuf buf) {
    }

    public String getChannel() {
        return channel;
    }


}
