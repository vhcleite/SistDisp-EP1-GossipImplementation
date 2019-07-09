package threads;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import model.Peer;
import model.PeerRecord;
import services.MessageHandler;

public class PeerListenerThread extends AbstractThread {

    public final static int BUFFER_SIZE = 65535;

    ArrayList<PeerRecord> peerRecords;
    DatagramSocket socket;

    public PeerListenerThread(Peer iPeer, DatagramSocket socket, ArrayList<PeerRecord> peerRecords) {
        super(iPeer);
        this.socket = socket;
        this.peerRecords = peerRecords;
    }

    @Override
    public void run() {
        try {
            byte[] receiveByteArray = new byte[BUFFER_SIZE];

            DatagramPacket receiveDatagram = null;
            while (true) {

                receiveDatagram = new DatagramPacket(receiveByteArray, receiveByteArray.length);

                ThreadLog("Esperando pacote");
                socket.receive(receiveDatagram);

                String message = new String(receiveDatagram.getData());

                ThreadLog(String.format("Recebido[size %d][%s]", message.length(), message));

                MessageHandler messageHandler = new MessageHandler();
                Peer peer = messageHandler.parseString(getValidJsonString(message));
                handlePeer(peer);

                // Clear the buffer after every message.
                receiveByteArray = new byte[BUFFER_SIZE];
            }
        } catch (Exception e) {
            ThreadLog("Erro em " + getThreadName());
            e.printStackTrace();
        }
    }

    private String getValidJsonString(String message) {
        int index = 0;
        while (message.charAt(index) != '\0') {
            index++;
        }
        return message.substring(0, index);
    }

    private void handlePeer(Peer peer) {

        PeerRecord newPeerRecord = new PeerRecord(peer, new Date());

        Iterator<PeerRecord> it = peerRecords.iterator();

        while (it.hasNext()) {
            PeerRecord savedPeerRecord = it.next();
            if (newPeerRecord.getPeer().getAddress().equals(savedPeerRecord.getPeer().getAddress())) {
                // encontrou registro gravado do peer recebido
                if (savedPeerRecord.getPeer().getMetadata() == null || //
                        newPeerRecord.getPeer().getMetadata().isYoungerThan(savedPeerRecord.getPeer().getMetadata())) {
                    // nunca foram gravados dados. Gravar pela primeira vez
                    savedPeerRecord.setPeer(newPeerRecord.getPeer());
                    savedPeerRecord.setReceivingDate(newPeerRecord.getReceivingDate());

                    ThreadLog("Salvando novos metadados para PeerRecord: "
                            + savedPeerRecord.getPeer().getAddress().toString());
                }
                return;
            }
        }

        // não encontrou registro com o endereço do peerRecord. Basta adicionar
        peerRecords.add(newPeerRecord);
    }

    public String getThreadName() {
        return "PeerListenerThread";
    }
}
