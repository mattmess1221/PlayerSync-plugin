package playersync.sponge;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import playersync.adapters.Adapter;
import playersync.adapters.PlayerAdapter;

import java.util.Optional;
import java.util.UUID;

public class SpongePlayerAdapter implements PlayerAdapter<Player> {

    @Override
    public TypeToken<? extends Adapter<Player>> getType() {
        return TypeToken.of(SpongePlayerAdapter.class);
    }

    @Override
    public UUID getUniqueId(Player player) {
        return player.getUniqueId();
    }

    @Override
    public Optional<Player> getPlayer(UUID uuid) {
        return Sponge.getServer().getPlayer(uuid);
    }
}
