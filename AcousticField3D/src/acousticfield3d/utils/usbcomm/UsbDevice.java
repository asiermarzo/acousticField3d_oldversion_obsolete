/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package acousticfield3d.utils.usbcomm;

import ch.ntb.usb.Device;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asier
 */
public class UsbDevice {
    private final Device d;
    
    public static final int TIMEOUT = 2000;
    
    private int endPoint = 1;

    public int getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(int endPoint) {
        this.endPoint = endPoint;
    }
    
    
    public UsbDevice(int vendorId, int productId) {
        d = USB.getDevice((short) vendorId, (short) productId);
    }
    
    public boolean open(int config, int inter){
        try {
            d.open(config, inter, -1);
        } catch (USBException ex) {
            Logger.getLogger(UsbDevice.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public boolean close(){
        try {
            d.close();
        } catch (USBException ex) {
            Logger.getLogger(UsbDevice.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public boolean send(byte[] data){
        try {
            d.writeBulk(endPoint, data, data.length, TIMEOUT, true);
        } catch (USBException ex) {
            Logger.getLogger(UsbDevice.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
}
