package playersync.data;

import java.io.IOException;

/**
 * Created by Matthew on 6/25/2017.
 */
public interface Data<Buffer> {

    void read(Buffer buffer) throws IOException;

    void write(Buffer buffer) throws IOException;

}
