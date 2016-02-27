package adk.ax8midicontroller;

import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import javax.security.auth.callback.Callback;

/**
 * Created by Andrew on 27/02/2016.
 */
public class MidiDriver {
    private static MidiDriver ourInstance = new MidiDriver();

    Context myContext;

    MidiManager myManager;
    Boolean isConnected = false;

    public static MidiDriver getInstance() {
        return ourInstance;
    }

    private MidiDriver() {
    }

    public void init(Context context) {
        this.myContext = context.getApplicationContext();
    }

    public void connect(final Handler handler, final Runnable runable) {
        this.myManager = ((MidiManager) this.myContext.getSystemService(Context.MIDI_SERVICE));

        this.myManager.registerDeviceCallback(new MidiManager.DeviceCallback() {

            MidiDeviceInfo[] info = myManager.getDevices();

            public void onDeviceAdded(MidiDeviceInfo device) {
                myManager.openDevice(info[0], new MidiManager.OnDeviceOpenedListener() {
                    public void onDeviceOpened(MidiDevice device) {
                        handler.post(runable);
                    }
                }, handler);
            }

            public void onDeviceRemoved(MidiDeviceInfo device) {
                // ...
            }
        }, handler);
    }

    public String getStatus() {
        MidiDeviceInfo[] info = this.myManager.getDevices();

        if(info.length > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < info.length; i++) {
                buffer.append("Device " + i + ": " +
                    info[i].getInputPortCount() + " inputs, " +
                    info[i].getOutputPortCount() + " outputs.\n");
            }
            buffer.append(this.isConnected ? "Connected!" : "Not connected.");
            return buffer.toString();
        } else {
            return "No devices detected";
        }
    }
}