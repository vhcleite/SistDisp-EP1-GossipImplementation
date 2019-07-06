package services;

import java.util.ArrayList;
import java.util.Random;

import model.Address;
import model.Peer;
import model.PeerRecord;

public class PeerClient {

    private Peer iPeer = new Peer(new Address("localhost", 9000));
    
    private ArrayList<PeerRecord> peerRecords = new ArrayList<PeerRecord>();
    
    /**
     * returna numero de peerRecords mantidos na lista
     * @return
     */
    private int getPeerRecordsSize() {
        return peerRecords.size();
    }

    /**
     * Devolve o PeerRecord da posição especificada
     * @param nextInt
     * @return
     */
    private PeerRecord getPeerRecordEntity(int nextInt) {
        return peerRecords.get(nextInt);
    }

    /**
     * Escolhe um PeerRecord da lista de forma aleatória
     */
    public PeerRecord getRandomPeerRecord() {
        
        if(getPeerRecordsSize() == 0) {
            return null;
        }
        
        Random rand = new Random();
        return getPeerRecordEntity(rand.nextInt(getPeerRecordsSize()));
    }
    
    public static void main(String args[]) {
        
        
        
        
        
        
    }


    
}
