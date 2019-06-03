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


    public boolean sendProgramChange(byte channel, byte program) {
        try {
            this.myOutput.send(new byte[]{(byte) (0xC0 + channel), program}, 0, 2);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean sendControlChange(byte channel, byte control, byte value) {
        try {
            this.myOutput.send(new byte[]{(byte) (0xB0 + channel), control, value}, 0, 3);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean sendSysExMessage(byte[] manufacturer, byte model, byte[] data) {
        byte[] msg = new byte[data.length + manufacturer.length +3]
        msg[0] = 0xF0;
        for (int i = 0; i < manufacturer.length; i++) {
            msg[i + 1] = manufacturer[i];
        }
        msg[manufacturer.length + 1] = model;
        for (int i = 0; i < data.length; i++) {
            msg[i + manufacturer.length + 2] = data[i];
        }
        msg[msg.length - 1] = 0xF7;
        try {
            this.myOutput.send(msg, 0, msg.length);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean sendBankChangeMsb(byte channel, byte bank) {
         return sendControlChange(channel, CC_BANK_MSB, bank);
    }

    public boolean sendBankChangeLsb(byte channel, byte bank) {
         return sendControlChange(channel, CC_BANK_LSB, bank);
    }
