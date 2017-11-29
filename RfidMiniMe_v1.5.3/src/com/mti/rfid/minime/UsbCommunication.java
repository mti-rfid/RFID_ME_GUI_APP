package com.mti.rfid.minime;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import android.app.Application;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;

public class UsbCommunication extends Application{
	public static final String TAG = "UsbControl";
	private static final boolean DEBUG = false;

	private static UsbCommunication usbComm;
	private static UsbDevice mDevice;
	private static UsbInterface mInterface;
	private static UsbDeviceConnection mDeviceConnection;
	private static UsbEndpoint mEndpointOut;
	private static UsbEndpoint mEndpointIn;
	
	public static int mTagMode;		// 0:Gen2, 1:Gen2+RSSI, 2:ISO6B
	public static boolean mStop;
	private static Thread mThread;
	private static final int DATA_LENGTH = 64;
	private static ByteBuffer mDataBuffer= ByteBuffer.allocate(DATA_LENGTH);
	private final LinkedList<UsbRequest> mInRequestPool = new LinkedList<UsbRequest>();
	
	public static UsbCommunication newInstance() {
		usbComm = new UsbCommunication();
		return usbComm;
	}

	public static UsbCommunication getInstance() {
		return usbComm;
	}

	public boolean setUsbInterface(UsbManager manager, UsbDevice device) {
		if(device != null) {
			if(mDeviceConnection != null) {
				if(mInterface != null) {
					mDeviceConnection.releaseInterface(mInterface);
					mInterface = null;
				}
				mDeviceConnection.close();
				mDeviceConnection = null;
				mDevice = null;
			}
			
			UsbInterface intf = device.getInterface(0);
			UsbDeviceConnection connection = manager.openDevice(device);
			if(connection != null) {
				if(DEBUG) Log.d(TAG, "open succeeded");
				if(connection.claimInterface(intf, true)) {
					if(DEBUG) Log.d(TAG, "claim interface succeeded");
					mDevice = device;
					mInterface = intf;
					mDeviceConnection = connection;

					UsbEndpoint epOut = null;
					UsbEndpoint epIn = null;

					for(int i = 0; i < intf.getEndpointCount(); i++) {
						UsbEndpoint ep = intf.getEndpoint(i);
/*
						if(ep.getDirection() == UsbConstants.USB_DIR_OUT)
							epOut = ep;
						else if (ep.getDirection() == UsbConstants.USB_DIR_IN)
							epIn = ep;
*/
						if (ep.getDirection() == UsbConstants.USB_DIR_IN)
							epIn = ep;
					}
/*
					if(epOut == null || epIn == null) {
						Log.e(TAG, "not all endpoints found");
						throw new IllegalArgumentException("not all endpoints found.");
					}
					mEndpointOut = epOut;
*/
					if(epIn == null) {
						Log.e(TAG, "endpoint input not found");
						throw new IllegalArgumentException("endpoint input not found.");
					}
					mEndpointIn = epIn;

					startThread();
					clearBuffer();

					return true;
				} else {
					Log.e(TAG, "claim interface failed");
					connection.close();
				}
			} else {
				Log.e(TAG, "open failed");
			}
		} else {
			clearBuffer();
			stopThread();
			mDeviceConnection = null;
			mInterface = null;
			mDevice = null;
		}
		return false;
	}
	
	public void clearBuffer() {
		sleep(1500);
		getResponse();
		synchronized(mInRequestPool) {
			mInRequestPool.clear();
		}
		getResponse();
	}
	
	public UsbDevice getUsbDevice() {
		return mDevice;
	}
	
	public int sendCmd(byte[] message, int length) {
		int sendBytes = 0;
		synchronized(this) {
			if(mDeviceConnection != null) {
				sendBytes = mDeviceConnection.controlTransfer(
						0x21, 0x09, 0x00, 0x00,	message, length, 2000);
			}
		}
		return sendBytes;
	}
	
	
	public byte[] getResponse() {
		UsbRequest request = getInRequest();
		request.queue(mDataBuffer, DATA_LENGTH);

		if(mDataBuffer.hasArray()) {
			if(DEBUG) Log.d(TAG, "buffer have data");
			return mDataBuffer.array();
		}
		return null;
	}

	
	private UsbRequest getInRequest() {
        synchronized(mInRequestPool) {
            if (mInRequestPool.isEmpty()) {
    			if(DEBUG) Log.d(TAG, "pool is empty");
    			UsbRequest request = new UsbRequest();
    			request.initialize(mDeviceConnection, mEndpointIn);
             	return request;
            } else {
            	return mInRequestPool.removeFirst();
            }
        }
	}
	
	
	private Thread startThread() {
		if(DEBUG) Log.d(TAG, "startThread");
		mStop = false;
		mThread = new Thread(new NewRunnable());
		mThread.start();
		return mThread;
	}

	private void stopThread() {
		mStop = true;
		try {
			mThread.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class NewRunnable implements Runnable {
		@Override
		public void run() {
			while(true) {
					if(mStop) {
						Log.d(TAG, "Stop Thread");
						return;
					}
				
				UsbRequest request = mDeviceConnection.requestWait();

				if(request == null)
					break;
				
				request.setClientData(null);
				
				synchronized(mInRequestPool) {
					mInRequestPool.add(request);
				}
			}
		}
	}

	public void setTagMode(int tagmode) {
		mTagMode = tagmode;
	}
	
	public int getTagMode() {
		return mTagMode;
	}
	
	private void sleep(int millisecond) {
		try{
			Thread.sleep(millisecond);
		} catch (InterruptedException e) {}
	}

}
