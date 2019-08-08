package services;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import model.Address;
import model.Peer;
import model.PeerRecord;
import model.Query;
import threads.MetadataBuilderThread;
import threads.MetadataVerifierThread;
import threads.MyMetadataSenderThread;
import threads.NeighborsMetadataSenderThread;
import threads.PeerListenerThread;

import static resources.PeerAddressesList.*;

public class PeerExecutor {

    public static Semaphore semaphore;
    public String localHost;
    public DatagramSocket socket;
    public List<Address> peerAddresses;

    private Peer iPeer;
    private ArrayList<PeerRecord> peerRecords = new ArrayList<PeerRecord>();
    private ArrayList<Query> queriesDone = new ArrayList<>();

    public PeerExecutor(Address localAddr, String remotePeersList) {
        initSocket(localAddr);
        initPeer(localAddr);
        initPeerRecords(remotePeersList);
        this.semaphore = new Semaphore(1);
    }

    private void initPeer(Address localAddr) {
        try {
            this.localHost = InetAddress.getLocalHost().getHostAddress();
            this.iPeer = new Peer(localAddr.getIp(), localAddr.getPort());
            new File(iPeer.getMonitoringFolderName()).mkdirs();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void initPeerRecords(String remotePeersList) {

        List<Address> addresses = getAddressesFromString(remotePeersList);

        this.peerAddresses = addresses;

        for (Address address : this.peerAddresses) {
            this.peerRecords.add(new PeerRecord(address.getIp(), address.getPort()));
        }
    }



    private void initSocket(Address localAddr) {
        try {
            this.socket = new DatagramSocket(localAddr.getPort());
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
            System.exit(0);
        }

        Address localAddr = getAddressFromString(args[0]);
        String remotePeersList = args[1];

        PeerExecutor client = new PeerExecutor(localAddr, remotePeersList);

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
                client.socket, client.peerRecords, client.peerAddresses, semaphore);
        neighborsMetadataSenderThread.start();

        // Thread responsavel por enviar os de peers vizinhos para os peers vizinhos
        MetadataVerifierThread metaDataVerifierThread = new MetadataVerifierThread(client.iPeer, client.peerRecords,
                semaphore);
        metaDataVerifierThread.start();
    }

}
