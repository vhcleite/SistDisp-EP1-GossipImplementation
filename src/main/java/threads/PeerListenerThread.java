package threads;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import model.Peer;

public class PeerListenerThread extends AbstractThread {

    public final static int BUFFER_SIZE = 65535;
    DatagramSocket socket;

    public PeerListenerThread(Peer iPeer, DatagramSocket socket) {
        super(iPeer);
        this.socket = socket;
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

                ThreadLog("Recebido - " + message);

                // Clear the buffer after every message.
                receiveByteArray = new byte[BUFFER_SIZE];
            }
        } catch (Exception e) {
            ThreadLog("Erro");
        }
    }

    public String getThreadName() {
        return "PeerListenerThread";
    }
}
