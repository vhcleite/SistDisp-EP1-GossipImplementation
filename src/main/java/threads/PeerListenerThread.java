package threads;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import model.Message;
import model.MessageType;
import model.Peer;
import model.PeerRecord;
import model.Query;
import services.MessageHandler;

public class PeerListenerThread extends AbstractThread {

    public final static int BUFFER_SIZE = 65535;

    private ArrayList<PeerRecord> peerRecords;
    private ArrayList<Query> queriesDone;
    private DatagramSocket socket;

    private Semaphore semaphore;

    public PeerListenerThread(Peer iPeer, DatagramSocket socket, ArrayList<PeerRecord> peerRecords,
            ArrayList<Query> queriesDone) {
        super(iPeer);
        this.socket = socket;
        this.peerRecords = peerRecords;
        this.queriesDone = queriesDone;
        this.semaphore = new Semaphore(1);
    }

    @Override
    public void run() {
        try {
            byte[] receiveByteArray = new byte[BUFFER_SIZE];

            DatagramPacket receiveDatagram = null;
            while (true) {

                receiveDatagram = new DatagramPacket(receiveByteArray, receiveByteArray.length);

                ThreadLog("Escutando...");
                socket.receive(receiveDatagram);

                String messageString = new String(receiveDatagram.getData());

                ThreadLog(String.format("Recebido de %s:%d [%s]", receiveDatagram.getAddress().getHostAddress(),
                        receiveDatagram.getPort(), messageString));

                MessageHandler messageHandler = new MessageHandler();

                String validJsonString = messageHandler.getValidJsonString(messageString);
                Message message = messageHandler.parseMessage(validJsonString);
                if (message != null) {
                    if (message.getType() == MessageType.METADATA) {
                        String content = message.getContent();
                        Peer recievedPeer = messageHandler.parsePeerMessage(content);
                        new PeerMessageHandlerThread(getPeer(), recievedPeer, peerRecords, semaphore).run();
                    } else if (message.getType() == MessageType.QUERY) {
                        String content = message.getContent();
                        Query query = messageHandler.parseQueryMessage(content);
                        new ClientQueryHandlerThread(socket, getPeer(), peerRecords, queriesDone, query, semaphore)
                                .run();
                    }
                } else {
                    ThreadLog("Mensagem mal formatada");
                }

                // Clear the buffer after every message.
                receiveByteArray = new byte[BUFFER_SIZE];
                logPeerRecords(peerRecords);
            }
        } catch (Exception e) {
            ThreadLog("Erro em " + getThreadName());
            e.printStackTrace();
        }
    }

    public String getThreadName() {
        return PeerListenerThread.class.getSimpleName();
    }
}
