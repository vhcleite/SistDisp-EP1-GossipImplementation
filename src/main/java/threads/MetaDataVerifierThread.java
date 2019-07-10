package threads;

import java.util.ArrayList;
import java.util.Iterator;

import model.Peer;
import model.PeerRecord;

public class MetaDataVerifierThread extends AbstractThread {

    private static final int THREAD_TIMEOUT = 3000;
    private static final int METADATA_EXPIRATION_TIME = 10000;

    private ArrayList<PeerRecord> peerRecords;

    public MetaDataVerifierThread(Peer iPeer, ArrayList<PeerRecord> peerRecords) {
        super(iPeer);
        this.peerRecords = peerRecords;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Iterator<PeerRecord> iterator = peerRecords.iterator();
                while (iterator.hasNext()) {
                    PeerRecord peerRecord = iterator.next();

                    if (peerRecord.isExpired(METADATA_EXPIRATION_TIME)) {
                        // se o registro esta expirado deve ser removido
                        iterator.remove();
                        ThreadLog(String.format("Registro %s removido", peerRecord.getPeer().getAddress()));
                    }

                }
                Thread.sleep(THREAD_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public String getThreadName() {
        return "MetadataVerifierThread";
    }
}
