package model;

public class Peer {
    
    Metadata metadata;
    
    Address address;
    
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
}
