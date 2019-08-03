package model;

import java.util.Date;
import java.util.Objects;

public class ClientId {
    private Address address;
    private Date timestamp;

    public ClientId(Address address, Date timestamp) {
        this.address = address;
        this.timestamp = timestamp;
    }

    public String getToken() {
        return String.format("%s:%s:%s", this.address.getIp(), this.address.getPort(), this.timestamp.getTime());
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
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
