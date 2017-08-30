package playersync.adapters;

import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Matthew on 6/25/2017.
 */
public interface PlayerAdapter<Player> extends Adapter<Player> {

    UUID getUniqueId(Player player);

    Optional<Player> getPlayer(UUID uuid);

    static <Player> TypeToken<PlayerAdapter<Player>> token() {
        return Adapters.getType(PlayerAdapter.class);
    }
}
