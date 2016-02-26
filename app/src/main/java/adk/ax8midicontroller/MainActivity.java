package adk.ax8midicontroller;

import android.content.Context;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {

    MidiManager myManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.myManager = ((MidiManager) getSystemService(Context.MIDI_SERVICE));

        this.myManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
            public void onDeviceAdded(MidiDeviceInfo info) {
                refreshInfo();
            }

            public void onDeviceRemoved(MidiDeviceInfo info) {
                refreshInfo();
            }
        }, null);

        this.refreshInfo();
    }

    private void refreshInfo() {
        MidiDeviceInfo[] info = this.myManager.getDevices();
        if(info.length > 0) {
            for (int i = 0; i < info.length; i++) {
                ((TextView)this.findViewById(R.id.main_console)).setText(
                    "Device " + i + ": " +
                    info[i].getInputPortCount() + " inputs, " +
                    info[i].getOutputPortCount() + " outputs."
                );
            }
        } else {
            ((TextView)this.findViewById(R.id.main_console)).setText("No devices detected");
        }
    }

}

