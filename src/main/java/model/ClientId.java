package model;

import java.util.Date;
import java.util.Objects;

public class ClientId {
    private Address address;
    private long timestamp;

    public ClientId(Address address) {
        this.address = address;
        this.timestamp = new Date().getTime();
    }

    public String getToken() {
        return String.format("%s:%s:%s", this.address.getIp(), this.address.getPort(), this.getTimestamp());
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClientId clientId = (ClientId) o;
        return Objects.equals(address, clientId.address) && Objects.equals(timestamp, clientId.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, timestamp);
    }

    @Override
    public String toString() {
        return "ClientId [address=" + address + ", timestamp=" + timestamp + "]";
    }
}
