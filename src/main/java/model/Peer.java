package model;

public class Peer {
    
    Metadata metadata;
    
    Address address;
    
    public Peer(Address address) {
        super();
        this.address = address;
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
