/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Final;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import javax.crypto.Cipher;

/**
 * Group 14 Final Project CS492 - RSAhandler class
 *
 * This class handles the RSA encryption/decryption and signatures by using the
 * Java cipher class. It is passed the location of the public and private keys
 * required to handle the RSA cipher functions.
 *
 * Date: 4/18/17
 * @author Tom Roberge
 */
public class RSAhandler {

    File myPrivate;
    File contactPublic;
    PublicKey publicKey;
    PrivateKey privateKey;

    public RSAhandler(File myPrivate, File contactPublic) {
        this.myPrivate = myPrivate;
        this.contactPublic = contactPublic;
    }

    // Verify signedtext for expected ciphertext
    public boolean verifySignature(byte[] signedtext, byte[] ciphertext) {
        boolean result = false;

        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(contactPublic));
            publicKey = (PublicKey) inputStream.readObject();
            signature.initVerify(publicKey);
            signature.update(ciphertext);
            result = signature.verify(signedtext);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return result;
    }

    public byte[] getSignature(byte[] toSign) {
        byte[] result = null;
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(myPrivate));
            privateKey = (PrivateKey) inputStream.readObject();
            signature.initSign(privateKey);
            signature.update(toSign);
            result = signature.sign();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return result;
    }

    public String decrypt(byte[] ciphertext) {
        byte[] decryptedText = null;
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(myPrivate));
            privateKey = (PrivateKey) inputStream.readObject();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedText = cipher.doFinal(ciphertext);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return new String(decryptedText);
    }

    public byte[] encrypt(byte[] plaintext) {
        byte[] encryptedText = null;
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(contactPublic));
            publicKey = (PublicKey) inputStream.readObject();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            encryptedText = cipher.doFinal(plaintext);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return encryptedText;
    }
}
