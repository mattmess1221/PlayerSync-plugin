package playersync.sponge;

import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;
import playersync.data.ChannelData;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Matthew on 12/24/2016.
 */
public class ChannelDataMessage extends ChannelData implements Message {

    public ChannelDataMessage(String channel) {
        super(channel);
    }

    @Override
    public void writeTo(@Nonnull ChannelBuf buf) {
        buf.writeString(getChannel());
        List<PlayerData> data = getPlayerData();
        buf.writeVarInt(data.size());
        for (PlayerData player : data) {
            buf.writeUniqueId(player.getUniqueId());
            buf.writeBytes(player.getData());
        }
    }

    @Override
    public void readFrom(@Nonnull ChannelBuf buf) {
    }

}
