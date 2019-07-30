package threads;

import model.*;
import services.LotteryService;
import services.MessageHandler;
import services.MessageSenderService;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ClientMessageHandler extends AbstractThread {

    private ArrayList<Query> queriesDone;
    private Query query;
    private Semaphore semaphore;
    private ArrayList<PeerRecord> peerRecords;
    private DatagramSocket socket;

    public ClientMessageHandler(DatagramSocket socket, Peer iPeer, ArrayList<PeerRecord> peerRecords,
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
        if (!queriesDone.contains(query)) {
            Address clientAdrdress = query.getClientId().getAddress();

            query.decreaseTtl();

            String queryFile = query.getFile();
            Peer iPeer = getPeer();

            if (iPeer.hasFile(queryFile)) {
                try {
                    Socket socket = new Socket(InetAddress.getByName(clientAdrdress.getIp()), clientAdrdress.getPort());

                    File file = getFile(queryFile, iPeer);
                    FileInputStream fis = new FileInputStream(file);

                    byte[] byteArray = new byte[(int) file.length()];
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(byteArray, 0, byteArray.length);

                    OutputStream os = socket.getOutputStream();
                    os.write(byteArray, 0, byteArray.length);
                    ThreadLog(String.format("Enviando arquivo %s para o client %s", queryFile, clientAdrdress.toString()));
                    os.flush();
                    socket.close();

                    ThreadLog(String.format("Arquivo %s enviado com sucesso!", queryFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (query.getTtl() > 0) {
                Address peerHasFileAddress = getNextPeer(queryFile, iPeer);

                MessageHandler messageHandler = new MessageHandler();
                Message message = new Message(MessageType.CLIENT, messageHandler.stringfy(query));

                try {
                    MessageSenderService.sendMessage(socket, messageHandler.stringfy(message), peerHasFileAddress);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } else {
            ThreadLog(String.format("A consulta %s ja foi realizada", query.getClientId().getToken()));
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
            semaphore.acquire();

            for (PeerRecord pr : peerRecords) {
                if (pr.getPeer().hasFile(queryFile)) {
                    peerHasFileAddress = pr.getPeer().getAddress();
                    break;
                }
            }

            if (peerHasFileAddress == null) {
                while (peerHasFileAddress.equals(iPeer.getAddress())) {
                    peerHasFileAddress = peerRecords.get(LotteryService.getRandomInt(peerRecords.size())).getPeer().getAddress();
                }
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
        return ClientMessageHandler.class.getSimpleName();
    }
}
