import java.util.ArrayList;

import model.Peer;
import model.PeerRecord;

public class PeerService {
    
    private Peer peer;
    
    private ArrayList<PeerRecord> records = new ArrayList<PeerRecord>();
    
    public PeerService(Peer peer) {
        
        this.peer = peer;
    }
    
    public static void main(String[] args) {
        
    }
    
}
