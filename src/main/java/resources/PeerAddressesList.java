package resources;

import java.util.Arrays;
import java.util.List;

import model.Address;

public class PeerAddressesList {

    public static List<Address> adresses = Arrays.asList(//
            new Address("127.0.0.1", 9000), //
            new Address("127.0.0.1", 9002));

    public static int getSize() {
        return adresses.size();
    }

    public static Address getAddress(int position) {
        return adresses.get(position);
    }

}
