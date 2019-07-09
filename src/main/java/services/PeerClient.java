package services;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Address;
import model.Peer;
import model.PeerRecord;
import threads.MetaDataBuilderThread;
import threads.MyMetaDataSenderThread;
import threads.PeerListenerThread;

public class PeerClient {

    public static final int MY_PORT = 9001;
    public static final String LOCALHOST = "127.0.0.1";
    public DatagramSocket socket;

    public static final List<Address> PEER_ADDRESSES = Arrays.asList(//
            new Address("127.0.0.1", 9000), //
            new Address("127.0.0.1", 9001)); //
//            new Address("127.0.0.1", 9002), //
//            new Address("127.0.0.1", 9003), //
//            new Address("127.0.0.1", 9002)); //

    private Peer iPeer = new Peer(LOCALHOST, MY_PORT);
    private ArrayList<PeerRecord> peerRecords = new ArrayList<PeerRecord>();

    public PeerClient() {
        initPeerRecords();
        initSocket();
    }

    private void initPeerRecords() {
        for (Address address : PEER_ADDRESSES) {
            this.peerRecords.add(new PeerRecord(address.getIp(), address.getPort()));
        }
    }

    private void initSocket() {
        try {
            this.socket = new DatagramSocket(MY_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("Não foi possível inicializar o socket");
        }
    }

    public static void main(String args[]) {

        PeerClient client = new PeerClient();

        // Para thread funcionar é esperado que exista a pasta gossip_test_folder na
        // home do usuario
        MetaDataBuilderThread builderThread = new MetaDataBuilderThread(client.iPeer);
        builderThread.start();

        // Thread responsavel por escutar mensagens
        PeerListenerThread peerListenerThread = new PeerListenerThread(client.iPeer, client.socket, client.peerRecords);
        peerListenerThread.start();

        // Thread responsavel por enviar os meus metadados para os peers vizinhos
        MyMetaDataSenderThread myMetadataSenderThread = new MyMetaDataSenderThread(client.socket, client.iPeer,
                PEER_ADDRESSES);
        myMetadataSenderThread.start();
    }

}
