package threads;

import java.net.DatagramSocket;
import java.util.List;

import model.Address;
import model.Peer;
import services.LotteryService;
import services.MessageHandler;
import services.MetadataSenderService;

public class MyMetaDataSenderThread extends AbstractThread {

    private final static int TIMEOUT = 6000;
    private List<Address> peerAddresses;
    private DatagramSocket socket;

    MessageHandler messageHandler = new MessageHandler();

    public MyMetaDataSenderThread(DatagramSocket socket, Peer iPeer, List<Address> peersAddresses) {
        super(iPeer);
        this.peerAddresses = peersAddresses;
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {

            // sorteia um peer para enviar metadados
            Address addressToSend;
            do {
                addressToSend = peerAddresses.get(LotteryService.getRandomInt(peerAddresses.size()));
            } while (addressToSend.equals(getPeer().getAddress()));

            try {
                if (getPeer().getMetadata() != null) {
                    MetadataSenderService.sendMessage(socket, messageHandler.stringfyPeer(getPeer()), addressToSend);
                    ThreadLog("Metadados enviados para " + addressToSend);
                }

                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getThreadName() {
        return "MyMetadataThreadName";
    }

}
