/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TrialsAndTribulations;

import java.util.BitSet;

/**
 *
 * @author Tom
 */
public class BitShift {

    public static void main(String[] args) {

        BitSet bs = new BitSet();

        bs.set(2);
        bs.set(0);

        for (int i = 0; i < 8; i++) {
            if (bs.get(i) == true) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }

        }
        System.out.println();

        
        String message = "Testing a message 123";
        byte[] ar = new byte[message.length()];
        try {
            ar = message.getBytes("UTF-8");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        String as = "";
        
        for (int i = 0; i < ar.length; i++) {
            byte b1 = ar[i];
            String s = String.format("%8s", Integer.toBinaryString(b1 & 0xFF))
                    .replace(' ', '0');
            //System.out.print(s);
            as += s;
        }
        
        System.out.println(as);
    }

// Takes a hex number as a string and converts to a byte array
    public static byte[] HexStringToBytes(String s) {

        int size = s.length();
        byte[] b = new byte[size / 2];

        for (int i = 0; i < size; i += 2) {
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return b;
    }
}
