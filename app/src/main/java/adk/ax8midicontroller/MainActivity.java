package adk.ax8midicontroller;

import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView myConsole;

    MidiManager myManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.myConsole = ((TextView)this.findViewById(R.id.main_console));

        this.myManager = ((MidiManager) getSystemService(Context.MIDI_SERVICE));
        this.myManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
            public void onDeviceAdded(MidiDeviceInfo device) {
                refreshDevices();
            }

            public void onDeviceRemoved(MidiDeviceInfo device) {
                refreshDevices();
            }
        }, null);

        this.refreshDevices();
    }

    private void refreshDevices() {
        MidiDeviceInfo[] info = this.myManager.getDevices();
        this.myConsole.clearComposingText();

        if(info.length > 0) {
            for (int i = 0; i < info.length; i++) {
                this.myConsole.append(
                        "Device " + i + ": " +
                                info[i].getInputPortCount() + " inputs, " +
                                info[i].getOutputPortCount() + " outputs.\n"
                );
            }
            this.myManager.openDevice(info[0], new MidiManager.OnDeviceOpenedListener() {
                public void onDeviceOpened(MidiDevice device) {
                    myConsole.append("Connected to device!");
                }
            }, null);
        } else {
            this.myConsole.setText("No info detected");
        }
    }

}