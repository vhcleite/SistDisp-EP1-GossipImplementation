package model;

import java.util.Date;

public class PeerRecord {

    Peer peer;

    Date receivingDate;

    public PeerRecord(String ip, int port) {
        peer = new Peer(ip, port);
        receivingDate = null;
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

        Date now = new Date();

        long difference = now.getTime() - this.receivingDate.getTime();
        return difference > expirationTime;
    }

}
