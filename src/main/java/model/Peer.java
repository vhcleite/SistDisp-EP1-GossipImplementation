package model;

import java.util.Objects;

public class Peer {

    Address address;
    Metadata metadata;

    public Peer(String ip, int port) {
        super();
        this.address = new Address(ip, port);
        this.metadata = null;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String toString() {
        return "ADDRESS -> " + getAddress().toString() + "/\r\n                     " + Objects.toString(getMetadata());
    }

    public boolean hasFile(String file) {
        if (metadata == null) {
            return false;
        }
        return this.metadata.getFolderContent().contains(file);
    }

    public String getMonitoringFolderName() {
        return String.format("%s/peer-%s_%d-repository", System.getProperty("user.home"), getAddress().getIp(),
                getAddress().getPort());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Peer peer = (Peer) o;
        return address.equals(peer.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}
