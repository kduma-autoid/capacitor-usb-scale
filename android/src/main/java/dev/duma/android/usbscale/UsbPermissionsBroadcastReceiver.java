package dev.duma.android.usbscale;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.util.Objects;

public class UsbPermissionsBroadcastReceiver {
    public static final String USB_PERMISSION ="dev.duma.capacitor.usbscale.USB_PERMISSION";

    private final Callback callback;
    private final Context context;


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        if (!Objects.requireNonNull(intent.getAction()).equals(USB_PERMISSION)) {
            return;
        }

        callback.run(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false));

        context.unregisterReceiver(receiver);
        }
    };

    public UsbPermissionsBroadcastReceiver(Callback callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    public PendingIntent register() {
        IntentFilter filter = new IntentFilter(USB_PERMISSION);

        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);

        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                ? PendingIntent.FLAG_MUTABLE
                : 0;
        return PendingIntent.getBroadcast(context, 0, new Intent(USB_PERMISSION), flags);
    }


    public interface Callback {
        void run(boolean status);
    }
}