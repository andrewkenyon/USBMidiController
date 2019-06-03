package adk.ax8midicontroller;

/**
 * Created by Andrew on 01/07/2019.
 */
public class MidiController {
    public MidiController(MidiDevice device) {

        myOutput = device.openInputPort(0);

        myInput = device.openOutputPort(0);
        myInput.connect(new MidiReceiver {
            public void onSend(byte[] data, int offset, int count, long timestamp) throws IOException {
                // parse MIDI or whatever
            }
        });
    }
 
}
