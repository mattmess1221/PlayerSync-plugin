package playersync.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import playersync.client.api.ChannelHandler;
import playersync.client.api.PacketSerializer;
import playersync.client.api.SyncManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import static playersync.Constants.*;

@SuppressWarnings("unchecked")
public class ClientSyncManager implements SyncManager {

    private static Logger logger = LogManager.getLogger(CHANNEL);

    @Nullable
    private NetHandlerPlayClient client;

    private Set<String> channels = Sets.newHashSet();
    private Map<String, PacketSerializer<?>> channelSerializers = Maps.newHashMap();
    private Map<String, ChannelHandler<?>> channelHandlers = Maps.newHashMap();
    private Map<String, Object> channelDefaults = Maps.newHashMap();

    private void sendHelloPacket(@Nonnull NetHandlerPlayClient client) {

        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeString(REGISTER);
        buffer.writeVarInt(channels.size());
        for (String channel : channels) {
            buffer.writeString(channel);
        }

        client.sendPacket(new CPacketCustomPayload(CHANNEL, buffer));
    }

    private void onHelloResponse() {
        logger.info("Servpai noticed me!");
        for (Entry<String, ? super Object> a : channelDefaults.entrySet()) {
            sendPacket(a.getKey(), a.getValue());
        }
    }

    public <T> void onPayload(PacketBuffer buffer) {
        String chan = buffer.readString(20);
        if (ACKNOWLEDGE.equals(chan)) {
            onHelloResponse();
            return;
        }
        if (!this.channels.contains(chan)) {
            logger.warn("Got packet for unknown channel " + chan + ". Ignoring");
            return;
        }
        PacketSerializer<T> serializer = (PacketSerializer<T>) this.channelSerializers.get(chan);
        ChannelHandler<T> handler = (ChannelHandler<T>) this.channelHandlers.get(chan);
        try {
            int len = buffer.readVarInt();
            for (int i = 0; i < len; i++) {
                UUID uuid = buffer.readUniqueId();
                T packet = serializer.deserialize(buffer);
                handler.handle(chan, uuid, packet);
            }
        } catch (IndexOutOfBoundsException e) {
            logger.info(e);
        }
    }

    public <T> void sendPacket(String chan, T packet) {
        if (this.client != null && this.client.getNetworkManager().isChannelOpen()) {

            if (!this.channels.contains(chan))
                throw new IllegalArgumentException("There is no channel registered for " + chan);

            PacketSerializer<T> serializer = (PacketSerializer<T>) this.channelSerializers.get(chan);
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            // this will be sent to clients exactly
            buffer.writeString(chan);
            serializer.serialize(packet, buffer);

            this.client.sendPacket(new CPacketCustomPayload(CHANNEL, buffer));

        }
    }

    @Override
    public <T> void register(String channel, PacketSerializer<T> serializer, ChannelHandler<T> handler, T def) {
        Preconditions.checkArgument(isChannelValid(channel), "Channel name " + channel + " is reserved.");
        Preconditions.checkNotNull(channel, "channel cannot be null.");
        Preconditions.checkNotNull(serializer, "serializer cannot be null.");
        Preconditions.checkNotNull(handler, "handler cannot be null.");
        Preconditions.checkNotNull(def, "default cannot be null.");


        this.channels.add(channel);
        this.channelDefaults.put(channel, def);
        this.channelHandlers.put(channel, handler);
        this.channelSerializers.put(channel, serializer);
    }

    private boolean isChannelValid(String channel) {
        return !Arrays.asList(REGISTER, UNREGISTER, ACKNOWLEDGE).contains(channel);
    }

    public void setClient(@Nonnull NetHandlerPlayClient client) {

        this.client = client;

        this.sendHelloPacket(client);
    }
}
