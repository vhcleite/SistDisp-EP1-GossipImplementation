package threads;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import model.Address;
import model.Message;
import model.MessageType;
import model.Peer;
import model.PeerRecord;
import services.LotteryService;
import services.MessageHandler;
import services.MessageSenderService;

public class NeighborsMetadataSenderThread extends AbstractThread {

    private final static int TIMEOUT = 3000;
    private List<PeerRecord> peerRecords;
    private List<Address> peerAddresses;
    private DatagramSocket socket;

    MessageHandler messageHandler = new MessageHandler();

    public NeighborsMetadataSenderThread(Peer iPeer, DatagramSocket socket, ArrayList<PeerRecord> peerRecords,
            List<Address> peersAddresses) {
        super(iPeer);
        this.peerRecords = peerRecords;
        this.peerAddresses = peersAddresses;
        this.socket = socket;
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
            } while (addressToSend.equals(recordToSend.getPeer().getAddress()) //
                    || addressToSend.equals(getPeer().getAddress()) //
                    || (recordToSend.getPeer().getMetadata() == null));

            try {
                if (getPeer().getMetadata() != null) {
                    Message message = new Message(MessageType.PEER, messageHandler.stringfy(recordToSend.getPeer()));
                    MessageSenderService.sendMessage(socket, messageHandler.stringfy(message), addressToSend);
                    ThreadLog(String.format("Metadados de %s enviados para %s", recordToSend.getPeer().getAddress(),
                            addressToSend));
                }

                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String getThreadName() {
        return NeighborsMetadataSenderThread.class.getSimpleName();
    }
}
