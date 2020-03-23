package app.classes;

/**
 * Representation of the primer.
 */
public class Primer{

    private String sequence;
    private PrimerType type;
    private Primer pair;
    private char[] sequenceChars;

    //==========================
    // Constructors
    //==========================

    /**
     * Creates an object by primer sequence and direction type.
     * 
     * @param sequence - primer sequence
     * @param type - primer direction type. (Forward or Reverse)
     */
    public Primer(String sequence, PrimerType type) { 
        this.sequence = sequence;
        this.type = type;
        this.sequenceChars = sequence.toCharArray();
    }

    //==========================
    // Properties
    //==========================

    public String getSequence() {
        return sequence;
    }

    public PrimerType getType() {
        return type;
    }

    public Primer getPair() {
        return pair;
    }

    public void setPair(Primer pair) {
        this.pair = pair; 
    }

    public char[] getSequenceChars() {
        return sequenceChars;
    }

    /**
     * The primer direction type - Forward or Reverse.
     */
    enum PrimerType {
        Forward,
        Reverse;

        PrimerType() { }
    }

    @Override
    public String toString() {
        return sequence;
    }

}