package resources;

import java.util.ArrayList;
import java.util.List;

import model.Address;

public class PeerAddressesList {

    public static List<Address> adresses;

    public PeerAddressesList(String remotePeersList) {
        this.adresses = getAddressesFromString(remotePeersList);
    }

    public static List<Address> getAddressesFromString(String remotePeersList) {
        List<Address> addresses = new ArrayList<>();
        String[] split = remotePeersList.split(",");

        for (String address : split) {
            Address addressFromString = getAddressFromString(address);
            addresses.add(addressFromString);
        }
        return addresses;
    }

    public static Address getAddressFromString(String address) {
        String[] addressArray = address.split(":");
        return new Address(addressArray[0], Integer.valueOf(addressArray[1]));
    }

    public static int getSize() {
        return adresses.size();
    }

    public static Address getAddress(int position) {
        return adresses.get(position);
    }

}
