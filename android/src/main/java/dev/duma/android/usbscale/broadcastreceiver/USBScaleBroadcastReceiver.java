package dev.duma.android.usbscale.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import androidx.core.content.ContextCompat;

import java.util.Objects;

public class USBScaleBroadcastReceiver implements IUSBScaleBroadcastReceiver {
    private final Context context;

    private final Callback callback;

    private UsbDevice currentlyOpenedDevice = null;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {

                case UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (device == null) {
                        return;
                    }

                    callback.OnScaleConnected(device);
                }

                case UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (device == null) {
                        return;
                    }

                    if(currentlyOpenedDevice == null || !currentlyOpenedDevice.getDeviceName().equals(device.getDeviceName())){
                        callback.OnScaleDisconnected(device);
                    } else {
                        callback.OnOpenedScaleDisconnected(device);
                        currentlyOpenedDevice = null;
                    }
                }

            }
        }
    };

    protected USBScaleBroadcastReceiver(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void setCurrentlyOpenedDevice(UsbDevice currentlyOpenedDevice) {
        this.currentlyOpenedDevice = currentlyOpenedDevice;
    }

    @Override
    public void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_EXPORTED);
    }

    @Override
    public void unregister() {
        try {
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
