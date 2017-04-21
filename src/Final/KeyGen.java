package Final;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Group 14 Final Project CS492 - KeyGen class
 * 
 * This class creates public and private keys for RSA encryption and 
 * signatures. It creates new files under the passed directory and name.
 * 
 * @author Tom Roberge
 * Date: 4/18/17
 */
public class KeyGen {

    String pubKey;
    String privKey;

    public KeyGen(String pubKey, String privKey) {
        this.pubKey = pubKey;
        this.privKey = privKey;
    }

    public void generate() {
        try {
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
            keyGenerator.initialize(1024);
            KeyPair key = keyGenerator.generateKeyPair();

            // Create new file objects
            File privateFile = new File(privKey);
            File publicFile = new File(pubKey);

            // Create private key files
            if (!privateFile.exists()) {
                privateFile.getParentFile().mkdirs();
                privateFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(privateFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(key.getPrivate());
                oos.close();
            }

            // Create public key files
            if (!publicFile.exists()) {
                publicFile.getParentFile().mkdirs();
                publicFile.createNewFile();
                FileOutputStream fos2 = new FileOutputStream(publicFile);
                ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
                oos2.writeObject(key.getPublic());
                oos2.close();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
