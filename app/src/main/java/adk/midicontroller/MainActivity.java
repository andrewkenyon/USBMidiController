package adk.midicontroller;

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

    MidiDriver myDriver;

    FractalController myController;

    TextView myConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.myConsole = ((TextView)this.findViewById(R.id.main_console));

        this.myDriver = MidiDriver.getInstance();
        this.myDriver.init(this);
        this.myDriver.connect(new Runnable() {
            public void run() {
                myConsole.setText(myDriver.getDeviceInfo());
                myConsole.append("Connected!");
                myController = new Controller();
            }
        });
    }

}
