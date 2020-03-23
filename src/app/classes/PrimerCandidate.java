package app.classes;

/**
 * Representation of the primer, which could be found in the read sequence.
 */
public class PrimerCandidate{

    private Primer primer;
    private int count;

    //=========================
    // Constructors
    //=========================

    /**
     * Creates a primer candidate.
     * @param primer
     */
    public PrimerCandidate(Primer primer){
        this.primer = primer;
    }

    //=========================
    // Properties
    //=========================

    public Primer getPrimer(){
        return primer;
    }

    public int getCount(){
        return count;
    }

    //=========================
    // Other methods
    //=========================
 
    public void incrementCount(){
        count++;
    }

}