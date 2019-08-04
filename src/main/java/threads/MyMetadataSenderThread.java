package threads;

import java.net.DatagramSocket;
import java.util.List;

import model.Address;
import model.Message;
import model.MessageType;
import model.Peer;
import services.LotteryService;
import services.MessageHandler;
import services.MessageSenderService;

public class MyMetadataSenderThread extends AbstractThread {

    private final static int TIMEOUT = 6000;
    private List<Address> peerAddresses;
    private DatagramSocket socket;

    MessageHandler messageHandler = new MessageHandler();

    public MyMetadataSenderThread(DatagramSocket socket, Peer iPeer, List<Address> peersAddresses) {
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

                    Message message = new Message(MessageType.PEER, messageHandler.stringfy(getPeer()));

                    MessageSenderService.sendMessage(socket, messageHandler.stringfy(message), addressToSend);
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
        return MyMetadataSenderThread.class.getSimpleName();
    }

}
