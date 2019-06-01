package adk.ax8midicontroller;

import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MidiConnection myConnection;

    Ax8Controller myController;

    TextView myConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.myConsole = ((TextView)this.findViewById(R.id.main_console));

        this.myConnection = MidiDriver.getInstance();
        this.myConnection.init(this);
        this.myConnection.connect(new Runnable() {
            public void run() {
                myConsole.setText(myConnection.getDeviceInfo());
                myConsole.append("Connected!");
                myController = new Controller();
            }
        });
    }

}
