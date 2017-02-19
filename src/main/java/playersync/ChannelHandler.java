package playersync;

import org.spongepowered.api.Platform;
import org.spongepowered.api.network.RemoteConnection;
import playersync.data.SClientData;
import playersync.data.SRegisterData;

public interface ChannelHandler {

    void handleSyncPacket(SClientData m, RemoteConnection remoteConnection, Platform.Type type);

    void handleRegisterPacket(SRegisterData m, RemoteConnection remoteConnection, Platform.Type type);
}
