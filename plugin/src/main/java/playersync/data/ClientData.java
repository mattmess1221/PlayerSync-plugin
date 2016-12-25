package playersync.data;

import java.util.Set;

public class ClientData {

    private String channel;
    private byte[] data;
    private Set<String> registrations;

    public ClientData() {

    }

    public ClientData(String channel, byte[] data) {
        this.channel = channel;
        this.data = data;
    }

    public ClientData(String channel, Set<String> registrations) {
        this.channel = channel;
        this.registrations = registrations;
    }

    protected void setChannel(String channel) {
        this.channel = channel;
    }

    protected void setData(byte[] data) {
        this.data = data;
    }

    protected void setRegistrations(Set<String> registrations) {
        this.registrations = registrations;
    }

    public String getChannel() {
        return channel;
    }

    public byte[] getData() {
        return data;
    }

    public Set<String> getRegistrations() {
        return registrations;
    }
}
