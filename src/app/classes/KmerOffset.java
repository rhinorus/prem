package app.classes;

/**
 * Representation of the k-mer offset in sequence.
 */
public class KmerOffset{

    private Integer offset;
    private Primer primer;

    //============================
    //  Constructors
    //============================

    /**
     * Creates a object by given offset and primer.
     * @param offset - k-mer offset in primer sequence
     * @param primer
     */
    public KmerOffset(Integer offset, Primer primer){
        this.primer = primer;
        this.offset = offset;
    }

    //============================
    //  Properties
    //============================ 
 
    public Integer getOffset(){
        return offset;
    }
 
    public Primer getPrimer(){
        return primer;
    }
}