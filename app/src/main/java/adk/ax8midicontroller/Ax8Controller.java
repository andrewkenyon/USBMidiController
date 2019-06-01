package adk.ax8midicontroller;

/**
 * Created by Andrew on 01/07/2019.
 */
public class Ax8Controller {
    MidiConnection myConnection;

    byte myChannel = 0;

    static final byte[] MANUFACTURER_ID = {0x00, 0x01, 0x74};
    static final byte MODEL_ID = 0x08;

    private static final byte headerChecksum = 0x8d;

    public Ax8Controller() {
        this.myConnection = MidiConnection.getInstance();
    }

    public boolean sendBankAndProgramChange(short preset) {
        return this.myConnection.sendBankChangeMsb(this.myChannel, preset / 128) 
            && this.myConnection.sendProgramChange(this.myChannel, preset % 128);
    }

    private byte calculateChecksum(byte[] body) {
        byte checksum = headerChecksum;
        for (int i = 0; i < body.length; i++) {
            checksum = checksum ^ body[i];
        }
        return (checksum & 0x7F);
    }
}
