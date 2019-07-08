package services;

import java.util.ArrayList;
import java.util.List;

import model.Address;
import model.Peer;
import model.PeerRecord;

public class NeighborsMetaDataSenderThread extends Thread {

    private final static int TIMEOUT = 3000;
    private List<PeerRecord> peerRecords;
    private List<Address> peerAddresses;
    private Peer iPeer;

    MessageHandler messageHandler = new MessageHandler();
    MetadataSenderService metadataSenderService = new MetadataSenderService(iPeer);

    public NeighborsMetaDataSenderThread(Peer iPeer, ArrayList<PeerRecord> peerRecords, List<Address> peersAddresses) {
        this.peerRecords = peerRecords;
        this.peerAddresses = peersAddresses;
        this.iPeer = iPeer;
    }

    @Override
    public void run() {
        while (true) {

            Address addressToSend = null;
            PeerRecord recordToSend = null;

            do {
                // sorteia um peer para enviar metadados
                addressToSend = peerAddresses.get(LotteryService.getRandomInt(peerAddresses.size()));

                // sorteia um peer para pegar os metadados
                recordToSend = peerRecords.get(LotteryService.getRandomInt(peerRecords.size()));
            } while (addressToSend.equals(recordToSend.getPeer().getAddress()));

            try {
                metadataSenderService.sendMessage(//
                        messageHandler.stringfyPeer(recordToSend.getPeer()), addressToSend);

                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
