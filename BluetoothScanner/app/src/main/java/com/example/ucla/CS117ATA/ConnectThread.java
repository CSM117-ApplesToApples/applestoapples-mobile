package com.example.ucla.CS117ATA;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;



/**
 * Created by User on 6/3/2015.
 */
public class ConnectThread extends Thread{

    private final BluetoothDevice bTDevice;
    private final BluetoothSocket bTSocket;
    private static final UUID MY_ID;


    public ConnectThread(BluetoothDevice bTDevice, UUID UUID) {
        BluetoothSocket tmp = null;
        this.bTDevice = bTDevice;
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String uuid = tManager.getDeviceId();
        this.MY_ID = UUID.fromString(uuid);
        try {
            tmp = this.bTDevice.createRfcommSocketToServiceRecord(UUID);
        }
        catch (IOException e) {
            Log.d("CONNECTTHREAD", "Could not start listening for RFCOMM");
        }
        bTSocket = tmp;
    }

    public boolean connect() {

        try {
            bTSocket.connect();
        } catch(IOException e) {
            Log.d("CONNECTTHREAD","Could not connect: " + e.toString());
            try {
                bTSocket.close();
            } catch(IOException close) {
                Log.d("CONNECTTHREAD", "Could not close connection:" + e.toString());
                return false;
            }
        }
        return true;
    }

    public boolean cancel() {
        try {
            bTSocket.close();
        } catch(IOException e) {
            return false;
        }
        return true;
    }

}
