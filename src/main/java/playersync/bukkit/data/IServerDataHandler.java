package playersync.bukkit.data;

import org.bukkit.entity.Player;
import playersync.bukkit.data.client.SClientData;
import playersync.bukkit.data.client.SRegisterData;

/**
 * Created by Matthew on 6/25/2017.
 */
public interface IServerDataHandler {
    void handleRegisterData(Player player, SRegisterData sRegisterData);

    void handleClientData(Player player, SClientData sClientData);
}
