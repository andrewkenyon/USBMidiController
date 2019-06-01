package adk.ax8midicontroller;

/**
 * Created by Andrew on 01/07/2019.
 */
public class Ax8Controller {
    MidiConnection myConnection;

    byte myChannel = 0;

    public Ax8Controller() {
        this.myConnection = MidiConnection.getInstance();
    }

    public boolean sendBankAndProgramChange(short preset) {
        return this.myConnection.sendBankChangeMsb(this.myChannel, preset / 128) 
            && this.myConnection.sendProgramChange(this.myChannel, preset % 128);
    }
}
