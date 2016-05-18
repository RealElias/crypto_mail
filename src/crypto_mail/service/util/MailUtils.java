package crypto_mail.service.util;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by elias on 12.5.16.
 */
public class MailUtils {

    public static String asString(List<Address> addresses) {
        StringJoiner joiner = new StringJoiner(", ");
        addresses.stream().forEach(address -> joiner.add(address.toString()));
        return joiner.toString();
    }

    public static List<Address> asAddressList(String... addressesLines) {
        List<Address> addresses = new ArrayList<>();
        try {
            for(String addressesLine : addressesLines) {
                for (String address : addressesLine.split(", ")) {
                    addresses.add(new InternetAddress(address));
                }
            }
        } catch (AddressException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    public static Address[] asAddressArray(List<Address> addressList) {
        Address[] addresses = new Address[addressList.size()];

        for (int i = 0; i < addressList.size(); i++) {
            addresses[i] = addressList.get(i);
        }

        return addresses;
    }
}
