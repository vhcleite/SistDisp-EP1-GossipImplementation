package threads;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

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
    private Semaphore semaphore;

    MessageHandler messageHandler = new MessageHandler();

    public NeighborsMetadataSenderThread(Peer iPeer, DatagramSocket socket, ArrayList<PeerRecord> peerRecords,
                                         List<Address> peersAddresses, Semaphore semaphore) {
        super(iPeer);
        this.peerRecords = peerRecords;
        this.peerAddresses = peersAddresses;
        this.socket = socket;
        this.semaphore = semaphore;
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
                try {
                    semaphore.acquire();
                    if(!peerRecords.isEmpty()) {
                        recordToSend = peerRecords.get(LotteryService.getRandomInt(peerRecords.size()));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();
                }

            } while (addressToSend.equals(recordToSend.getPeer().getAddress()) //
                    || addressToSend.equals(getPeer().getAddress()) //
                    || (recordToSend.getPeer().getMetadata() == null));

            try {
                if (getPeer().getMetadata() != null) {
                    Message message = new Message(MessageType.METADATA, messageHandler.stringfy(recordToSend.getPeer()));
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
