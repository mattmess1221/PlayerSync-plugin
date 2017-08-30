package playersync.bukkit;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import playersync.adapters.Adapter;
import playersync.adapters.PlayerAdapter;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Matthew on 6/25/2017.
 */
public class BukkitPlayerAdapter implements PlayerAdapter<Player>  {
    @Override
    public TypeToken<? extends Adapter<Player>> getType() {
        return TypeToken.of(BukkitPlayerAdapter.class);
    }

    @Override
    public UUID getUniqueId(Player player) {
        return player.getUniqueId();
    }

    @Override
    public Optional<Player> getPlayer(UUID uuid) {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }
}
