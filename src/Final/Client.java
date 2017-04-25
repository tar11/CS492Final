package Final;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Final Project CS492 - Client Class
 * Group Members: Tom R. & Bart S.
 * 
 * The server class must be launched before the client class. This class
 * sends a socket connection request to port 6013 on local host (127.0.0.1). 
 * Once a connection is established with the socket, the following protocol 
 * will be utilized to set up initialization via RSA public key cryptography:
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
 * Reference: p. 37 & 38 of Lecture 17 Simple Protocols
 */
public class Client {

    public static final String PUBLIC_KEY = "C:/RSA/Client/public.key";
    public static final String PRIVATE_KEY = "C:/RSA/Client/private.key";
    public static final String SERVER_PUBLIC_KEY = "C:/RSA/Server/public.key";

    public static void main(String[] args) {

        System.out.println("---CLIENT---");

        // Get the client keys for RSA
        File pubKey = new File(PUBLIC_KEY);
        File privKey = new File(PRIVATE_KEY);
        File servKey = new File(SERVER_PUBLIC_KEY);

        // If the keys do not exist, generate them
        if (!pubKey.exists() || !privKey.exists()) {
            KeyGen gen = new KeyGen(PUBLIC_KEY, PRIVATE_KEY);
            gen.generate();
        }

        // Create nonce to send to server
        Random rand = new Random();
        int n = rand.nextInt(10000) + 1;

        try {
            // Make connection to server socket
            Socket sock = new Socket("127.0.0.1", 6013);
            String message = null;

            // Write to the socket
            PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);

            // Create reader objects to handle messages passed
            BufferedReader bout = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader bin = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            // Create reader objects to handle objects passed
            ObjectOutputStream outToServer = new ObjectOutputStream(sock.getOutputStream());
            ObjectInputStream inFromServer = new ObjectInputStream(sock.getInputStream());

            // Create new datapacket object and add name and nonce
            DataPacket data = new DataPacket("Client", n);
            
            // Create new RSAhandler object to handle RSA activity on client side
            RSAhandler handler = new RSAhandler(privKey, servKey);

            int counter = 0;
            boolean serverSigned = false;
            boolean nonce = false;
            boolean initialization = false;
            String s;
            String checkNonce, sKey = ""; // sKey is string of symmetric key

            while (true) {
                // If counter < 2, initialize ecryption
                if (counter < 2) {
                    if (counter == 0) {
                        // Send name and nonce as DataPacket object
                        outToServer.writeObject(data);

                        // Receive [{R,K}.Client].Server
                        data = (DataPacket) inFromServer.readObject();

                        // Get result of signature
                        serverSigned = handler.verifySignature(data.getSigned(), data.getCiphertext());

                        // Decrypt text, check nonce, get key
                        s = handler.decrypt(data.getCiphertext());
                        StringTokenizer st = new StringTokenizer(s, ",");
                        checkNonce = st.nextToken();
                        if (n == Integer.parseInt(checkNonce)) {
                            nonce = true;
                        }
                        sKey = st.nextToken();
                    }
                    if (counter == 1) {
                        // Send [{R+1,K}.Server].Alice
                        n += 1;
                        String temp = Integer.toString(n) + "," + sKey;
                        data.setCiphertext(handler.encrypt(temp.getBytes()));
                        data.setSigned(handler.getSignature(data.getCiphertext()));
                        outToServer.writeObject(data);

                        // Receive verification of connection
                        data = (DataPacket) inFromServer.readObject();
                        System.out.println("Server initialization says: " + data.getMessage());

                        // If server signature and encrypted nonce are good, initialization ready
                        if (serverSigned == true && nonce == true) {
                            initialization = true;
                        }

                        System.out.println("Client encryption initialization: " + initialization);
                        System.out.println("~~~~~~~~~~~~");
                    }
                    
                } else if (initialization == true) {
                    // Symmetric encryption chat
                    System.out.print(">>");
                    String r = bout.readLine();
                    
                    // Create a new cipher object and set key
                    A51 a51 = new A51();
                    a51.setKey(sKey);
                    a51.setMessage(r);
                    
                    // Encipher and send
                    pw.println(a51.cipher());
                    
                    // Read and decrypt response
                    message = bin.readLine();
                    a51.setMessage(message);
                    System.out.println("Server Message Encrypted: " + message);
                    System.out.println("Server Message Decrypted: " + a51.cipher());
                    
                } else if (counter >= 2 && initialization == false) {
                    System.exit(0);
                }

                counter++;
            }

        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
