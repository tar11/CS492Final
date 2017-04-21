/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Final;

import java.util.BitSet;

/**
 * Group 14 Final Project CS492 - A51 Class
 *
 * This class uses the A5/1 Symmetric Stream Cipher to encrypt a message. No
 * frame counter is used in this implementation. This class uses a 64-bit key
 * for encryption/decryption. After the 64 clocks used to insert the key into
 * the three registers, the registers are clocked an additional 100 times.
 *
 * @author Tom Roberge 
 * Date: 4/18/17
 */
public class A51 {

    private String symKey, message;

    public A51() {

    }

    public String cipher() {
        // Tap bits for the registers
        int[] r1taps = {13, 16, 17, 18};
        int[] r2taps = {20, 21};
        int[] r3taps = {7, 20, 21, 22};

        // Set register size and majority bits
        final int R1 = 19;
        final int R1M = 8;
        final int R2 = 22;
        final int R2M = 10;
        final int R3 = 23;
        final int R3M = 10;

        // Initialize variables
        String bs = "";
        byte[] key = HexStringToBytes(symKey);
        BitSet keySet = new BitSet();
        BitSet keyStream = new BitSet();
        BitSet messageSet = new BitSet();

        // Create a byte array length of sample message
        byte[] messageArray = new byte[message.length()];

        // Convert the sample message to a byte array
        try {
            messageArray = message.getBytes("ISO-8859-1");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        // Convert message sample byte array to string
        String as = "";
        for (int i = 0; i < messageArray.length; i++) {
            byte b1 = messageArray[i];
            String s = String.format("%8s", Integer.toBinaryString(b1 & 0xFF))
                    .replace(' ', '0');
            as += s;
        }

        // Convert string of bits to a BitSet
        messageSet = BitStringToBitSet(as);

        // Creates string from key byte array
        for (int i = 0; i < 8; i++) {
            byte b1 = key[i];
            String s = String.format("%8s", Integer.toBinaryString(b1 & 0xFF))
                    .replace(' ', '0');
            bs += s;
        }

        // Convert string of bits to a BitSet
        keySet = BitStringToBitSet(bs);

        // Initialize registers
        BitSet r1 = new BitSet();
        BitSet r2 = new BitSet();
        BitSet r3 = new BitSet();

        // Process key into registers
        for (int i = 0; i < 64; i++) {
            r1 = ShiftSet(r1, R1, keySet.get(i) ^ Tap(r1, r1taps));
            r2 = ShiftSet(r2, R2, keySet.get(i) ^ Tap(r2, r2taps));
            r3 = ShiftSet(r3, R3, keySet.get(i) ^ Tap(r3, r3taps));
        }

        // Clock additional 100 times for additional security (GSM standard)
        for (int i = 0; i < 100; i++) {
            int maj = 0;
            boolean[] ar = {false, false, false};
            if (r1.get(R1M) == true) {
                ar[0] = true;
                maj += 1;
            }
            if (r2.get(R2M) == true) {
                ar[1] = true;
                maj += 1;
            }
            if (r3.get(R3M) == true) {
                ar[2] = true;
                maj += 1;
            }
            // If majority is false (0 bit)
            if (maj <= 1) {
                if (ar[0] == false) {
                    r1 = ShiftSet(r1, R1, Tap(r1, r1taps));
                }
                if (ar[1] == false) {
                    r2 = ShiftSet(r2, R2, Tap(r2, r2taps));
                }
                if (ar[2] == false) {
                    r3 = ShiftSet(r3, R3, Tap(r3, r3taps));
                }
                // Else majority is true
            } else {
                if (ar[0] == true) {
                    r1 = ShiftSet(r1, R1, Tap(r1, r1taps));
                }
                if (ar[1] == true) {
                    r2 = ShiftSet(r2, R2, Tap(r2, r2taps));
                }
                if (ar[2] == true) {
                    r3 = ShiftSet(r3, R3, Tap(r3, r3taps));
                }
            }
        }

        // Create keystream as long as the sample message
        for (int i = 0; i < message.length() * 8; i++) {

            // Get keystream bit
            keyStream.set(i, r1.get(R1 - 1) ^ r2.get(R2 - 1) ^ r3.get(R3 - 1));

            // Shift majority registers
            int maj = 0;
            boolean[] ar = {false, false, false};
            if (r1.get(R1M) == true) {
                ar[0] = true;
                maj += 1;
            }
            if (r2.get(R2M) == true) {
                ar[1] = true;
                maj += 1;
            }
            if (r3.get(R3M) == true) {
                ar[2] = true;
                maj += 1;
            }
            // If majority is false (0 bit)
            if (maj <= 1) {
                if (ar[0] == false) {
                    r1 = ShiftSet(r1, R1, Tap(r1, r1taps));
                }
                if (ar[1] == false) {
                    r2 = ShiftSet(r2, R2, Tap(r2, r2taps));
                }
                if (ar[2] == false) {
                    r3 = ShiftSet(r3, R3, Tap(r3, r3taps));
                }
                // Else majority is true
            } else {
                if (ar[0] == true) {
                    r1 = ShiftSet(r1, R1, Tap(r1, r1taps));
                }
                if (ar[1] == true) {
                    r2 = ShiftSet(r2, R2, Tap(r2, r2taps));
                }
                if (ar[2] == true) {
                    r3 = ShiftSet(r3, R3, Tap(r3, r3taps));
                }
            }

        }

        // XOR the message with the created keystream and return as string
        messageSet.xor(keyStream);
        return BitStringToText(ReturnSet(messageSet, message.length() * 8));
    }

    // Takes a HEX number as a string and converts to a byte array
    public static byte[] HexStringToBytes(String s) {

        int size = s.length();
        byte[] b = new byte[size / 2];

        for (int i = 0; i < size; i += 2) {
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return b;
    }

    // Returns result of XOR of tap bits as True/False
    // Params: register bitset, int array of tap bits for that register
    public static boolean Tap(BitSet bs, int[] a) {
        boolean result = bs.get(a[0]);
        result ^= bs.get(a[1]);

        if (a.length == 4) {
            result ^= bs.get(a[2]);
            result ^= bs.get(a[3]);
        }
        return result;
    }

    // Returns a right shifted bitset
    // Params: register bitset, int size of register, boolean set new bit
    public static BitSet ShiftSet(BitSet bs, int a, boolean set) {
        BitSet temp = bs.get(0, a - 1);
        BitSet result = new BitSet();

        if (set == true) {
            result.set(0);
        }

        int j = 0;
        for (int i = 1; i < a; i++) {
            if (temp.get(j) == true) {
                result.set(i);
            }
            j++;
        }
        return result;
    }

    // Prints bitset
    // Params: bitset to print, size of bitset
    public static void PrintSet(BitSet bs, int size) {
        for (int i = 0; i < size; i++) {
            String s1;
            if (bs.get(i) == true) {
                s1 = "1";
            } else {
                s1 = "0";
            }
            System.out.print(s1);
        }
        System.out.println();
    }

    // Return string of bits representing a BitSet
    // Params: bitset to print, size of bitset
    public static String ReturnSet(BitSet bs, int size) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            String s1;
            if (bs.get(i) == true) {
                s1 = "1";
            } else {
                s1 = "0";
            }
            sb.append(s1);
        }
        return sb.toString();
    }

    // Takes a string of bits and converts to a BitSet
    public static BitSet BitStringToBitSet(String s) {
        BitSet result = new BitSet();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '1') {
                result.set(i);
            }
        }
        return result;
    }

    // Takes a string of bits and returns text string
    public static String BitStringToText(String s) {
        StringBuffer sb = new StringBuffer();
        int begIndex = 0;
        int endIndex = 8;
        for (int i = 0; i < s.length() / 8; i++) {
            String chunk = s.substring(begIndex, endIndex);
            int charCode = Integer.parseInt(chunk, 2);
            String y = new Character((char) charCode).toString();
            sb.append(y);
            begIndex += 8;
            endIndex += 8;
        }
        return sb.toString();
    }

    // Set key for A5/1
    public void setKey(String symKey) {
        this.symKey = symKey;
    }

    // Set message for A5/1
    public void setMessage(String message) {
        this.message = message;
    }

}
