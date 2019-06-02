package adk.midicontroller;

import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;

import javax.security.auth.callback.Callback;

/**
 * Created by Andrew on 27/02/2016.
 */
public class MidiDriver {
    private static MidiDriver ourInstance = new MidiDriver();

    Context myContext;

    MidiManager myManager;

    MidiDevice myDevice;
    MidiInputPort myOutput;
    MidiOutputPort myInput;

    public static final CC_BANK_MSB = 0;
    public static final CC_BANK_LSB = 32;

    public static MidiDriver getInstance() {
        return ourInstance;
    }

    private MidiDriver() {
    }

    public void init(Context context) {
        this.myContext = context.getApplicationContext();

        this.myManager = ((MidiManager) this.myContext.getSystemService(Context.MIDI_SERVICE));
    }

    public void connect(final Runnable runnable) {
        this.connect(runnable, new Handler());
    }

    public void connect(final Runnable runnable, final Handler handler) {
        MidiDeviceInfo[] info = myManager.getDevices();
        if(info.length > 0) {
            connectToDevice(info[0], runnable, handler);
        }

        this.myManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
            public void onDeviceAdded(MidiDeviceInfo device) {
                connectToDevice(device, runnable, handler);
            }

            public void onDeviceRemoved(MidiDeviceInfo device) {
                // ...
            }
        }, handler);
    }

    private void connectToDevice(MidiDeviceInfo device, final Runnable runnable, final Handler handler) {
        myManager.openDevice(device, new MidiManager.OnDeviceOpenedListener() {
            public void onDeviceOpened(MidiDevice device) {
                myDevice = device;

                myOutput = device.openInputPort(0);

                myInput = device.openOutputPort(0);
                myInput.connect(new MidiReceiver {
                    public void onSend(byte[] data, int offset, int count, long timestamp) throws IOException {
                        // parse MIDI or whatever
                    }
                });

                handler.post(runnable);
            }
        }, handler);
    }

    public String getDeviceInfo() {
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

    public boolean sendProgramChange(byte channel, byte program) {
        try {
            this.myOutput.send(new byte[]{(byte) (0xC0 + channel), program}, 0, 2);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean sendControlChange(byte channel, byte control, byte value) {
        try {
            this.myOutput.send(new byte[]{(byte) (0xB0 + channel), control, value}, 0, 3);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean sendSysExMessage(byte[] manufacturer, byte model, byte[] data) {
        byte[] msg = new byte[data.length + manufacturer.length +3]
        msg[0] = 0xF0;
        for (int i = 0; i < manufacturer.length; i++) {
            msg[i + 1] = manufacturer[i];
        }
        msg[manufacturer.length + 1] = model;
        for (int i = 0; i < data.length; i++) {
            msg[i + manufacturer.length + 2] = data[i];
        }
        msg[msg.length - 1] = 0xF7;
        try {
            this.myOutput.send(msg, 0, msg.length);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

   public boolean sendBankChangeMsb(byte channel, byte bank) {
         return sendControlChange(channel, CC_BANK_MSB, bank);
    }

    public boolean sendBankChangeLsb(byte channel, byte bank) {
         return sendControlChange(channel, CC_BANK_LSB, bank);
    }
}
