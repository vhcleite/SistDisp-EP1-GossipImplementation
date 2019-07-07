package services;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import model.Metadata;
import model.Peer;

public class MetaDataBuilderThread extends Thread {

    private final static int TIMEOUT = 2000;

    private Peer peer;

    public MetaDataBuilderThread(Peer peer) {
        this.setPeer(peer);
    }

    // https://netjs.blogspot.com/2017/04/reading-all-files-in-folder-java-program.html
    @Override
    public void run() {

        while (true) {

            System.out.println("START Run Thread MetaDataBuilder: ");

            String home = System.getProperty("user.home");
            File folder = new File(home + "/gossip_test_folder/");
            System.out.println("reading files Java8 - Using Files.walk() method");
            listAllFiles(home + "/gossip_test_folder");

            System.out.println("END Run Thread MetaDataBuilder: ");

            try {
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void listAllFiles(String path) {
        System.out.println("In listAllfiles(String path) method");
        try {

            ArrayList<String> files = new ArrayList<String>();

            Files.walk(Paths.get(path)).forEach(filePath -> {

                if (Files.isRegularFile(filePath)) {
                    files.add(filePath.getFileName().toString());
                }
            });

            // Criado um Metadata para cada verificação para que novo timestatmp seja gerado
            // no construtor
            getPeer().setMetadata(new Metadata(files));

            System.out.println(getPeer().getMetadata().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Peer getPeer() {
        return peer;
    }

    public void setPeer(Peer peer) {
        this.peer = peer;
    }

}