package services;

import java.util.ArrayList;
import java.util.Iterator;

import model.PeerRecord;

public class MetaDataVerifierThread extends Thread {

    private static final int THREAD_TIMEOUT = 3000;
    private static final int METADATA_EXPIRATION_TIME = 5000;

    private ArrayList<PeerRecord> peerRecords = new ArrayList<PeerRecord>();

    public MetaDataVerifierThread(ArrayList<PeerRecord> peerRecords) {
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
                        // se nao eh valido remove o registro
                        iterator.remove();
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
}
