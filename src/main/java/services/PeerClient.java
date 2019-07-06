package services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import model.Address;
import model.Peer;
import model.PeerRecord;

public class PeerClient {
    
    public static final int MY_PORT = 9000;
    
    public static final int[] PEER_PORTS = {9000, 9001, 9002, 9003, 9004};
    
    public static final String LOCALHOST = "localhost";

    private Peer iPeer = new Peer(LOCALHOST, MY_PORT);
    
    private static ArrayList<PeerRecord> peerRecords = new ArrayList<PeerRecord>();
    
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
        
        PeerClient client= new PeerClient();
        initPeerRecords(client);
        
        
        MetaDataBuilderThread builderThread = new MetaDataBuilderThread();
        builderThread.run();
    }

    private static void initPeerRecords(PeerClient client) {
        
        for(int port : PEER_PORTS) {    
            client.peerRecords.add(new PeerRecord(LOCALHOST, port));
        }
        
    }


    
}
