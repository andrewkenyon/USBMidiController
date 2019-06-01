package adk.ax8midicontroller;

/**
 * Created by Andrew on 01/07/2019.
 */
public class Ax8Driver {
    Context ourContext;

    MidiDriver myMidi;

    public Ax8Driver() {
        this.myMidi = MidiDriver.getInstance();
    }

    public boolean sendBankAndProgramChange(short preset) {
        return this.myMidi.sendBankChangeMsb(preset / 128) && sendProgramChange(preset % 128);
    }
}
