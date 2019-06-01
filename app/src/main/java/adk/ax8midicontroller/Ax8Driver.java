class Ax8Driver {
    private ourInstance = new Ax8Driver ();

    private ourMidiDriver;

    private Ax8Driver () {
    }

    public Ax8Driver getInstance() {
        return ourInstance;
    }

    public void init(Context context) {
    }
}
