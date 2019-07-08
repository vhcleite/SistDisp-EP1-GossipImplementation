package services;

import model.PeerRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class MetaDataVerifierThread extends Thread {

    private static final int THREAD_TIMEOUT = 3000;
    private static final int METADA_TIMEOUT = 5000;

    private ArrayList<PeerRecord> peerRecords = new ArrayList<PeerRecord>();

    public MetaDataVerifierThread(ArrayList<PeerRecord> peerRecords) {
        this.peerRecords = peerRecords;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Date currentDate = new Date();

                Iterator<PeerRecord> iterator = peerRecords.iterator();
                while (iterator.hasNext()) {
                    PeerRecord peerRecord = iterator.next();

                    boolean isValidRecord = peerRecord.isValid(currentDate, METADA_TIMEOUT);
                    // se nao eh valido remove o registro
                    if (!isValidRecord) {
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
