package playersync.bukkit.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface ServerData {
    void write(ByteArrayOutputStream buffer) throws IOException;
}
