/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TrialsAndTribulations;

import java.nio.charset.Charset;

/**
 *
 * @author Tom
 */
public class StringCharacterization {

    public static void main(String[] args) {

        String message = "this is a Test123";

        System.out.println("Length of string: " + message.length());

        byte[] ar = message.getBytes();

        System.out.println("Length of array: " + ar.length);

        String a = ar.toString();

        String message2 = new String(ar);
        System.out.println(message2);

        byte[] al = a.getBytes();

        String messageal = new String(al);

        System.out.println(messageal);

        System.out.println("Default Charset=" + Charset.defaultCharset());

        // c = 63
        String t = "01100011";

        int charCode = Integer.parseInt(t, 2);

        String y = new Character((char) charCode).toString();

        System.out.println("This should be it: " + y);

        // 8    4   2   1
        
        String togo = "01010100011010000110100101110011001000000110100101110011001000000111010001101000011001010010000001100101011011100110001101110010011110010111000001110100011001010110010000100000011011010110010101110011011100110110000101100111011001010010000001110100011001010111001101110100001100010011001000110011";
        
        String fin = BitStringToText(togo);
        
        System.out.println(fin);
        
    }

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
}
