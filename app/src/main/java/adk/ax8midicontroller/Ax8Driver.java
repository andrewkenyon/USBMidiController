package adk.ax8midicontroller;

/**
 * Created by Andrew on 01/07/2019.
 */
public class Ax8Driver {
    MidiDriver myMidi;

    byte myChannel = 0;

    public Ax8Driver() {
        this.myMidi = MidiDriver.getInstance();
    }

    public boolean sendBankAndProgramChange(short preset) {
        return this.myMidi.sendBankChangeMsb(this.myChannel, preset / 128) 
            && this.myMidi.sendProgramChange(this.myChannel, preset % 128);
    }
}
