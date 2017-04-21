/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Final;

import java.io.Serializable;

/**
 * Group 14 Final Project CS492 - DataPacket class
 * 
 * This can be used as an object to hold the different variables being sent
 * so one more manageable object can be sent and received through the socket.
 * This object is serializable.
 * 
 * @author Tom Roberge
 * Date: 4/18/17
 */
public class DataPacket implements Serializable {
    
    String name;
    int r;
    byte[] ciphertext, signed;
    String message;
    
    public DataPacket() {
        
    }
    
    public DataPacket(String name, int r) {
        this.name = name;
        this.r = r;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getR() {
        return r;
    }
    
    public void setR(int r) {
        this.r = r;
    }
    
    public void setCiphertext(byte[] ciphertext) {
        this.ciphertext = ciphertext;
    }
    
    public byte[] getCiphertext() {
        return ciphertext;
    }
    
    public void setSigned(byte[] signed) {
        this.signed = signed;
    }
    
    public byte[] getSigned() {
        return signed;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
    
}
