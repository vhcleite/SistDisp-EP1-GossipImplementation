package threads;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import model.Peer;
import model.PeerRecord;

public class MetadataVerifierThread extends AbstractThread {

    private static final int THREAD_TIMEOUT = 3000;
    private static final int METADATA_EXPIRATION_TIME = 10000;
    private Semaphore semaphore;
    private ArrayList<PeerRecord> peerRecords;

    public MetadataVerifierThread(Peer iPeer, ArrayList<PeerRecord> peerRecords, Semaphore semaphore) {
        super(iPeer);
        this.peerRecords = peerRecords;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        while (true) {

                Iterator<PeerRecord> iterator = peerRecords.iterator();
                while (iterator.hasNext()) {
                    PeerRecord peerRecord = iterator.next();

                    if (peerRecord.isExpired(METADATA_EXPIRATION_TIME)) {
                        // se o registro esta expirado deve ser removido
                        try {
                            semaphore.acquire();
                            iterator.remove();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally {
                            semaphore.release();
                        }
                        ThreadLog(String.format("Registro %s removido", peerRecord.getPeer().getAddress()));

                        logPeerRecords(peerRecords);
                    }

                }
            try {
                Thread.sleep(THREAD_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    public String getThreadName() {
        return MetadataVerifierThread.class.getSimpleName();
    }
}
