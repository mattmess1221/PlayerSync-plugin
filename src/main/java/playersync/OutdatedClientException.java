package playersync;

/**
 * Created by Matthew on 6/25/2017.
 */
public class OutdatedClientException extends Exception {

    private int version;

    public OutdatedClientException(int version) {
        super();
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
