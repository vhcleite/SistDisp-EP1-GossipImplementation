package threads;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import model.Peer;
import model.PeerRecord;

public class PeerMessageHandlerThread extends AbstractThread {

    private Peer recievedPeer;
    private ArrayList<PeerRecord> peerRecords;
    private Semaphore semaphore;

    public PeerMessageHandlerThread(Peer iPeer, Peer recievedPeer, ArrayList<PeerRecord> peerRecords,
            Semaphore semaphore) {
        super(iPeer);
        this.recievedPeer = recievedPeer;
        this.peerRecords = peerRecords;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        if (!recievedPeer.getAddress().equals(getPeer().getAddress())) {

            PeerRecord newPeerRecord = new PeerRecord(recievedPeer, new Date());

            Iterator<PeerRecord> it = peerRecords.iterator();

            while (it.hasNext()) {
                PeerRecord savedPeerRecord = it.next();
                if (newPeerRecord.getPeer().getAddress().equals(savedPeerRecord.getPeer().getAddress())) {
                    // encontrou registro gravado do recievedPeer recebido
                    if (savedPeerRecord.getReceivingDate() == null //
                            || newPeerRecord.getPeer().getMetadata() == null //
                            || newPeerRecord.getPeer().getMetadata()
                                    .isYoungerThan(savedPeerRecord.getPeer().getMetadata())) {
                        savedPeerRecord.setPeer(newPeerRecord.getPeer());
                        savedPeerRecord.setReceivingDate(newPeerRecord.getReceivingDate());
                        ThreadLog("Atualizado registro para:\r\n" + savedPeerRecord.toString());
                    } else {
                        ThreadLog("Metadados nao atualizados por serem mais antigos ou duplicados:\r\n"
                                + savedPeerRecord.toString());
                    }
                    return;
                }
            }

            // não encontrou registro com o endereço do peerRecord. Basta adicionar
            try {
                semaphore.acquire();
                ThreadLog("Criado novo registro para: " + newPeerRecord.getPeer().getAddress().toString());
                peerRecords.add(newPeerRecord);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }

        } else {
            ThreadLog("");
        }
    }

    @Override
    public String getThreadName() {
        return PeerMessageHandlerThread.class.getSimpleName();
    }

}
