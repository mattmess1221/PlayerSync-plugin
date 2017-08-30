package playersync;

import playersync.data.Data;

public interface Channels<Player, Buffer> {

    String CHANNEL = "pSync";

    void sendData(Player uuid, Data<Buffer> data);
}
