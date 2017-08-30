package playersync.data.server;

import java.util.List;

public interface IRegisterData {

    int getVersion();

    List<String> getChannels();
}
