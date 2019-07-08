package services;

import model.Address;
import model.Peer;
import model.PeerRecord;

import java.util.ArrayList;
import java.util.List;

public class NeighborsMetaDataSenderThread extends Thread {

    private final static int TIMEOUT = 3000;
    private List<PeerRecord> peerRecords;
    private List<Address> peersAddresses;
    private Peer iPeer;

    public NeighborsMetaDataSenderThread(Peer iPeer,ArrayList<PeerRecord> peerRecords, List<Address> peersAddresses) {
        this.peerRecords = peerRecords;
        this.peersAddresses = peersAddresses;
        this.iPeer = iPeer;
    }

    @Override
    public void run() {
        while (true) {
            boolean isSamePeer = true;
            PeerRecord recordToSend = null;
            Address peerToSend = null;

            while (isSamePeer) {
                //sorteia um peer para enviar
                int peerToSendIndex = LotteryPeerService.chooseRandomPeerIndex(peersAddresses.size());
                peerToSend = peersAddresses.get(peerToSendIndex);

                //sorteia um peer para pegar os metadados
                int recordToSendIndex = LotteryPeerService.chooseRandomPeerIndex(peerRecords.size());
                recordToSend = peerRecords.get(recordToSendIndex);

                // verifica se não são os mesmos peers
                isSamePeer = isSamePeer(peerToSend, recordToSend);
            }

            // cria json
            MessageHandler messageHandler = new MessageHandler();
            String jsonPeer = messageHandler.stringfyPeer(recordToSend.getPeer());

            //enviar os metadados
            MetadataSenderService metadataSenderService = new MetadataSenderService(iPeer);
            try {
                metadataSenderService.sendMessage(jsonPeer, peerToSend);
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean isSamePeer(Address peerToSend, PeerRecord recordToSend) {
        return recordToSend.getPeer().getAddress().getPort() == peerToSend.getPort();
    }
}
