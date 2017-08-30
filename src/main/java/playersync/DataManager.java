package playersync;

import java.util.UUID;

public interface DataManager<Message> {

    void sendData(UUID uuid, Message message);
}
