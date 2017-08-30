package playersync.bukkit.data;

import org.bukkit.entity.Player;
import playersync.data.Data;

import java.nio.ByteBuffer;

/**
 * Created by Matthew on 6/25/2017.
 */
public interface BukkitData<T> extends Data<ByteBuffer> {

    void handle(Player player, T handler);

}
