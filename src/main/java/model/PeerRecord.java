package model;

import java.util.Date;
import java.util.Objects;

public class PeerRecord {

    Peer peer;

    Date receivingDate;

    public PeerRecord(String ip, int port) {
        peer = new Peer(ip, port);
        receivingDate = null;
    }

    public PeerRecord(Peer peer, Date now) {
        this.peer = peer;
        this.receivingDate = now;
    }

    public Peer getPeer() {
        return peer;
    }

    public void setPeer(Peer peer) {
        this.peer = peer;
    }

    public Date getReceivingDate() {
        return receivingDate;
    }

    public void setReceivingDate(Date receivingDate) {
        this.receivingDate = receivingDate;
    }

    public boolean isExpired(int expirationTime) {

        if (getReceivingDate() == null) {
            return false;
        }

        Date now = new Date();

        long difference = now.getTime() - this.receivingDate.getTime();
        return difference > expirationTime;
    }

    public String toString() {
        return "RECEIVING DATE -> " + Objects.toString(getReceivingDate()) + "/ " + Objects.toString(getPeer());
    }

}
