package model;

import java.util.Date;

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
    public String toString() {
        return "ClientId [address=" + address + ", timestamp=" + timestamp + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientId other = (ClientId) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (timestamp != other.timestamp)
            return false;
        return true;
    }
}
