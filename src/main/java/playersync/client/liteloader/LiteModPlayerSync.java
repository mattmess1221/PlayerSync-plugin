package playersync.client.liteloader;

import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mumfrey.liteloader.JoinGameListener;
import com.mumfrey.liteloader.PluginChannelListener;
import com.mumfrey.liteloader.Priority;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketJoinGame;
import playersync.client.ClientSyncManager;
import playersync.client.api.IPlayerSync;
import playersync.client.api.SyncManager;

import java.io.File;
import java.util.List;

import static playersync.Constants.CHANNEL;

@Priority(500)
public class LiteModPlayerSync implements IPlayerSync, JoinGameListener, PluginChannelListener {

    private ClientSyncManager client;

    @Override
    public String getName() {
        return "PlayerSync";
    }

    @Override
    public String getVersion() {
        return "0.1-SNAPSHOT";
    }

    @Override
    public void init(File configPath) {
        this.client = new ClientSyncManager();
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {
    }

    @Override
    public SyncManager getPlayerSyncManager() {
        return client;
    }

    @Override
    public void onJoinGame(INetHandler netHandler, SPacketJoinGame joinGamePacket, ServerData serverData, RealmsServer realmsServer) {

        this.client.setClient((NetHandlerPlayClient) netHandler);
    }

    @Override
    public List<String> getChannels() {
        return ImmutableList.of(CHANNEL);
    }

    @Override
    public void onCustomPayload(String channel, PacketBuffer data) {
        if (CHANNEL.equals(channel))
            this.client.onPayload(data);
    }

}
