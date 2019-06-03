package adk.ax8midicontroller;

/**
 * Created by Andrew on 01/07/2019.
 */
public class FractalController {
    MidiController myMidi;

    byte myChannel = 0;

    static final byte[] MANUFACTURER_ID = {0x00, 0x01, 0x74};
    static final byte MODEL_ID = 0x08;

    private static final byte headerChecksum = 0x8d;

    private static final byte GET_PRESET_NUMBER = 0x14;

    public FractalController(MidiController midi) {
        this.myMidi = midi;
    }

    public boolean sendBankAndProgramChange(short preset) {
        return this.myDriver.sendBankChangeMsb(this.myChannel, preset / 128) 
            && this.myDriver.sendProgramChange(this.myChannel, preset % 128);
    }

    private byte appendChecksum(byte[] body) {
        byte[] newBody = new byte[body.length + 1];
        byte checksum = headerChecksum;
        for (int i = 0; i < body.length; i++) {
            newBody[i] = body[i];
            checksum = checksum ^ body[i];
        }
        newBody[body.length] = checksum & 0x7F;
        return newBody;
    }

    public boolean getPresetNumber() {
        byte[] body = {0x14};
        return this.myDriver.sendSysExMessage(
            MANUFACTURER_ID,
            MODEL_ID,
            this.appendChecksum(body)
        );
    }

    public boolean setPresetNumber(short preset) {
        byte[] body = {0x3C, preset / 128, preset % 128};
        return this.myDriver.sendSysExMessage(
            MANUFACTURER_ID,
            MODEL_ID,
            this.appendChecksum(body)
        );
    }
}
