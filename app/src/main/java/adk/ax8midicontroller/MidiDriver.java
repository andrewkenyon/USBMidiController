package adk.ax8midicontroller;

import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;

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

    public boolean connect() {
        this.myManager = ((MidiManager) this.myContext.getSystemService(Context.MIDI_SERVICE));

        this.myManager.registerDeviceCallback(new MidiManager.DeviceCallback() {

            MidiDeviceInfo[] info = myManager.getDevices();

            public void onDeviceAdded(MidiDeviceInfo device) {
                myManager.openDevice(info[0], new MidiManager.OnDeviceOpenedListener() {
                    public void onDeviceOpened(MidiDevice device) {
                        isConnected = true;
                    }
                }, null);
            }

            public void onDeviceRemoved(MidiDeviceInfo device) {
                // ...
            }
        }, null);
        return isConnected;
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