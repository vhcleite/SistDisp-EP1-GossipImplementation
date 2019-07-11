package threads;

import java.util.List;
import java.util.Objects;

import model.Peer;
import model.PeerRecord;

public abstract class AbstractThread extends Thread {

    private Peer iPeer;

    public AbstractThread(Peer iPeer) {
        setPeer(iPeer);
    }

    public Peer getPeer() {
        return iPeer;
    }

    public void setPeer(Peer peer) {
        this.iPeer = peer;
    }

    protected void ThreadLog(String log) {
        System.out.println(getIdentifier() + log);
    }

    private String getIdentifier() {
        return String.format("[%s] %s: ", getPeer().getAddress(), getThreadName());
    }

    protected static void logPeerRecords(List<PeerRecord> peerRecords) {
        System.out.println(
                "========================================================== PEER RECORDS ==========================================================");

        for (PeerRecord peerRecord : peerRecords) {
            System.out.println(">>>>>>>>>>> " + Objects.toString(peerRecord));
            System.out.println();
        }

        System.out.println(
                "=================================================================================================================================");
    }

    abstract public String getThreadName();
}
