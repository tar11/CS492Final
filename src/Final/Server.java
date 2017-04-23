/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Final;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Group 14 Final Project CS492 - Server Class
 * Group Members: Tom R. & Bart S.
 * 
 * The server class must be launched before the client class. This class
 * accepts a socket connection through port 6013. Once a connection is 
 * established with the socket, the following protocol will be utilized to set
 * up initialization via RSA public key cryptography:
 * 
 *      "Name",R                        (client -> server)
 *      [{R,K}.Client].Server           (server -> client)
 *      [{R+1,K}.Server].Client         (client -> server)
 * 
 *      where .Server/.Client signifies who can decrypt{} or who has signed[]
 *      and R is a random number nonce
 * 
 * Once the above exchange is made, the server will send a response to the
 * client indicating a successful or unsuccessful connection via protocol.
 * 
 * The initialization exchange uses a serializable object (DataPacket) to
 * transmit the information.
 * 
 * The RSA functions are handled by the RSAhandler class.
 * 
 * @author Tom Roberge
 * Date: 4/18/17
 * Reference: p. 37 & 38 of Class Lecture 17 Simple Protocols
 */
public class Server {
    
    // Key locations for RSA
    public static final String PUBLIC_KEY = "C:/RSA/Server/public.key";
    public static final String PRIVATE_KEY = "C:/RSA/Server/private.key";
    public static final String CLIENT_PUBLIC_KEY = "C:/RSA/Client/public.key";
    
    // Symmetric encryption key
    public static final String KEY = "1123456789ABCDEF";
    
    public static void main(String[] args) {
        
        System.out.println("---SERVER---");

        // Load the files containing the keys for RSA
        File pubKey = new File(PUBLIC_KEY);
        File privKey = new File(PRIVATE_KEY);
        File clientKey = new File(CLIENT_PUBLIC_KEY);

        // If the keys do not exist, generate them
        if (!pubKey.exists() || !privKey.exists()) {
            KeyGen gen = new KeyGen(PUBLIC_KEY, PRIVATE_KEY);
            gen.generate();
        }

        int counter = 0;
        DataPacket data;
        RSAhandler handler = new RSAhandler(privKey, clientKey);
        int r = 0;
        boolean nonce = false;
        boolean clientSigned = false;
        boolean sKey = false;
        boolean initialization = false;
        
        try {
            ServerSocket sock = new ServerSocket(6013);
            
            while (true) {
                // Accept the client connection to the server
                Socket client = sock.accept();
                String message = null;

                // Object to pass text to the socket output
                PrintWriter pw = new PrintWriter(client.getOutputStream(), true);

                // Reader objects to handle text passed
                BufferedReader bin = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedReader bout = new BufferedReader(new InputStreamReader(System.in));

                // Create reader objects to handle objects passed
                ObjectOutputStream outToClient = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream inFromClient = new ObjectInputStream(client.getInputStream());
                
                if (counter < 2) {
                    if (counter == 0) {
                        // Receive name and nonce
                        data = (DataPacket) inFromClient.readObject();
                        System.out.println(data.getName() + " is trying to connect...");
                        r = data.getR();

                        // Send [{R,K}.Client].Server
                        String s = Integer.toString(data.getR()) + "," + KEY;
                        data.setCiphertext(handler.encrypt(s.getBytes()));
                        data.setSigned(handler.getSignature(data.getCiphertext()));
                        outToClient.writeObject(data);
                        
                        counter++;
                    }
                    if (counter == 1) {
                        // Receive [{R+1,K}.Server].Client
                        data = (DataPacket) inFromClient.readObject();
                        String s = handler.decrypt(data.getCiphertext());
                        StringTokenizer st = new StringTokenizer(s, ",");
                        s = st.nextToken();
                        
                        // Check if encrypted nonce is incremented r + 1
                        if (r + 1 == Integer.parseInt(s)) {
                            nonce = true;
                        }
                        
                        // Check if same key is returned in encryption
                        s = st.nextToken();
                        if (s.equals(KEY)) {
                            sKey = true;
                        }
                        
                        // Check signature of client
                        clientSigned = handler.verifySignature(data.getSigned(), data.getCiphertext());
                        
                        // If everything is verified, set initialization to ready
                        if (nonce == true && clientSigned == true && sKey == true) {
                            initialization = true;
                        }

                        // Send encryption all ready confirmation
                        if (initialization == true) {
                            data.setMessage("Encryption ready.");
                        } else {
                            data.setMessage("Encryption initialization failed.");
                        }
                        outToClient.writeObject(data);
                        
                        counter++;
                    }
                }
                
                // Create a new cipher object and set key
                A51 a51 = new A51();
                a51.setKey(KEY);
                
                // Symmetric encryption message transfer
                while ((message = bin.readLine()) != null) {
                    a51.setMessage(message);
                    System.out.println("Client Message Encrypted: " + message);
                    System.out.println("Client Message Decrypted: " + a51.cipher());
                    
                    // Get message, encipher, and send
                    String line = bout.readLine();
                    a51.setMessage(line);
                    pw.println(a51.cipher());
                }
                
                client.close();
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
}
