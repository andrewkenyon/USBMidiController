package adk.ax8midicontroller;

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
    Boolean isConnected = false;
    MidiDevice myDevice;
    MidiInputPort myOutput;
    MidiOutputPort myInput;

    byte myChannel = 0;

    public static final CC_BANK_MSB = 0;
    public static final CC_BANK_LSB = 32;

    byte[] myManufacturer;
    byte myModel;

    public static MidiDriver getInstance() {
        return ourInstance;
    }

    private MidiDriver() {
    }

    public void init(Context context) {
        this.myContext = context.getApplicationContext();

        this.myManager = ((MidiManager) this.myContext.getSystemService(Context.MIDI_SERVICE));
    }

    public void configSysEx(byte[] manufacturer, byte model) {     
        this.myManufacturer = manufacturer;
        this.myModel = model;
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

    public boolean sendProgramChange(byte program) {
        try {
            this.myOutput.send(new byte[]{(byte) (0xC0 + this.myChannel), program}, 0, 2);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean sendControlChange(byte control, byte value) {
        try {
            this.myOutput.send(new byte[]{(byte) (0xB0 + this.myChannel), control, value}, 0, 3);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean sendSysExMessage(byte[] data) {
        if (this.myManufacturer == null || this.myModel == null) {
            return false;
        }
        byte[] msg = new byte[data.length + this.myManufacturer.length +3]
        msg[0] = 0xF0;
        for (int i = 0; i < this.myManufacturer.length; i++) {
            msg[i + 1] =  this.myManufacturer[i];
        }
        msg[this.myManufacturer.length + 1] = this.myModel;
        for (int i = 0; i < data.length; i++) {
            msg[i + this.myManufacturer.length + 2] = data[i];
        }
        msg[msg.length - 1] = 0xF7;
        try {
            this.myOutput.send(msg, 0, msg.length);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

   public boolean sendBankChangeMsb(byte bank) {
         return sendControlChange(CC_BANK_MSB, bank);
    }

    public boolean sendBankChangeLsb(byte bank) {
         return sendControlChange(CC_BANK_LSB, bank);
    }
}
