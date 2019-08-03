package services;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import model.Address;
import model.Peer;
import model.PeerRecord;
import model.Query;
import threads.MetadataBuilderThread;
import threads.MetadataVerifierThread;
import threads.MyMetadataSenderThread;
import threads.NeighborsMetadataSenderThread;
import threads.PeerListenerThread;

public class PeerClient {

    public String localHost;
    public DatagramSocket socket;
    public List<Address> peerAddresses;

    private Peer iPeer;
    private ArrayList<PeerRecord> peerRecords = new ArrayList<PeerRecord>();
    private ArrayList<Query> queriesDone = new ArrayList<>();

    public PeerClient(Integer localPeerPort, String remotePeersList) {
        initSocket(localPeerPort);
        initPeer(localPeerPort);
        initPeerRecords(remotePeersList);
    }

    private void initPeer(Integer localPeerPort) {
        try {
            this.localHost = InetAddress.getLocalHost().getHostAddress();
            this.iPeer = new Peer(localHost, localPeerPort);
            String home = System.getProperty("user.home");
            String gossipFolder = home + "/" + localPeerPort;
            new File(gossipFolder).mkdirs();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void initPeerRecords(String remotePeersList) {

        List<Address> addresses = new ArrayList<>();
        String[] split = remotePeersList.split(",");

        for (String address : split) {
            String[] addressArray = address.split(":");
            addresses.add(new Address(addressArray[0], Integer.valueOf(addressArray[1])));
        }

        this.peerAddresses = addresses;

        for (Address address : this.peerAddresses) {
            this.peerRecords.add(new PeerRecord(address.getIp(), address.getPort()));
        }
    }

    private void initSocket(Integer localPeerPort) {
        try {
            this.socket = new DatagramSocket(localPeerPort);
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("Não foi possível inicializar o socket");
        }
    }

    public static void main(String args[]) {

        if (args.length != 2) {
            System.out.println("Os argumento são: ");
            System.out.println("(1) porta do peer local");
            System.out.println("(2) lista de ip1:porta1,ip2:porta2 separados por vírgulas dos peers remotos");
        }

        Integer localPeerPort = Integer.valueOf(args[0]);
        String remotePeersList = args[1];

        PeerClient client = new PeerClient(localPeerPort, remotePeersList);

        // Para thread funcionar é esperado que exista a pasta gossip_test_folder na
        // home do usuario
        MetadataBuilderThread builderThread = new MetadataBuilderThread(client.iPeer);
        builderThread.start();

        // Thread responsavel por escutar mensagens
        PeerListenerThread peerListenerThread = new PeerListenerThread(client.iPeer, client.socket, client.peerRecords,
                client.queriesDone);
        peerListenerThread.start();

        // Thread responsavel por enviar os meus metadados para os peers vizinhos
        MyMetadataSenderThread myMetadataSenderThread = new MyMetadataSenderThread(client.socket, client.iPeer,
                client.peerAddresses);
        myMetadataSenderThread.start();

        // Thread responsavel por enviar os metadados de peers vizinhos para outros
        // peers vizinhos
        NeighborsMetadataSenderThread neighborsMetadataSenderThread = new NeighborsMetadataSenderThread(client.iPeer,
                client.socket, client.peerRecords, client.peerAddresses);
        neighborsMetadataSenderThread.start();

        // Thread responsavel por enviar os de peers vizinhos para os peers vizinhos
        MetadataVerifierThread metaDataVerifierThread = new MetadataVerifierThread(client.iPeer, client.peerRecords);
        metaDataVerifierThread.start();
    }

}
