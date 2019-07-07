package services;

import java.util.ArrayList;
import java.util.Random;

import model.Peer;
import model.PeerRecord;

public class PeerClient {

    public static final int MY_PORT = 9000;

    public static final int[] PEER_PORTS = { 9000, 9001, 9002, 9003, 9004 };

    public static final String LOCALHOST = "localhost";

    private Peer iPeer = new Peer(LOCALHOST, MY_PORT);

    private ArrayList<PeerRecord> peerRecords = new ArrayList<PeerRecord>();

    /**
     * returna numero de peerRecords mantidos na lista
     * 
     * @return
     */
    private int getPeerRecordsSize() {
        return peerRecords.size();
    }

    /**
     * Devolve o PeerRecord da posição especificada
     * 
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

        if (getPeerRecordsSize() == 0) {
            return null;
        }

        Random rand = new Random();
        return getPeerRecordEntity(rand.nextInt(getPeerRecordsSize()));
    }

    private static void initPeerRecords(PeerClient client) {

        for (int port : PEER_PORTS) {
            client.peerRecords.add(new PeerRecord(LOCALHOST, port));
        }

    }

    public static void main(String args[]) {

        PeerClient client = new PeerClient();
        initPeerRecords(client);

        // Para thread funcionar é esperado que exista a pasta gossip_test_folder na
        // home do usuario
        MetaDataBuilderThread builderThread = new MetaDataBuilderThread(client.iPeer);
        builderThread.start();
    }

}
