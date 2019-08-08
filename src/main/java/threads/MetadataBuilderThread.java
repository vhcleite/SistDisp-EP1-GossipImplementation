package threads;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import model.Metadata;
import model.Peer;

public class MetadataBuilderThread extends AbstractThread {

    private final static int TIMEOUT = 1000;

    public MetadataBuilderThread(Peer peer) {
        super(peer);
    }

    // https://netjs.blogspot.com/2017/04/reading-all-files-in-folder-java-program.html
    @Override
    public void run() {

        while (true) {

            listAllFiles(getPeer().getMonitoringFolderName());
//            ThreadLog(getPeer().getMetadata().toString());
//
            try {
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void listAllFiles(String path) {
        try {

            ArrayList<String> files = new ArrayList<String>();

            Files.walk(Paths.get(path)).forEach(filePath -> {

                if (Files.isRegularFile(filePath)) {
                    files.add(filePath.getFileName().toString());
                }
            });

            // Criado um Metadata para cada verificação para que novo timestamp seja gerado
            // no construtor
            getPeer().setMetadata(new Metadata(files));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getThreadName() {
        return MetadataBuilderThread.class.getSimpleName();
    }
}