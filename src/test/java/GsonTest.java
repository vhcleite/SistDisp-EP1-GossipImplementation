import org.junit.jupiter.api.Test;

import model.Peer;
import services.MessageHandler;

public class GsonTest {

    @Test
    public void GsonConversionTest() {
        String peerString = "{\"metadata\":{\"creationDate\":\"Jul 9, 2019, 5:16:02 PM\",\"folderContent\":[\"doc2\",\"doc4\",\"documento de teste\",\"doc3\",\"doc1\",\"doc5\"]},\"address\":{\"ip\":\"127.0.0.1\",\"port\":9000}}";
        MessageHandler handler = new MessageHandler();
        Peer peer = handler.parseString(peerString);
        System.out.println("creation date: " + peer.getMetadata().getCreationDate());
    }

}
