package threads;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import model.Address;
import model.Message;
import model.MessageType;
import model.Peer;
import model.PeerRecord;
import model.Query;
import services.LotteryService;
import services.MessageHandler;
import services.MessageSenderService;

public class ClientQueryHandlerThread extends AbstractThread {

    private ArrayList<Query> queriesDone;
    private Query query;
    private Semaphore semaphore;
    private ArrayList<PeerRecord> peerRecords;
    private DatagramSocket socket;

    public ClientQueryHandlerThread(DatagramSocket socket, Peer iPeer, ArrayList<PeerRecord> peerRecords,
            ArrayList<Query> queries, Query query, Semaphore semaphore) {
        super(iPeer);
        this.socket = socket;
        this.peerRecords = peerRecords;
        this.queriesDone = queries;
        this.query = query;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        // TODO verificar se eh necessario implementar um contains
        if (!queriesDone.contains(query)) {
            ThreadLog(String.format("Recebida query [%s]", query));
            Address clientAddress = query.getClientId().getAddress();

            query.decreaseTtl();

            String queryFile = query.getFile();
            Peer iPeer = getPeer();

            if (iPeer.hasFile(queryFile)) {
                sendFileToClient(clientAddress, queryFile, iPeer);
            } else if (query.getTtl() > 0) {
                fowardQuery(queryFile, iPeer);
            } else {
                ThreadLog(String.format("TTL da query zerado. Não repassando query"));
            }

        } else {
            ThreadLog(String.format("A query %s ja foi realizada previamente", query.getClientId().getToken()));
        }
    }

    private void fowardQuery(String queryFile, Peer iPeer) {
        Address nextPeerAddress = getNextPeer(queryFile, iPeer);
        MessageHandler messageHandler = new MessageHandler();
        Message message = new Message(MessageType.CLIENT, messageHandler.stringfy(query));

        try {
            MessageSenderService.sendMessage(socket, messageHandler.stringfy(message), nextPeerAddress);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendFileToClient(Address clientAddress, String queryFile, Peer iPeer) {
        try {
            Socket socket = new Socket(InetAddress.getByName(clientAddress.getIp()), clientAddress.getPort());

            File file = getFile(queryFile, iPeer);
            FileInputStream fileStream = new FileInputStream(file);

            byte[] byteArray = new byte[(int) file.length()];
            BufferedInputStream bufferedStream = new BufferedInputStream(fileStream);
            bufferedStream.read(byteArray, 0, byteArray.length);

            OutputStream os = socket.getOutputStream();
            os.write(byteArray, 0, byteArray.length);
            ThreadLog(String.format("Enviando arquivo %s para o client %s", queryFile, clientAddress.toString()));
            os.flush();
            socket.close();

            ThreadLog(String.format("Arquivo %s enviado com sucesso!", queryFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFile(String queryFile, Peer iPeer) {
        String home = System.getProperty("user.home");
        String gossipFolder = home + "/" + iPeer.getAddress().getIp() + "/" + queryFile;
        return new File(gossipFolder);
    }

    private Address getNextPeer(String queryFile, Peer iPeer) {
        Address peerHasFileAddress = null;
        try {
            // Colocado semaforo para que thread possa consultar lista de peer vizinhos sem
            // ocorrer concorrência com outras threads
            semaphore.acquire();

            for (PeerRecord pr : peerRecords) {
                if (pr.getPeer().hasFile(queryFile)) {
                    ThreadLog(String.format("Encontrado peer [%s] com arquivo [%s] na lista de peer vizinhos",
                            pr.getPeer().getAddress(), queryFile));

                    peerHasFileAddress = pr.getPeer().getAddress();
                    break;
                }
            }

            if (peerHasFileAddress == null) {
                while (peerHasFileAddress.equals(iPeer.getAddress())) {
                    peerHasFileAddress = peerRecords.get(LotteryService.getRandomInt(peerRecords.size())).getPeer()
                            .getAddress();
                }
                ThreadLog(String.format("Escolhido peer aleatorio [%s] para encaminhar query de arquivo[%s]",
                        peerHasFileAddress.toString(), queryFile));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
        return peerHasFileAddress;
    }

    @Override
    public String getThreadName() {
        return ClientQueryHandlerThread.class.getSimpleName();
    }
}
