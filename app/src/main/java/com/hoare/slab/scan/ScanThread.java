package com.hoare.slab.scan;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.pda.serialport.SerialPort;
import cn.pda.serialport.Tools;

/**
 * Created by Administrator on 2018/6/19/019.
 */

public class ScanThread extends Thread {

    private static String TAG = ScanThread.class.getSimpleName();

    private SerialPort mSerialPort;
    private InputStream is;
    private OutputStream os;
    /* serialport parameter */
    private int port = 3;
    private int baudrate = 9600;
    private int flags = 0;

    private Handler handler;

    public static int SCAN = 1001; // messege recv mode

    /**
     * if throw exception, serialport initialize fail.
     *
     * @throws SecurityException
     * @throws IOException
     */
    public ScanThread(Handler handler) throws SecurityException, IOException {
        this.handler = handler;
        mSerialPort = new SerialPort(port, baudrate, flags);
        mSerialPort.scaner_poweron();
        mSerialPort.rfid_poweron();
        is = mSerialPort.getInputStream();
        os = mSerialPort.getOutputStream();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /** clear useless data **/
        byte[] temp = new byte[1024];
        is.read(temp);
    }

    @Override
    public void run() {
        try {
            int size = 0;
            byte[] buffer = new byte[1024];
            int available = 0;
            while (!isInterrupted()) {
                available = is.available();
                int sumLen = 0;
                byte[] tempBuffer = new byte[1024];
                while (available > 0) {
                    Log.e(TAG, "available = " + available);
                    size = is.read(buffer);
                    if (size > 0) {
                        String byteStr = Tools.Bytes2HexString(buffer, size);
                        Log.e(TAG, "byte = " + byteStr);
                        if (byteStr.endsWith("0D")) {
                            System.arraycopy(buffer, 0, tempBuffer, sumLen, size);
                            sumLen = sumLen + size;
                            Log.e(TAG, "sumLen = " + sumLen);
                            Log.e(TAG, "tempBuffer = " + tempBuffer.length);
                            if (sumLen > 12) {
                                sendMessege(tempBuffer, sumLen, SCAN);
                            } else {
                                sendMessege(buffer, size, SCAN);
                            }
                            available = is.available();
                        } else {
                            System.arraycopy(buffer, 0, tempBuffer, sumLen, 12);
                            sumLen = sumLen + size;
                            available = is.available();
                            while (available == 0) {
                                available = is.available();
                            }
                        }
                    }
                }
//				if (available > 0) {
//					Log.e(TAG, "available = " + available);
//					size = is.read(buffer);
//					if (size > 0) {
//						sendMessege(buffer, size, SCAN);
//					}
//				}
            }
        } catch (Exception e) {
            // 返回错误信息
            e.printStackTrace();
        }
        super.run();
    }

    private void sendMessege(byte[] data, int dataLen, int mode) {
        try {
            String dataStr = new String(data, 0, dataLen);
            Bundle bundle = new Bundle();
            bundle.putString("data", dataStr);
            Message msg = new Message();
            msg.what = mode;
            msg.setData(bundle);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scan() {
        if (mSerialPort.scaner_trig_stat() == true) {
            mSerialPort.scaner_trigoff();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        mSerialPort.scaner_trigon();
    }

    public void close() {
        if (mSerialPort != null) {
            mSerialPort.scaner_poweroff();
            mSerialPort.rfid_poweroff();
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSerialPort.close(port);
        }
    }

}
